FROM tomcat:9.0-jdk17-temurin

RUN apt-get update \
    && apt-get install -y --no-install-recommends postgresql-client tzdata \
    && ln -snf /usr/share/zoneinfo/America/Guayaquil /etc/localtime \
    && echo America/Guayaquil > /etc/timezone \
    && rm -rf /var/lib/apt/lists/*

ENV TZ=America/Guayaquil

RUN rm -rf /usr/local/tomcat/webapps/*

EXPOSE 8080
