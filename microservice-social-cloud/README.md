##### How to run this application

1. apply client id and secret from github , push to environment variable of running server
2. For Local running(mvn spring-boot:run), you can add the client Id and secret to the configuration of Spring boot maven plugin 
3. Add FQDN into hosts file so that browser can parse forwarding URL correctly
4. To Kubenetes environment ,suggest create secret and append env from created secret

```shell script
kubectl create namespace sawied
kubectl get ns
kubectl create secret generic git-secret --from-literal=clientId=[clientId] --from-literal=clientSecret=[clientSecret] --namespace sawied
kubectl get secrets
```

access [Project Info](http://localhost/actuator/info),and run in local
```shell script
mvn spring-boot:run
mvn release:prepare
mvn release:perform
mvn release:rollback
```

