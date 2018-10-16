/*
* Copyright (c) Joan-Manuel Marques 2013. All rights reserved.
* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
*
* This file is part of the practical assignment of Distributed Systems course.
*
* This code is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This code is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this code.  If not, see <http://www.gnu.org/licenses/>.
*/

package recipes_service.tsae.data_structures;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

//LSim logging system imports sgeag@2017
import edu.uoc.dpcs.lsim.LSimFactory;
import edu.uoc.dpcs.lsim.logger.LoggerManager.Level;
import lsim.worker.LSimWorker;
import recipes_service.tsae.data_structures.Log;
import recipes_service.tsae.data_structures.Timestamp;
import recipes_service.data.Operation;

/**
 * @author Joan-Manuel Marques, Daniel Lázaro Iglesias
 * December 2012
 *
 */
public class Log implements Serializable{
	// Needed for the logging system sgeag@2017
	private transient LSimWorker lsim = LSimFactory.getWorkerInstance();

	private static final long serialVersionUID = -4864990265268259700L;
	/**
	 * This class implements a log, that stores the operations
	 * received  by a client.
	 * They are stored in a ConcurrentHashMap (a hash table),
	 * that stores a list of operations for each member of 
	 * the group.
	 */
	private ConcurrentHashMap<String, List<Operation>> log= new ConcurrentHashMap<String, List<Operation>>();  

	public Log(List<String> participants){
		// create an empty log
		for (Iterator<String> it = participants.iterator(); it.hasNext(); ){
			log.put(it.next(), new Vector<Operation>());
		}
	}

	/**
	 * inserts an operation into the log. Operations are 
	 * inserted in order. If the last operation for 
	 * the user is not the previous operation than the one 
	 * being inserted, the insertion will fail.
	 * Las operaciones se insertan en orden se deb comprobar que
	 * la ultima operacion insertada en el host es no previa a
	 * la operacion que va a ser insertada, sino fuera asi no se inserta
	 * devuelve fallo
	 * @param op
	 * @return true if op is inserted, false otherwise.
	 */
	public boolean add(Operation op){
		String hostId = op.getTimestamp().getHostid();
		List<Operation> listOperations =this.log.get(hostId);
		Integer sizeOpe =listOperations.size();
		if(!listOperations.isEmpty()) {
			System.out.println("tamaño de listaOperaciones: "+sizeOpe);
			Timestamp lastTimestamp =((Operation) (listOperations.get(sizeOpe-1))).getTimestamp();
			long timestampDifference = op.getTimestamp().compare(lastTimestamp);
			if ((lastTimestamp == null && timestampDifference == 0)
	                || (lastTimestamp != null && timestampDifference >0)) {
	            this.log.get(hostId).add(op);
	            lsim.log(Level.TRACE, "Inserting into Log the operation: "+op);
	            return true;
	        }else {
	        	lsim.log(Level.WARN, " Try inserting into Log the operation: "+op+" with a ts "+op.getTimestamp()+" but the previos ts value "+ op.getTimestamp());
	        	return false;
	        }
		}else {
			this.log.get(hostId).add(op);
            lsim.log(Level.TRACE, "Inserting into Log the operation: "+op);
            return true;
        }
	}
	
	/**
	 * Checks the received summary (sum) and determines the operations
	 * contained in the log that have not been seen by
	 * the proprietary of the summary.
	 * Returns them in an ordered list.
	 * @param sum
	 * @return list of operations
	 */
	public List<Operation> listNewer(TimestampVector sum){
		
		List<Operation> listOperationsOrdered = new Vector();

        /**
         Recorremos todos los nodos del log y dentro de cada nodo todas sus operaciones
         comparamos su timestamp para ver si es mas nuevo que le ultimo timestamp del
         summary (TimeStampVector)
         */

        for (String node : this.log.keySet()) {
            List<Operation> operations = this.log.get(node);
            Timestamp timestampToCompare = sum.getLast(node);        	
            for (Operation op : operations) {
                if (op.getTimestamp().compare(timestampToCompare) > 0) {
                	listOperationsOrdered.add(op);
                }
            }
        }
        return listOperationsOrdered;
	}
	
	/**
	 * Removes from the log the operations that have
	 * been acknowledged by all the members
	 * of the group, according to the provided
	 * ackSummary. 
	 * @param ack: ackSummary.
	 */
	public void purgeLog(TimestampMatrix ack){
	}

	/**
	 * equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (!(obj instanceof Log)) {
            return false;
        }

        Log other = (Log) obj;

        if (this.log == other.log) {
            return true;
        } else if (this.log == null || other.log == null) {
            return false;
        } else {
            return this.log.equals(other.log);
        }
	}

	/**
	 * toString
	 */
	@Override
	public synchronized String toString() {
		String name="";
		for(Enumeration<List<Operation>> en=log.elements();
		en.hasMoreElements(); ){
		List<Operation> sublog=en.nextElement();
		for(ListIterator<Operation> en2=sublog.listIterator(); en2.hasNext();){
			name+=en2.next().toString()+"\n";
		}
	}
		
		return name;
	}
}