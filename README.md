# Microservice in practice

This document intend to introduce core concepts and demonstrating how to build a typical service components,include oauth2, Application, API gateway, configuration center.

```
mvn release:prepare -Darguments="-DskipTests"
mvn release:perform -Darguments="-DskipTests"
```

> As a sample, we focus on CentOs system  as test environment, and virtualbox as virtual machine.

#### Change Ubuntu server ip address ####

modify the file /etc/netplan/*.yaml as following

```
network:
    ethernets:
        enp0s3:
            addresses: [192.168.88.107/24]
            dhcp4: no
            gateway4: 192.168.88.1
    version: 2

```
do apply new settings:
```
$ sudo netplan apply
```

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
```bash
$ sudo apt-get remove docker docker-engine docker.io containerd runc
```
* Install using the repository
install pakages to allow apt to use a repository over HTTPS
```bash
$ sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common
```
* Add Docker’s official GPG key:
```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
```
* Add stable repository 
```bash
sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
```

 * Install docker 

```bash
$ sudo apt-get install docker-ce docker-ce-cli containerd.io

```

#### Install Docker Compose  ####
1.  Run this command to download the latest version of Docker Compose:
```bash
sudo curl -L "https://github.com/docker/compose/releases/download/1.23.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
```
2. Apply executable permissions to the binary:
```bash
chmod +x /usr/local/bin/docker-compose
```


#### Install MongoDB document database
1. pull latest mongo image from docker hub
```bash
docker pull mongo
```
and remember run with a one-node replica set, after startup mongoDB via docker-compose script that set the default admin and admin password.
then sign in mongoDB console. initiate DB with replication model. mongo

mongod --repSet devRepSet

Input mongo enter mongo console and initiate db,then:
```javascript
use admin;
db.auth('usename','password');
rs.initiate();
cf=rs.conf();
cf.members[0].host="mongo-cc:27017";
rs.reconfig(cf);
quit();
```
2. add user and create database
```javascript
db.createUser(
  {
    user: "callcenter",
    pwd: "elastic",
    roles: [
       { role: "readWrite", db: "mongodb_es" }
    ]
  }
)

//admin use has been added in docker-compose script,so need not do here.
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

#### Install mongo-connector: ####
```bash
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
```bash
sudo docker pull elasticsearch:6.6.1
```


#### Install python ####
pyenv lets you easily switch between multiple versions of Python. It's simple, unobtrusive, and follows the UNIX tradition of single-purpose tools that do one thing well.
1. install pyenv to manage python version
```bash
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
sudo supervisorctl -c /etc/supervisor/supervisord.conf reload
sudo supervisorctl -c /etc/supervisor/supervisord.conf stop all
sudo supervisorctl -c /etc/supervisor/supervisord.conf start all
```


check supervisord process if exist
stop supervisord 
check logs
```

ps -ef | grep supervisord
sudo kill -HUP $(cat /var/run/supervisord.pid)
tail -f /var/log/call_center.out.log
```

start supervisord when system startup
create init.d script in folder
``` 
sudo vi /etc/init.d/supervisord
```
and input following:
```
#! /bin/sh
#
# Downloaded from:
# http://bazaar.launchpad.net/~ubuntu-branches/ubuntu/trusty/supervisor/trusty/view/head:/debian/supervisor.init
#
# skeleton      example file to build /etc/init.d/ scripts.
#               This file should be used to construct scripts for /etc/init.d.
#
#               Written by Miquel van Smoorenburg <miquels@cistron.nl>.
#               Modified for Debian
#               by Ian Murdock <imurdock@gnu.ai.mit.edu>.
#               Further changes by Javier Fernandez-Sanguino <jfs@debian.org>
#               Modified by sbilly <superli.1980@gmail.com> Added supervisorctl to status
#
# Version:      @(#)skeleton  1.9  26-Feb-2001  miquels@cistron.nl
#
### BEGIN INIT INFO
# Provides:          supervisor
# Required-Start:    $remote_fs $network $named
# Required-Stop:     $remote_fs $network $named
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start/stop supervisor
# Description:       Start/stop supervisor daemon and its configured
#                    subprocesses.
### END INIT INFO

. /lib/lsb/init-functions

PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin
DAEMON=/usr/local/bin/supervisord
SUPERVISORCTL=/usr/local/bin/supervisorctl
NAME=supervisord
DESC=supervisor

test -x $DAEMON || exit 0

LOGDIR=/var/log/supervisor
PIDFILE=/var/run/$NAME.pid
DODTIME=5                   # Time to wait for the server to die, in seconds
                            # If this value is set too low you might not
                            # let some servers to die gracefully and
                            # 'restart' will not work

# Include supervisor defaults if available
if [ -f /etc/default/supervisor ] ; then
        . /etc/default/supervisor
fi
DAEMON_OPTS="-c /etc/supervisor/supervisord.conf $DAEMON_OPTS"

set -e

running_pid()
{
    # Check if a given process pid's cmdline matches a given name
    pid=$1
    name=$2
    [ -z "$pid" ] && return 1
    [ ! -d /proc/$pid ] &&  return 1
    (cat /proc/$pid/cmdline | tr "\000" "\n"|grep -q $name) || return 1
    return 0
}

running()
{
# Check if the process is running looking at /proc
# (works for all users)

    # No pidfile, probably no daemon present
    [ ! -f "$PIDFILE" ] && return 1
    # Obtain the pid and check it against the binary name
    pid=`cat $PIDFILE`
    running_pid $pid $DAEMON || return 1
    return 0
}

force_stop() {
# Forcefully kill the process
    [ ! -f "$PIDFILE" ] && return
    if running ; then
        kill -15 $pid
        # Is it really dead?
        [ -n "$DODTIME" ] && sleep "$DODTIME"s
        if running ; then
            kill -9 $pid
            [ -n "$DODTIME" ] && sleep "$DODTIME"s
            if running ; then
                echo "Cannot kill $NAME (pid=$pid)!"
                exit 1
            fi
        fi
    fi
    rm -f $PIDFILE
    return 0
}

case "$1" in
  start)
        echo -n "Starting $DESC: "
        start-stop-daemon --start --quiet --pidfile $PIDFILE \
                --startas $DAEMON -- $DAEMON_OPTS
        test -f $PIDFILE || sleep 1
        if running ; then
            echo "$NAME."
        else
            echo " ERROR."
        fi
        ;;
  stop)
        echo -n "Stopping $DESC: "
        start-stop-daemon --stop --quiet --oknodo --pidfile $PIDFILE
        echo "$NAME."
        ;;
  force-stop)
        echo -n "Forcefully stopping $DESC: "
        force_stop
        if ! running ; then
            echo "$NAME."
        else
            echo " ERROR."
        fi
        ;;
  #reload)
        #
        #       If the daemon can reload its config files on the fly
        #       for example by sending it SIGHUP, do it here.
        #
        #       If the daemon responds to changes in its config file
        #       directly anyway, make this a do-nothing entry.
        #
        # echo "Reloading $DESC configuration files."
        # start-stop-daemon --stop --signal 1 --quiet --pidfile \
        #       /var/run/$NAME.pid --exec $DAEMON
  #;;
  force-reload)
        #
        #       If the "reload" option is implemented, move the "force-reload"
        #       option to the "reload" entry above. If not, "force-reload" is
        #       just the same as "restart" except that it does nothing if the
        #   daemon isn't already running.
        # check wether $DAEMON is running. If so, restart
        start-stop-daemon --stop --test --quiet --pidfile $PIDFILE \
        --startas $DAEMON \
        && $0 restart \
        || exit 0
        ;;
  restart)
    echo -n "Restarting $DESC: "
    start-stop-daemon --stop --quiet --oknodo --pidfile $PIDFILE
        [ -n "$DODTIME" ] && sleep $DODTIME
        start-stop-daemon --start --quiet --pidfile $PIDFILE \
                --startas $DAEMON -- $DAEMON_OPTS
        echo "$NAME."
        ;;
  status)
    echo -n "$NAME is "
    if running ;  then
        echo "running"
    else
        echo " not running."
        exit 1
    fi
    $SUPERVISORCTL $DAEMON_OPTS status
    ;;
  *)
        N=/etc/init.d/$NAME
        # echo "Usage: $N {start|stop|restart|reload|force-reload}" >&2
        echo "Usage: $N {start|stop|restart|force-reload|status|force-stop}" >&2
        exit 1
        ;;
esac

exit 0

```
and then
```
sudo chmod +x /etc/init.d/supervisord
sudo update-rc.d supervisord defaults
sudo /etc/init.d/supervisord start
sudo /etc/init.d/supervisord stop
```

Spring boot application autostartup script for supervisord, Assume you have a spring boot application, you want to startup with operator system,
create a ini configuration file in /etc/supervisor/application.ini, input text as following:
```
[program:call-center]
command =java -Xmx1024m -jar -Dspring.profiles.active=en  callcenter-0.0.1-SNAPSHOT.jar
directory=/home/sawied/cc/run/callcenter
user=sawied
stopsignal = TERM
autostart = true
autorestart = true
stderr_logfile=/var/log/call_center.err.log
stdout_logfile=/var/log/call_center.out.log

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
mvn clean package -Dmaven.test.skip=true
cp /home/sawied/githome/core/callCenter_hsbc/target/callcenter-0.0.1-SNAPSHOT.jar /home/sawied/cc/run/callcenter

vi src/main/java/com/github/sawied/azure/speech/App.java
vi src/main/java/com/github/sawied/azure/speech/Main.java
```
replace text in vim editor
```
:%s/lemon/orange/
```
download file via curl
curl -L -o file url