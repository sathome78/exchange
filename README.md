﻿##Установка и настройка среды
1. Install Java:
	1.	sudo apt-get install python-software-properties
	2.	sudo add-apt-repository ppa:webupd8team/java
	3.	sudo apt-get update
	4.	sudo apt-get install oracle-java8-installer
		If doesn't work, do this:
			1. cd /var/lib/dpkg/info
			2. sudo sed -i 's|JAVA_VERSION=8u161|JAVA_VERSION=8u172|' oracle-java8-installer.*
			3. sudo sed -i 's|PARTNER_URL=http://download.oracle.com/otn-pub/java/jdk/8u161-b12/2f38c3b165be4555a1fa6e98c45e0808/|PARTNER_URL=http://download.oracle.com/otn-pub/java/jdk/8u172-b11/a58eab1ec242421181065cdc37240b08/|' oracle-java8-installer.*
			4. sudo sed -i 's|SHA256SUM_TGZ="6dbc56a0e3310b69e91bb64db63a485bd7b6a8083f08e48047276380a0e2021e"|SHA256SUM_TGZ="28a00b9400b6913563553e09e8024c286b506d8523334c93ddec6c9ec7e9d346"|' oracle-java8-installer.*
			5. sudo sed -i 's|J_DIR=jdk1.8.0_161|J_DIR=jdk1.8.0_172|' oracle-java8-installer.*
			6. sudo apt-get install oracle-java8-installer
			7. java  -version 
		must be:
			java -version
			java version "1.8.0_172"
			Java(TM) SE Runtime Environment (build 1.8.0_172-b11)
			Java HotSpot(TM) 64-Bit Server VM (build 25.172-b11, mixed mode)
			or later ( 9 / 10 )	

4. Download Tomcat 8 / 9
	1. wget apache.cp.if.ua/tomcat/tomcat-8/v8.5.30/bin/apache-tomcat-8.5.30.tar.gz
	2. tar -xvf apache-tomcat-8.5.30.tar.gz
	3. sudo mkdir /data/tomcat  ( or /opt/tomcat )
	4. sudo mv apache-tomcat-8.5.30 /data/tomcat ( or /opt/tomcat )
	5. sudo mcedit /etc/init.d/tomcat
			####Tomcat startup######
		#! /bin/sh

TOMCAT_DIR=/data/tomcat2/
export JAVA_HOME=/usr/lib/jvm/java-8-oracle/


case "$1" in
 start)
   su root -c $TOMCAT_DIR/bin/startup.sh
   ;;
 stop)
   su root -c $TOMCAT_DIR/bin/shutdown.sh
   sleep 10
   ;;
 restart)
   su root -c $TOMCAT_DIR/bin/shutdown.sh
   sleep 20
   su root -c $TOMCAT_DIR/bin/startup.sh
   ;;
 *)
   echo "Usage: tomcat {start|stop|restart}" >&2
   exit 3
   ;;
esac
##### end tomcat startup script ##########
	
3. Install Mysql-server (нужно создать другого пользователя бд и дать ему правана базу)
	1. sudo apt-get install mysql-server
	2. setup root password
	3. confirm root password
	4. mysql -u root -p 
	5. create database birzha;
	6. exit;
	7. mysql -u root -p birzha < dump_**.sql
	
5. Install NGINX
	1. sudo apt-get install nginx
	2. sudo cp code_analysis /etc/nginx/site-available
	3. sudo ls -l /etc/nginx/site-available/code_analysis /etc/nginx/site-enabled/link_code_analysis
	конфиг стандартный редирект на localhost:8080
6. DEPLOY ROOT.war
	1. sudo rm -rf /data/tomcat/webapps/*
	2. cp ROOT.war /data/tomcat/webapps
	3. sudo /etc/init.d/tomcat start

#################

убить все процессы джава 
sudo killall java

поиск по процессу томкат

ps aux | grep tomcat

sudo kill -9 <PID> 


##