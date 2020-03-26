# Pull base image
FROM tomcat:8.0-jre8

RUN rm -rf /usr/local/tomcat/webapps/*

COPY Controller/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

CMD ["catalina.sh", "run"]
