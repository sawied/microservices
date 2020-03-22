### Kubernetes Installation
The following step aim to demonstrate how to setup a Kubernetes cluster in local with ***VirtualBox***. 
 ***Before your beginning :***
 
* Ubuntu 16.04+
* 2 GB or more of RAM per machine
* 2 CPUs or more
* Full network connectivity between all machines in the cluster (public or private network is fine)
* Unique hostname, MAC address, and product_uuid for every node.
* Swap disabled. You MUST disable swap in order for the kubelet to work properly


###Creating Virtual machine
 Install an Ubuntu Operating System in VirtualBox , 
named ***master-k8s***, with ***2GB RAM and 2 Cores*** (at least). It's better to use ***NAT*** network adapter(create a NAT network before you installing
) so that we are able to get a constant ip address.

###Install Docker and Kubernetes

* Disable SELinux if system has installed
  ```bash
   setenforce 0
   ```
* Disable firewall
   ```
   sudo ufw disable
   ```
* Disable Swap 
comment the line contain swap in file '/etc/fstab' so that can take effect permanently.
   ```sh
   swapoff -a && sed -i '/swap/s/^/#/' /etc/fstab
    ```
* Enable net.bridge.bridge-nf-call-iptables core option
   ```sh
   tee /etc/sysctl.d/k8s.conf <<-'EOF'
   net.bridge.bridge-nf-call-ip6tables = 1
   net.bridge.bridge-nf-call-iptables = 1
   EOF
   ```
* Enable packet forwarding for IPv4 in /etc/sysctl.conf
   just uncomment the above line:
   ```
   net.ipv4.ip_forward = 1
   ```
   
#####Install docker CE for Ubuntu

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
    $ sudo apt-get install docker-ce
    ```

* change the default Cgroup drivers and using aliyun to accelerate downloading image. you must change to your account. 
    ```sh
    cat > /etc/docker/daemon.json <<EOF
    {
      "registry-mirrors": ["https://assigned_account.mirror.aliyuncs.com"],
      "exec-opts": ["native.cgroupdriver=systemd"],
      "log-driver": "json-file",
      "log-opts": {
        "max-size": "100m"
      },
      "storage-driver": "overlay2"
    }
    EOF
    # Restart docker.
    systemctl daemon-reload
    systemctl enable docker 
    systemctl restart docker
    ```
  
##### persistence ip address 
let's us to use static ip address instead of dynamic address, edit file in /etc/netplan 
remember changing ip address in second machine.
```
network:
    ethernets:
        enp0s3:
            addresses: [10.0.2.5/24]
            gateway4: 10.0.2.1
            nameservers:
               addresses: [114.114.114.114]
            dhcp4: true
    version: 2
