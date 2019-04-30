## Call center system installation guide ##

**System requirment**

>operating system: ubuntu 18 above and minimum disk size 30G 

audio:
Sample rate : 16000 Hz
Channels : 2
Bits per sample : 16
Bitrate : 512 kbps
Codec : PCM

---
#### Setup Docker CE ####

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

---
#### Install Docker Compose  ####
1.  Run this command to download the latest version of Docker Compose:
```
sudo curl -L "https://github.com/docker/compose/releases/download/1.23.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
```
2. Apply executable permissions to the binary:
```
sudo chmod +x /usr/local/bin/docker-compose
```
3. add use to group docker
```
sudo gpasswd -a sawied docker
```
---------
#### install java and useful tools  Azure speech dependencies ####

>Note: java for run Spring Boot Application, and Azure Speech client for java need some dependencies. and ffmpeg tool for audio converting.
```
sudo apt-get install -y openjdk-8-jdk git ffmpeg
sudo apt-get install build-essential libssl1.0.0 libasound2 wget

```
and copy resource from gitlab. include docker-compose script, mongo-connector Dockerfile. call-center main project build package.
```bash
scp -r -i /home/sawied/.ssh/id_rsa docker-compose.yml sawied@139.219.15.183:/home/sawied/cc

scp -r -i /home/sawied/.ssh/id_rsa /home/sawied/githome/core/callCenter_hsbc/target/callcenter-0.0.1-SNAPSHOT.jar sawied@42.159.117.250:/home/sawied/cc/run/callcenter
```
enter dirctory /home/sawied/cc, startup docker containers like this:
```bash
sudo docker-compose up -d
```
> and do that ,you maybe not the mongo-connector does not startup successfully, then do step for **mongoDB initiate**. add user for DB visiting.
you can add work user to group docker, so that remove sudo prefix.

package  exist package and extract it in remote machine.
```bash
sudo tar -zcvpf cc.tar.gz /cc
sudo tar -zxvf cc.tar.gz
```

#### Install MongoDB document database
1. pull latest mongo image from docker hub
```bash
docker pull mongo
```
and remember run with a one-node replica set, after startup mongoDB via docker-compose script that set the default admin and admin password.
then sign in mongoDB console. initiate DB with replication model. mongo

mongod --repSet devRepSet

Input **mongo** enter mongo console and initiate db,then:
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
import data


----
#### Install supervisor for Spring boot startup with system automatically ####

supervisor is a client/server system that allows its users to control a number of processes on UNIX-like operating system .it was inspired by the following:
* Convenience
* simple
* centralized
* efficient
* compatible

**supervisor components**
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

Once you see the file echoed to your terminal, reinvoke the command as
```
 echo_supervisord_conf > /etc/supervisor/supervisord.conf
 ```
copy default supervisord configration
modify default path

* /tmp/supervisor.sock --> /var/run/supervisor.sock
* /tmp/supervisord.log --> var/log/supervisor.log
* /tmp/supervisord.pid --> /var/run/supervisor.pid

> remember to make **unix_http_server.file** program match **supervisorctl.serverurl**, and default supervisord run as root, don't 

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

change dirctory owner and group:
```
sudo chown -R root supervisor  
sudo chgrp -R root supervisor
sudo chmod g-w supervisord
```

Spring boot application autostartup script for supervisord, Assume you have a spring boot application, you want to startup with operator system,
create a ini configuration file in /etc/supervisor/application.ini, input text as following:
```
[program:call-center]
command =java -Xmx1024m -jar  -Dspring.profiles.active=en  callcenter-0.0.1-SNAPSHOT.jar
directory=/home/sawied/cc/run/callcenter
user=root
stopsignal = TERM
autostart = true
autorestart = true
stderr_logfile=/var/log/call_center.err.log
stdout_logfile=/var/log/call_center.out.log

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
user=root
stopsignal = TERM
autostart = true
autorestart = true
stderr_logfile=/var/log/call_center.err.log
stdout_logfile=/var/log/call_center.out.log

```






---
#### install maven dependencies ####
enter dirctory the path that this file exist. and do command as following:
```
mvn install:install-file -DgroupId=iflytek -DartifactId=iflytek -Dversion=1.0 -DgeneratePom=true -Dpackaging=jar -Dfile=src/lib/lfasr-sdk-client-2.0.0.1005-jar-with-dependencies.jar
mvn install:install-file -DgroupId=msc -DartifactId=msc -Dversion=1.0 -DgeneratePom=true -Dpackaging=jar -Dfile=src/lib/Msc.jar
mvn install:install-file -DgroupId=jave -DartifactId=jave -Dversion=1.0 -DgeneratePom=true -Dpackaging=jar -Dfile=src/lib/jave-1.0.1.jar
mvn install:install-file -DgroupId=VCGClient -DartifactId=VCGClient -Dversion=1.0 -DgeneratePom=true -Dpackaging=jar -Dfile=src/lib/VCGClient-1.0.jar
mvn install:install-file -DgroupId=com.alibaba.idst -DartifactId=nls-service-sdk -Dversion=1.0 -DgeneratePom=true -Dpackaging=jar -Dfile=src/lib/nls-service-sdk.jar
```

#### run the application ####
```
java -Xmx1024m -jar -Dspring.profiles.active=en  callcenter-0.0.1-SNAPSHOT.jar
```

### supervisord installation and configuration ###

-------------
#### Azure resource manager ####

1. How to generate License from key pair. 

