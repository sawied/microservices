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
and remember run with a one-node replica set
```
mongod --repSet devRepSet
rs.initiate()
cf=rs.conf()
cf.members[0].host="mongo-cc:27017"
rs.reconfigj(cf)
```
2. add user and create database
```
db.createUser(
  {
    user: "callcenter",
    pwd: "elastic",
    roles: [
       { role: "readWrite", db: "mongodb_es" }
    ]
  }
)

db.createUser(
  {
    user: "root",
    pwd: "password",
    roles: [ { role: "userAdminAnyDatabase", db: "admin" } ]
  }
)
```

sudo apt-get install python3.5
admin user:root/rootpwd
mongo-connector

run spring boot project
```
mvn spring-boot:run -Dsring-boot.run.profiles=dev
```

install mongo-connector:
```
curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
python get-pip.py
sudo apt-get install python-pip net-tools curl
sudo pip install mongo-connector[elastic5] 
```
try to expose mongo data to elasticsearch engine:
mongo-connector -m mongodb://root:rootpwd@localhost:27017 -t elasticsearch-cc:9200 -d elastic2_doc_manager
mongo-connector -c mongo-connector-conf.json
#### Install elasticsearch 
1. pull the latest image from docker hub
```
sudo docker pull elasticsearch:6.6.1
```


#### Install python ####
pyenv lets you easily switch between multiple versions of Python. It's simple, unobtrusive, and follows the UNIX tradition of single-purpose tools that do one thing well.
1. install pyenv to manage python version
```
$ curl https://pyenv.run | bash
$ echo 'export PYENV_ROOT="$HOME/.pyenv"' >> ~/.bash_profile
$ echo 'export PATH="$PYENV_ROOT/bin:$PATH"' >> ~/.bash_profile
```
2. install python 3.7
```
sudo apt-get install -y make build-essential libssl-dev zlib1g-dev libbz2-dev \
libreadline-dev libsqlite3-dev wget curl llvm libncurses5-dev libncursesw5-dev \
xz-utils tk-dev libffi-dev liblzma-dev python-openssl git
pyenv install 3.7.2
```
3. switch to version 3.7.2
```
pyenv global 3.7.2
pyenv versions
sudo apt install python-pip
```


#### ffmpeg ###
splict multiple audio track channel to single one, the following command means 
mapping channel 1 track to final0.wav file and channel 0 to final1.wav
```
ffmpeg -i final.wav -map_channel 0.0.1  -ac 1 final0.wav -map_channel 0.0.0 final1.wav
ffmpeg -y -i final.wav -map_channel 0.0.1  -ac 1 -ar 16k final0.wav -ac 1 -ar 16k -map_channel 0.0.0 final1.wav
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

Configuration File
The Supervisor configuration file is conventionally named supervisord.conf. It is used by both supervisord and supervisorctl. If either application is started without the -c option (the option which is used to tell the application the configuration filename explicitly), the application will look for a file named supervisord.conf within the following locations, in the specified order. It will use the first file it finds.

1. $CWD/supervisord.conf
2. $CWD/etc/supervisord.conf
3. /etc/supervisord.conf
4. /etc/supervisor/supervisord.conf (since Supervisor 3.3.0)
5. ../etc/supervisord.conf (Relative to the executable)
6. ../supervisord.conf (Relative to the executable)

Once you see the file echoed to your terminal, reinvoke the command as echo_supervisord_conf > /etc/supervisord.conf
copy default supervisord configration
modify default path
/tmp/supervisor.sock --> /var/run/supervisor.sock
/tmp/supervisord.log --> var/log/supervisor.log
/tmp/supervisord.pid --> /var/run/supervisor.pid
> remember to make unix_http_server.file program match supervisorctl.serverurl, and default supervisord run as root, don't 

```
sudo chmod 777 /run
sudo chmod 777 /var/log
sudo supervisord -c /etc/supervisor/supervisord.conf
sudo supervisorctl -c /etc/supervisor/supervisord.conf status
```
stop supervisord 
```
ps -ef | grep supervisord
sudo kill -HUP $(cat /var/run/supervisord.pid)
```

#### Run Ubuntu ####
1. pull the latest image
```
apt-get install git net-tools curl unzip
export PATH=$PATH:/usr/share/maven/bin
docker pull ubuntu
docker run -it -d --name azure-ubuntu ubuntu
docker attach azure-ubuntu
export PATH=$PATH:/usr/share/maven/bin

git checkout --track origin/dev
git config --global user.email "danan.2009@gmail.com"
git config --global user.name "sawied"
apt-get install build-essential libssl1.0.0 libasound2
mvn compile exec:java -Dexec.mainClass="com.github.sawied.azure.speech.App"
mvn exec:java -Dexec.mainClass="com.github.sawied.azure.speech.App"
mvn exec:java -Dexec.mainClass="com.github.sawied.azure.speech.Main"

vi src/main/java/com/github/sawied/azure/speech/App.java
vi src/main/java/com/github/sawied/azure/speech/Main.java
```
replace text in vim editor
```
:%s/lemon/orange/
```
download file via curl
curl -L -o file url