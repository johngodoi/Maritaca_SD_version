#!/bin/bash
#$1 form url
#$2 form name
PROJECT_NAME="maritaca-mobile"
SRC_PACKAGE_PATH="src/br/unifesp/maritaca/mobile"
GEN_PACKAGE_PATH="gen/br/unifesp/maritaca/mobile"
DIR_MARITACA_MOBILE=$3
DIR_NEW_PROJECT=$4
NEW_PROJECT=$DIR_NEW_PROJECT"/"$1
if [ -n "$1" ]; then
  if [ ! -d $NEW_PROJECT ]; then
    mkdir $NEW_PROJECT
  else
	rm -rf $NEW_PROJECT
	mkdir $NEW_PROJECT
  fi
  #
  cp -r $DIR_MARITACA_MOBILE/$PROJECT_NAME/ $DIR_NEW_PROJECT/$1
  #src:
  mkdir $DIR_NEW_PROJECT/$1/$PROJECT_NAME/$SRC_PACKAGE_PATH/$1
  cd $DIR_NEW_PROJECT/$1/$PROJECT_NAME/$SRC_PACKAGE_PATH
  ls -1 | grep -v ^$1 | xargs -I{} mv {} $DIR_NEW_PROJECT/$1/$PROJECT_NAME/$SRC_PACKAGE_PATH/$1
  #gen:
  if [ -d $DIR_NEW_PROJECT/$1/$PROJECT_NAME/$GEN_PACKAGE_PATH ]; then  
    mkdir $DIR_NEW_PROJECT/$1/$PROJECT_NAME/$GEN_PACKAGE_PATH/$1
    cd $DIR_NEW_PROJECT/$1/$PROJECT_NAME/$GEN_PACKAGE_PATH
    ls -1 | grep -v ^$1 | xargs -I{} mv {} $DIR_NEW_PROJECT/$1/$PROJECT_NAME/$GEN_PACKAGE_PATH/$1
  fi
  cd $DIR_NEW_PROJECT/$1/$PROJECT_NAME
  ant clean
  find . -name "*.java" -o -name "*.xml" | xargs sed -i 's/br\.unifesp\.maritaca\.mobile/br\.unifesp\.maritaca\.mobile\.'$1'/g'
  find . -name "ant.properties" | xargs sed -i 's/'$PROJECT_NAME'/proys\/'$1'\/'$PROJECT_NAME'/g'
  find . -name "strings.xml" | xargs sed -i 's/<string name=\"app_name\">Maritaca Mobile/<string name=\"app_name\">'$2'/g'
  ant release
fi
