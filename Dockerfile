FROM maven:3.6.0-jdk-8-alpine as builder

ARG env
ENV env $env

WORKDIR /workspace
ADD . /workspace
COPY settings.xml /root/.m2/
RUN mvn clean install -P bintray,${env} -T  1C -Dmaven.test.skip=true



FROM tomcat:8.0.20-jre8

RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY --from=builder /workspace/Controller/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
