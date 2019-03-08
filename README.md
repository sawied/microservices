# Microservice in practice

This document intend to introduce core concepts and demonstrating how to build a typical service components,include oauth2, Application, API gateway, configuration center.

> As a sample, we focus on CentOs system  as test environment, and virtualbox as virtual machine.

### ***Section 1 build development environment***

#### Install Docker CE

1. Install the latest version of Docker CE, or go to the next step to install a specific version:
```bash
$ sudo yum install docker-ce
```
2. To install a specific version of Docker CE, list the available versions in the repo, then select and install:
```bash
$ yum list docker-ce --showduplicates | sort -r
```
> before you  begin installing, make sure you have remove old docker version installed in your system.
```
$ sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
``` 

and install required packages:
```
$ sudo yum install -y yum-utils \
  device-mapper-persistent-data \
  lvm2
```
,then configure YUM stable repository:
```
$ sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
```

after installation completed.
check docker version like this:
```
$ docker version 
Client:
 Version:      17.09.0-ce
 API version:  1.26 (downgraded from 1.32)
 Go version:   go1.8.3
 Git commit:   afdb6d4
 Built:        Tue Sep 26 22:39:28 2017
 OS/Arch:      linux/amd64

Server:
 Version:      1.13.1
 API version:  1.26 (minimum version 1.12)
 Go version:   go1.8.3
 Git commit:   774336d/1.13.1
 Built:        Wed Mar  7 17:06:16 2018
 OS/Arch:      linux/amd64
 Experimental: false
```

3. Install docker CE for Ubuntu

* remove old version docker
```
$ sudo apt-get remove docker docker-engine docker.io containerd runc
```
* Install using the repository
install pakages to allow apt to use a repository over HTTPS
```
$ sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common
```
* Add Docker’s official GPG key:
```
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
```
* Add stable repository 
```
sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
```

 * Install docker 

```
$ sudo apt-get install docker-ce docker-ce-cli containerd.io

```

#### Install Docker Compose  ####
1.  Run this command to download the latest version of Docker Compose:
```
sudo curl -L "https://github.com/docker/compose/releases/download/1.23.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
```
2. Apply executable permissions to the binary:
```
chmod +x /usr/local/bin/docker-compose
```


#### Install MongoDB document database
1. pull latest mongo image from docker hub
```
docker pull mongo
```

#### Install elasticsearch 
1. pull the latest image from docker hub
```
sudo docker pull elasticsearch:6.6.1
```

#### Install openldap as centre authentiation server

1. Fistly install server library via apt get
```
sudo apt install slapd ldap-utils
```
then, change the default domain
```
sudo dpkg-reconfigure slapd
```

search slapd config and base dn
```
sudo ldapsearch -Q -LLL -Y EXTERNAL -H ldapi:/// -b cn=config dn
ldapsearch -x -LLL -H ldap:/// -b dc=sawied,dc=top dn
```

#### Install Apache2 in ubuntu server

we can install apache2 server via package management tool apt, below is sample script to installing:
```
sudo apt-get install apache2
```
restart server:
```
sudo apachectl -k restart
```

#### Install openJDK 8 and mysql server in ubuntu server

> before installing mysql,adding the MySQL APT Repository
1. Go to the download page for MySQL repository at https://dev.mysql.com/downloads/repo/apt/.
2. Install the downloaded release package with following command,
```
sudo dpkg -i /PATH/VERSION-SPECIFIC-PACKAGE-NAME.deb
sudo apt-get update
sudo apt search mysql-server
```

```
sudo apt-get install openjdk-8-jdk mysql-server mysql-client
 ```
 print verbose information:
 ```
 mysqld --verbose --help
 mysqld --initialize-insecure
 sudo systemctl  stop mysql
 ```
 mysqld default options are read from the following file in given order:
 /etc/my.cnf /etc/mysql/my.cnf ~/.my.cnf
After installation , can find default password set in error log, if you want to initialize by yourself , run the command below to init database
```
 mysqld --initialize-insecure
```

 #### install Sonar server
 





#### Install supervisor###

supervisor is a client/server system that allows its users to control a number of processes on UNIX-like operating system .it was inspired by the following:
* Convenience
* simple
* centralized
* efficient
* compatible

##### supervisor components #####
* supervisord
* supervisorctl

> before you install supervisor,make sure python 2.4 or later has been installed ,but not any version of python 3.

type apt install python-pip
```
sudo apt install python-pip
sudo pip install supervisor
```
after installing has completed , run 
```
echo_supervisord_conf
```
this will print a "sample" supervisor configuration file to your terminal's stdout.

default 

