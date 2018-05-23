# Software
## sublime
https://www.sublimetext.com/3
## ssh en centox cloudera OpenSSH Installations under CentOS Linux


To install the server and client type:
### yum -y install openssh-server openssh-clients

Start the service:
### chkconfig sshd on
### service sshd start

Make sure port 22 is opened:
### netstat -tulpn | grep :22
Firewall Settings

Edit /etc/sysconfig/iptables (IPv4 firewall),
### vi /etc/sysconfig/iptables

Add the line
-A RH-Firewall-1-INPUT -m state --state NEW -m tcp -p tcp --dport 22 -j ACCEPT

If you want to restict access to 192.168.1.0/24, edit it as follows:
-A RH-Firewall-1-INPUT -s 192.168.1.0/24 -m state --state NEW -p tcp --dport 22 -j ACCEPT

If your site uses IPv6, and you are editing ip6tables, use the line:
-A RH-Firewall-1-INPUT -m tcp -p tcp --dport 22 -j ACCEPT

Save and close the file. Restart iptables:
### service iptables restart
OpenSSH Server Configuration

Edit /etc/ssh/sshd_config, enter:
### vi /etc/ssh/sshd_config

To disable root logins, edit or add as follows:
PermitRootLogin no

Restrict login to user tom and jerry only over ssh:
AllowUsers tom jerry

Change ssh port i.e. run it on a non-standard port like 1235
Port 1235

Save and close the file. Restart sshd:
### service sshd restart

### Header 3

## SSH UBUNTU

Installation

Installation of the OpenSSH client and server applications is simple. To install the OpenSSH client applications on your Ubuntu system, use this command at a terminal prompt:

## sudo apt install openssh-client

To install the OpenSSH server application, and related support files, use this command at a terminal prompt:

## sudo apt install openssh-server

The openssh-server package can also be selected to install during the Server Edition installation process.
Configuration

You may configure the default behaviour of the OpenSSH server application, sshd, by editing the file /etc/ssh/sshd_config. For information about the configuration directives used in this file, you may view the appropriate manual page with the following command, issued at a terminal prompt:

## man sshd_config

There are many directives in the sshd configuration file controlling such things as communication settings, and authentication modes. The following are examples of configuration directives that can be changed by editing the /etc/ssh/sshd_config file.

Prior to editing the configuration file, you should make a copy of the original file and protect it from writing so you will have the original settings as a reference and to reuse as necessary.

Copy the /etc/ssh/sshd_config file and protect it from writing with the following commands, issued at a terminal prompt:

## sudo cp /etc/ssh/sshd_config /etc/ssh/sshd_config.original
## sudo chmod a-w /etc/ssh/sshd_config.original

The following are examples of configuration directives you may change:

    To set your OpenSSH to listen on TCP port 2222 instead of the default TCP port 22, change the Port directive as such:

    Port 2222

    To have sshd allow public key-based login credentials, simply add or modify the line:

    PubkeyAuthentication yes

    If the line is already present, then ensure it is not commented out.

    To make your OpenSSH server display the contents of the /etc/issue.net file as a pre-login banner, simply add or modify the line:

    Banner /etc/issue.net

    In the /etc/ssh/sshd_config file.

After making changes to the /etc/ssh/sshd_config file, save the file, and restart the sshd server application to effect the changes using the following command at a terminal prompt:

## sudo systemctl restart sshd.service

Many other configuration directives for sshd are available to change the server application's behavior to fit your needs. Be advised, however, if your only method of access to a server is ssh, and you make a mistake in configuring sshd via the /etc/ssh/sshd_config file, you may find you are locked out of the server upon restarting it. Additionally, if an incorrect configuration directive is supplied, the sshd server may refuse to start, so be extra careful when editing this file on a remote server.
SSH Keys

SSH keys allow authentication between two hosts without the need of a password. SSH key authentication uses two keys, a private key and a public key.

To generate the keys, from a terminal prompt enter:

## ssh-keygen -t rsa

This will generate the keys using the RSA Algorithm. During the process you will be prompted for a password. Simply hit Enter when prompted to create the key.

By default the public key is saved in the file ~/.ssh/id_rsa.pub, while ~/.ssh/id_rsa is the private key. Now copy the id_rsa.pub file to the remote host and append it to ~/.ssh/authorized_keys by entering:

## ssh-copy-id username@remotehost

Finally, double check the permissions on the authorized_keys file, only the authenticated user should have read and write permissions. If the permissions are not correct change them by:

## chmod 600 .ssh/authorized_keys

You should now be able to SSH to the host without being prompted for a password.

