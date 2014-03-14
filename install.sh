#!/bin/bash
#
#  The main goal is to download and install :
#  - the Java EE 7 app server
#  - the asciidoctor module
#  - the asciidoctor-backends data
#  - the ad-editor app
###########
debug=true

APP_SERVER_HOME=""
applicationServer="wildfly-8.0.0.Final"

appVersion="0.1.0-alpha3"
appName="ad-editor"


function usage {
  echo "usage : install.sh -s {server} -v {version}"
  echo "OPTIONS :"
  echo "  -X : debug mode to shwo process steps"
  echo "  -s {server}  : Java EE 7 server name (default: wildfly)"
  echo "  -v {version}  : ad-editor version (default: last release)"
  echo "ex : install.sh -s wildfly -v 0.1.0-alpha3"
}

while getopts s:v:X opt; do
  case $opt in
    s) server="$OPTARG" ;;
    v) version="$OPTARG" ;;
    X) debug=true ;;
  esac;
done


#
#if mode debug is true
function log {
   if [ $# -eq 1 ] && [ $debug = true ]; then
       echo "[INFO] $1";
   fi
}

#
#Check that all required params are present
function checkParams {

  if [ -z "$server" ]; then
     log "No server name specified (-s), Wildfly used by default"
  else
     log "TODO handle TomEE and other app server..."
     #TODO : handle TomEE
     #applicationServer==$server
  fi

  if [ -z "$version" ]; then
     log "No version specified (-v), $appVersion  used by default"
  else
     appVersion=$version
  fi
  
  app=$appName"-"$appVersion
  appWAR=$app".war"
  #github release
  releaseVersion="v"$app

  log "Installation for ... App Server : $applicationServer, Application : $app "
}

#check if a server is already started on 8080
function checkPortAlreadyUsed {
  STATUS=$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8080)
    if [ $STATUS -eq 200 ]; then
        log "Is there a  server already started on 8080 ? Please stop it and relaunch this script."
        exit 0;
    else
	log " $STATUS, OK (no app server started on 8080)"
    fi
}

#
# Download and install the target Java EE 7 app server
function checkJavaEE7AppServer {
    isAppServerInstalled=false
    
    # determine if WildFly is installed
    if [ "x$JBOSS_HOME" = "x" ]; then
      log "No JBOSS_HOME defined"
    #check if JBOSS_HOME is a WildFly version
    elif [ ! -f "$JBOSS_HOME/bin/init.d/wildfly.conf" ]; then
      log "JBOSS installed is not a WildFly version"
    #ok 
    else
      isAppServerInstalled=true
      APP_SERVER_HOME=$JBOSS_HOME
    fi

   # install WildFly if is not present
   if [ "$isAppServerInstalled" = false ]; then
     if [ !  -f "$applicationServer.tar.gz" ]; then
      log "No targz archive found, download the server..."
      curl -O http://download.jboss.org/wildfly/8.0.0.Final/$applicationServer.tar.gz
     else
       #clean an older wildfly version
       if [ -d "$applicationServer" ]; then
         log "Delete an older unpacked version"
         rm -rf $applicationServer
       fi
     fi

     tar xzf $applicationServer.tar.gz
     APP_SERVER_HOME=$(pwd)"/$applicationServer"
     export JBOSS_HOME="$APP_SERVER_HOME"
   fi
   log "App Server HOME :  $APP_SERVER_HOME"

}

#
# AsciidoctorJ + JRuby + asciidoctor-backends
function createAsciidoctorModule {
  #check if this module already exist
  if [ -d "$APP_SERVER_HOME/modules/org/asciidoctor/main" ]; then
   log "Asciidoctor module is already installed in the App Server"
  else
    #if it doesn't exist, check if the dependencies are present in the local m2 repo
    jrubyInRepoLocal=$HOME"/.m2/repository/org/jruby/jruby-complete/1.7.4/jruby-complete-1.7.4.jar"
    jrubyInRepoDistant="http://search.maven.org/remotecontent?filepath=org/jruby/jruby-complete/1.7.4/jruby-complete-1.7.4.jar"
    asciidoctorJInRepoLocal=$HOME"/.m2/repository/org/asciidoctor/asciidoctor-java-integration/0.1.4/asciidoctor-java-integration-0.1.4.jar"
    asciidoctorJInRepoDistant="http://search.maven.org/remotecontent?filepath=org/asciidoctor/asciidoctor-java-integration/0.1.4/asciidoctor-java-integration-0.1.4.jar"
    
    if [ ! -d "./org/asciidoctor/main" ]; then
      mkdir "org" && cd "org" && mkdir "asciidoctor" && cd "asciidoctor" && mkdir "main" && cd "main" 
      if [ ! -f "$jrubyInRepoLocal" ]; then
        log "Download JRuby $jrubyInRepoLocal..."
        curl -O $jrubyInRepoDistant
      else
        log "Copy JRuby from local repo"
        cp $jrubyInRepoLocal .
      fi
      if [ ! -f "$asciidoctorJInRepoLocal" ]; then
        log "Download AsciidoctorJ ..."
        curl -O $asciidoctorJInRepoDistant
      else
        log "Copy AsciidoctorJ from local repo"
        cp $asciidoctorJInRepoLocal .
      fi
      log "Download module.xml"
      curl -O https://raw.github.com/mgreau/when-websocket-met-asciidoctor/$appVersion/module/org/asciidoctor/main/module.xml
      cd ../../..
    else
      log "Asciidoctor module is ready to install"
    fi
    log "Install asciidoctor module"
    cp -r org $APP_SERVER_HOME/modules
  fi
}

#
#
function installApp {
  log "Downloading and install app..."
  curl -OL https://github.com/mgreau/when-websocket-met-asciidoctor/releases/download/$appVersion/$appWAR
  mv $appWAR $APP_SERVER_HOME/standalone/deployments
  log "App deployed"
}


#
#
function cleanAndStatus {
  log "cleaning..."
  rm -rf "org"
}

function launchApp {
  log "Launch the app..."
  $APP_SERVER_HOME/bin/standalone.sh &
  url="http://localhost:8080/$app"

  while true
  do
    if [ -f $APP_SERVER_HOME/standalone/log/server.log ]
    then
      cat $APP_SERVER_HOME/standalone/log/server.log | grep "started in" 2>/dev/null
      if [ $? = 0 ]
      then
        unamestr=`uname`
	if [[ "$unamestr" == 'Linux' ]]; then
          xdg-open $url	  
	else 
	  open $url
	fi
      exit
      fi
    fi
  done

  log "Have fun with $app : $url"
}

echo "--------- INSTALLING Real-time Collaborative editor for AsciiDoc -------"
checkParams
checkPortAlreadyUsed
checkJavaEE7AppServer
createAsciidoctorModule
installApp
cleanAndStatus
launchApp
