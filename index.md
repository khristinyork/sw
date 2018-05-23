# Software
## sublime
https://www.sublimetext.com/3
## ssh en centox cloudera OpenSSH Installations under CentOS Linux


To install the server and client type:
# yum -y install openssh-server openssh-clients

Start the service:
# chkconfig sshd on
# service sshd start

Make sure port 22 is opened:
# netstat -tulpn | grep :22
Firewall Settings

Edit /etc/sysconfig/iptables (IPv4 firewall),
# vi /etc/sysconfig/iptables

Add the line
-A RH-Firewall-1-INPUT -m state --state NEW -m tcp -p tcp --dport 22 -j ACCEPT

If you want to restict access to 192.168.1.0/24, edit it as follows:
-A RH-Firewall-1-INPUT -s 192.168.1.0/24 -m state --state NEW -p tcp --dport 22 -j ACCEPT

If your site uses IPv6, and you are editing ip6tables, use the line:
-A RH-Firewall-1-INPUT -m tcp -p tcp --dport 22 -j ACCEPT

Save and close the file. Restart iptables:
# service iptables restart
OpenSSH Server Configuration

Edit /etc/ssh/sshd_config, enter:
# vi /etc/ssh/sshd_config

To disable root logins, edit or add as follows:
PermitRootLogin no

Restrict login to user tom and jerry only over ssh:
AllowUsers tom jerry

Change ssh port i.e. run it on a non-standard port like 1235
Port 1235

Save and close the file. Restart sshd:
# service sshd restart

### Header 3