```



#####Install Kubernetes for Ubuntu

* add Kubernetes deb mirrors source into APT repository:

  **Note: we added aliyun mirrors source here in order to download dependency packages available.** 
    ```sh
    apt-get update && apt-get install -y apt-transport-https
    curl https://mirrors.aliyun.com/kubernetes/apt/doc/apt-key.gpg | apt-key add - 
    cat <<EOF >/etc/apt/sources.list.d/kubernetes.list
    deb https://mirrors.aliyun.com/kubernetes/apt/ kubernetes-xenial main
    EOF
    ```
* install kubernetes

    ```
    apt-get update
    apt-get install -y kubelet kubeadm kubectl kubernetes-cni
    systemctl start kubelet
    systemctl status kubelet
    systemctl enable kubelet
    systemctl restart kubelet
    ```
* list the config images
    ```bash
    kubeadm config images list
    
    k8s.gcr.io/kube-apiserver:v1.17.4
    k8s.gcr.io/kube-controller-manager:v1.17.4
    k8s.gcr.io/kube-scheduler:v1.17.4
    k8s.gcr.io/kube-proxy:v1.17.4
    k8s.gcr.io/pause:3.1
    k8s.gcr.io/etcd:3.4.3-0
    k8s.gcr.io/coredns:1.6.5
   ```
* using bash script pullK8sImages.sh to download config images

    ```bash
    cat <<'EOF'>./pullK8sImages.sh
    #!/bin/bash
     images=(kube-proxy:v1.17.4 kube-scheduler:v1.17.4 kube-controller-manager:v1.17.4 kube-apiserver:v1.17.4 etcd:3.4.3-0 pause:3.1 coredns:1.6.5)
     for imageName in ${images[@]} ; do
       docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/$imageName
       docker tag registry.cn-hangzhou.aliyuncs.com/google_containers/$imageName k8s.gcr.io/$imageName
       docker rmi registry.cn-hangzhou.aliyuncs.com/google_containers/$imageName
     done
    EOF
    ```
    we just download the config images from aliyun and re-tagged with origin name. the downloaded images be removed after tagged.
    
    ```bash
    chmod +X pullK8sImages.sh
    bash pull pullK8sImages.sh
    ```
  

 ###Clone a new virtual machine from master
 So for we have installed all the packages for kubernetes. then we could clone a new machine as Kubernetes node.we don't want to install all the packages in a new machine again. 
 Stop the master virtual machine.
```bash
shutdown now
```

when clone a new machine make sure using ***Full Clone mode***, and generate all network adapter ***MAC Address**. 
named the node machine as ***node1-k8s***.

after the node machine clone completed,start it and change hostname 

```
hostnamectl --static set-hostname node1-k8s
```

Make sure the host name of second machine has been changed successfully, sometime it doesn't take effect due to
***cloud-init***  package is installed, To check if the package is installed run the following:
```
ls -l /etc/cloud/cloud.cfg
```
if it returns file, then edit it , change the ***preserve_hostname** to true
```
preserve_hostname: true
```

###Using kubeadm to config master node

come back master node to config master node,this will create a Kubernetes cluster,and start some system containers.

```
 kubeadm init
``` 
after then,  If everything goes right,we will get the message as following :

```
You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  https://kubernetes.io/docs/concepts/cluster-administration/addons/

Then you can join any number of worker nodes by running the following on each as root:

kubeadm join 10.0.2.4:6443 --token j3miii.p5q8tnew96pggm6q \
    --discovery-token-ca-cert-hash sha256:c077177242196ff18617b1b172e39da6a2ad615d0e401e2215d4c11fba3aaf2c
```

mark down  the output, because you will use that command to join a node. switch to node machine and execute the join node command

```
kubeadm join 10.0.2.4:6443 --token j3miii.p5q8tnew96pggm6q \
    --discovery-token-ca-cert-hash sha256:c077177242196ff18617b1b172e39da6a2ad615d0e401e2215d4c11fba3aaf2c
```

* check kubectl works or not, the following command prints the running pods
before do that ,export environment variable:
```
export KUBECONFIG=/etc/kubernetes/admin.conf
```
```
kubectl get po -n kube-system
```
* list nodes
```
kubectl get node
```
just now, you get the nodes but the status must be ***NotReady***.

* Configurate container network

Many container network plugin available , here we use Weave Net. install network plugin in master

```

kubectl apply -f "https://cloud.weave.works/k8s/net?k8s-version=$(kubectl version | base64 | tr -d '\n')"
```

after a while(depend your network), re-check node status,the status should be ***Ready***
```
kubectl get node
NAME         STATUS   ROLES    AGE   VERSION
master-k8s   Ready    master   20h   v1.17.4
node1-k8s    Ready    <none>   20h   v1.17.4

```  

***How to re-get a ip address***
```
dhclient -r
dhclient
```
* useful commands

```
kubectl describe node node1-k8s
systemctl  status kubelet
```
###Using local cluster
copy admin.conf into remote machine or local user home dir. and export environment variable.
```
mkdir -p ~/.kube/
sudo cp /etc/kubernetes/admin.conf  ~/.kube/config
echo "export KUBECONFIG=~/.kube/config" >> ~/.bash_profile
source ~/.bash_profile
```

* trying do deployment via local cluster
```bash
cat nginx-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
  labels:
    app: nginx
spec:
  replicas: 1
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.14.2
        ports:
        - containerPort: 80
```
* check nginx pod
```
kubectl get pods
NAME                                READY   STATUS    RESTARTS   AGE
nginx-deployment-574b87c764-2clkn   1/1     Running   0          2m49s
# enter nginx container
kubectl exec -it nginx-deployment-574b87c764-2clkn -- /bin/bash
# try to access nginx server
curl -v http://localhost
```


