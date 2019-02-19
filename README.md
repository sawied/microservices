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
 



#### Install Docker Compose  ####
