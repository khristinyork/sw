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
import java.util.concurrent.ConcurrentHashMap;

//LSim logging system imports sgeag@2017
import lsim.worker.LSimWorker;
import recipes_service.communication.Host;
import recipes_service.data.AddOperation;
import recipes_service.tsae.data_structures.Timestamp;
import edu.uoc.dpcs.lsim.LSimFactory;
import edu.uoc.dpcs.lsim.logger.LoggerManager.Level;

/**
 * @author Joan-Manuel Marques December 2012
 *
 */
public class TimestampVector implements Serializable {
	// Needed for the logging system sgeag@2017
	private transient LSimWorker lsim = LSimFactory.getWorkerInstance();

	private static final long serialVersionUID = -765026247959198886L;
	/**
	 * This class stores a summary of the timestamps seen by a node. For each node,
	 * stores the timestamp of the last received operation.
	 */
    private String hostport;
    private Timestamp ts;
	private ConcurrentHashMap<String, Timestamp> timestampVector = new ConcurrentHashMap<String, Timestamp>();
	
	
	public ConcurrentHashMap<String, Timestamp> getTimestampVector() {
		return timestampVector;
	}

	public String getHostport() {
		return hostport;
	}

	public void setHostport(String hostport) {
		this.hostport = hostport;
	}

	public Timestamp getTs() {
		return ts;
	}

	public void setTs(Timestamp ts) {
		this.ts = ts;
	}

	public TimestampVector(List<String> participants) {
		// create and empty TimestampVector
		for (Iterator<String> it = participants.iterator(); it.hasNext();) {
			String id = it.next();
			// when sequence number of timestamp < 0 it means that the timestamp is the null
			// timestamp
			setHostport(id);
			setTs(new Timestamp(getHostport(), Timestamp.NULL_TIMESTAMP_SEQ_NUMBER));
			timestampVector.put(getHostport(), getTs());
		}
	}

	/**
	 * Updates the timestamp vector with a new timestamp.
	 * 
	 * @param timestamp
	 */
	public synchronized void updateTimestamp(Timestamp timestamp) {
		lsim.log(Level.TRACE, "Updating the TimestampVectorInserting with the timestamp: " + timestamp);
		//si el timestamp es negativo es lo mismo que nulo
		 if (!timestamp.isNullTimestamp()) {
	            this.timestampVector.replace(timestamp.getHostid(), timestamp);
	        }
	}

	/**
	 * merge in another vector, taking the elementwise maximum
	 * 
	 * @param tsVector (a timestamp vector)
	 */
	public synchronized void updateMax(TimestampVector tsVector) {
		/*recorremos nuestro local timestampVector comparando
		los nodos que tenemos con los nodos de tsVector y si para
		el mismo nodo el tsVector tiene un valor de timestamp mayor
		reemplazamos el valor de timestamp de nuestro nofo por el valor de tsvector*/
		for (String node : this.timestampVector.keySet()) {
            Timestamp tsVectorTimestamp = tsVector.getLast(node);

            if (tsVectorTimestamp == null) {
                continue;
            } else if (this.getLast(node).compare(tsVectorTimestamp) < 0) {
                this.timestampVector.replace(node, tsVectorTimestamp);
            }
        }
	}

	/**
	 * 
	 * @param node
	 * @return the last timestamp issued by node that has been received.
	 */
	public Timestamp getLast(String node) {
		 return this.timestampVector.get(node);		
	}

	/**
	 * merges local timestamp vector with tsVector timestamp vector taking the
	 * smallest timestamp for each node. After merging, local node will have the
	 * smallest timestamp for each node.
	 * 
	 * @param tsVector (timestamp vector)
	 */
	public synchronized void mergeMin(TimestampVector tsVector) {
	}

	/**
	 * Copia del objeto con son mismos valores en el momento que se realiza la copia
	 * no tiene la misma referencia de memoria
	 * clone
	 */
	public TimestampVector clone() {
		List<String> participants=null;
		for (String node : this.timestampVector.keySet()) {
			participants.add(node);
		}
		return new TimestampVector(participants);
	}

	/**
	 * Método equals implementa una relacción de equivalencia en referencia a
	 * objetos que no son nulos 
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimestampVector other= (TimestampVector)obj;
//		Host other = (Host) obj;
		if(timestampVector==null) {
			if(other.getTimestampVector()!=null)
				return false;
		}else if (!timestampVector.equals(other.getTimestampVector())) {
			return false;
		}
		return true;
	}

	/**
	 * toString
	 */
	@Override
	public synchronized String toString() {
		String all = "";
		if (timestampVector == null) {
			return all;
		}
		for (Enumeration<String> en = timestampVector.keys(); en.hasMoreElements();) {
			String name = en.nextElement();
			if (timestampVector.get(name) != null)
				all += timestampVector.get(name) + "\n";
		}
		return all;
	}
}