* First, prepare key pair . use openssl tool to create secret key pair
  ```bash
  openssl genrsa -out rsa.key 2018
  ```

  The default format of  openssl output is  PEM, convert private key format from PEM to pkcs8 that can be recognized by Java ,and export public key
  ```
  openssl pkcs8 -in rsa.key -inform PEM  -nocrypt -topk8 -outform PEM -out pkcs8-private.key
  openssl rsa -in rsa.key -pubout -out rsa_pub.key
  ```
  those key is used to generate License,


* The second, create Linux VM from Azure platform, login in Azure 

  Choose 'Ubuntu Server 18.04 LTS',and create VM, wait the VM creating    successfully.
 remember to input public key generated in step 1.
 
  then you will get a VM Server,and output information contains public    IP. use putty tool try to connect. 139.219.15.1**
 
 do the image setup. 

After all the facilities setup completed and successed, do Azure image certification.

1. download the tool from Azure offical site. refer [Azure](https://docs.azure.cn/zh-cn/articles/azure-marketplace/imagecertification) for more details.
2. fix scanned issue.

    The part issues and solutions:

    | name | description | solution |
    |-----|------|------|
    |Bash History|Bash history files should be cleared before creating the VM image|$ history -c  
    |Client Alive Interval|It is recommended to set ClientAliveInterval to 180.On the application need,it can be set between 30 to 235.If you are enabling the SSH for your end users this value must be set as explained|edit file /etc/ssh/sshd_config for parameter "ClientAliveInterval 180"

3. use Azure CLI to create custom Linux VM 

    Before creating image from Azure CLI,SSH to the VM and Deprovision the VM
    > **Note** 
    >the account and entire home directory will be deleted running this command.
    root password will be disabled. You will not be able to login as root.

    ```
    $ sudo waagent -deprovision+user -force
    $ history -c
    $ exit

    ```


    Install Azure CLI for your operatoring system, switch to corresponding region variable, 
    ```
    az cloud set -n AzureChinaCloud # for China
    az cloud set -n AzureCloud # for International
    az --version for version printing
    az login # will launch a browser for you to login.
    az vm deallocate --resource-group CC --name core-service
    az vm generalize --resource-group CC --name core-service
    az image create --resource-group CC --name core-service-image --source core-service
    ```
    create VM from the image
    ```
    az vm create --resource-group CC --name CALL-CENTER --image core-service-image --admin-username sawied --generate-ssh-keys
    ```

    >How to copy image from one Azure account from another account?
    >Firstly, create storage account in target Azure Account
    >
    >powershell script:
    >* set execution policy allow local ps1 script can be executing.
    >* Maybe you need to install AzureRM module
    >* get installed module as this: 
    >```
    >Get-Module PowerShellGet -list | Select-Object Name,Version,Path
    >Name          Version Path
    >----          ------- ----
    >PowerShellGet 1.0.0.1 C:\Program Files\WindowsPowerShell\Modules\PowerShellGet\1.0.0.1\PowerShellGet.psd1
    
    * install azureRM module, during the installing ,it will inquiry some question. 
    ```
    set-ExecutionPolicy RemoteSigned
    Install-Module AzureRM -AllowClobber
    ```

    Create VM from ARM template

   >**Important**
   >
   > While the Azure documentation is being updated to reflect the new module cmdlet names, articles      may still use the AzureRM commands. After installing the Az module, it's recommended that you      enable the AzureRM cmdlet aliases with **Enable-AzureRmAlias**. See the Migrate from AzureRM to Az      article for more details.
    

# About License #

Creating a license protecte CC from illegal using. 

1. creating a RSA key pair. and convert key format from PKCS1 to PKCS8, in order to this can use it directly.
```bash
 $ openssl genrsa -out rsa.key 2018
 $ openssl pkcs8 -in rsa.key -inform PEM  -nocrypt -topk8 -outform PEM -out pkcs8-private.key
```

2. extension for custom script executing.
visit: [VirtualMachine extension](https://docs.azure.cn/zh-cn/virtual-machines/extensions/custom-script-linux)

```bash
cat script | gzip -9 | base64 -w 0
```    

3. install the license and subscribe key like this

```bash
sudo bash /cc/script/azure_params_replace.sh 193b67f02ae94f9996bda218286a2f3c southeastasia Tl7OIQAAABYAAAACAAAABAAAAAZiaWxsYWJjZGVmAAAAHgAAAAsAAAAKZXhwaXJ5RGF0ZQAAAWwDXgbkAAAAAAAAARkAAAABAAAAEAAAAP1saWNlbnNlU2lnbmF0dXJlAzU2dpNywdMBBCU2PAL5ok0xhRiC1rc5Lou6jk4chO1e02QAEkjcZ+wkC/yqFMoa1NgTQ4y/xZL3BqtXzo+VuMvQhBwazqj0M5v/4/60R//jqXw6LLVRrOsoxBGjleMrJuGDUV8ZR6drm2wOGf+25WkToi20cfJ57h3ts5OWdxyv+jitGYf+pynC7cix/omBfauqOXTzmg+PhL8ng+QJEfqdrAosxaN/YKUJ9ahvnVzlabrGe1oVxP0nKyn0QSU+8ZwwRRNfwGs9en0Q+R2T+1Bh7y5CxedRRYrrQ/7uW4ms8ZjS1dgi3suZL3zy2u0hl0ysVzFcldDvh7nDCgAAACUAAAAMAAAACWxpY2Vuc2VpZJvuNifTntYYvV5cH9x3QgMAAAAAAAAAIgAAAAIAAAAPAAAAB3NpZ25hdHVyZURpZ2VzdFNIQS0yNTY=
```