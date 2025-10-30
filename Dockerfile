FROM openjdk:17-jdk-slim
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/erp_epmapat-v0.1.jar
COPY ${JAR_FILE} erp_epmapat_be.jar
EXPOSE 9090

ENTRYPOINT ["java", "-jar", "erp_epmapat_be.jar"]