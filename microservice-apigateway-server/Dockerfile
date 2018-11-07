FROM openjdk:8-jre
MAINTAINER MVTM sawied danan.2009@gmail.com

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
ADD target/lib           /usr/share/service/lib

# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/service/${JAR_FILE}

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/service/${JAR_FILE}.jar"]