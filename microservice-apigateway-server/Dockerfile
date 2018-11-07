FROM openjdk:8-jre
MAINTAINER MVTM sawied danan.2009@gmail.com

VOLUME /tmp

# Add the service itself
ARG DEPENDENCY=target/dependency

COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp", "app:app/lib/*","com.github.sawied.microservice.gateway.ApigatewayServerApplication"]