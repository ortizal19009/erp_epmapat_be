FROM tomcat:9.0-jdk17-temurin

ENV TZ=America/Guayaquil

RUN rm -rf /usr/local/tomcat/webapps/*

EXPOSE 8080
