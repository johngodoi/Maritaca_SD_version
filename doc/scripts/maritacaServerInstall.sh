#!/bin/bash
#
# Author: Tiago Barabasz
# Email:  tbarabasz@gmail.com

################################## VARIABLES ################################## 
CASSANDRA_URL="http://www.apache.org/dyn/closer.cgi?path=/cassandra/1.0.8/apache-cassandra-1.0.8-bin.tar.gz"
JBOSS_URL="http://download.jboss.org/jbossas/7.1/jboss-as-7.1.1.Final/jboss-as-7.1.1.Final.tar.gz"

CASSANDRA_TGZ="cassandra.tgz"
JBOSS_TGZ="jboss.tgz"

MARITACA_URL=""

SF_USER="tiagobarabasz"

################################## FUNCTIONS ################################## 
createMaritacaDir(){
	mkdir maritacaServer
	cd    maritacaServer
}

download(){
	wget -O $1 $2
	tar zxf $1 
}

cloneProject(){
	git clone ssh://$SF_USER@git.code.sf.net/p/maritaca/experimental maritaca
}

buildProject(){
	cd maritaca
	mvn clean install
}

##################################   CODE    ################################## 
createMaritacaDir
download "$CASSANDRA_TGZ" "$CASSANDRA_URL"
download "$JBOSS_TGZ"     "$JBOSS_URL"
cloneProject
buildProject

