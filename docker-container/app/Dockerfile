FROM ubuntu:24.04

RUN apt update && apt install -y openjdk-21-jre

ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH
COPY ./app.jar /app/app.jar
COPY ./entrypoint.sh /app/entrypoint.sh
WORKDIR /app
RUN chmod +x /app/entrypoint.sh
RUN chmod +x /app/app.jar
ENTRYPOINT ["/app/entrypoint.sh"]
