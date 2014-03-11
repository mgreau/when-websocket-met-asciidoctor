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
       echo "[DEBUG] $1";
   fi
}

#
#Check that all required params are present
function checkParams {

  if [ -z "$server" ]; then
     echo "No server name specified (-s), Wildfly used by default"
  else
     serverApplication=$server
  fi

  if [ -z "$version" ]; then
     echo "No version specified (-v), $appVersion  used by default"
  else
     appVersion=$version
  fi
  app=$appName"-"$appVersion
  appWAR=$app".war"
  #github release
  releaseVersion="v"$app

  log "Paramètres de la commande => server : $server, version : $version "
}

#
# Download the target Java EE 7 app server
function downloadAppServer {
 # determine if WildFly is installed, if not set
    if [ "x$JBOSS_HOME" = "x" ]; then
      log "No JBOSS_HOME define, download and install WildFly..."
      curl -O http://download.jboss.org/wildfly/8.0.0.Final/$applicationServer.tar.gz
      tar xzf $applicationServer.tar.gz
      APP_SERVER_HOME=$(pwd)"/$applicationServer"
   else
      APP_SERVER_HOME=$JBOSS_HOME
   fi

    log "server home :  $APP_SERVER_HOME"
}

#
# AsciidoctorJ + JRuby + asciidoctor-backends
function createAsciidoctorModule {
  mkdir "org" && cd "org" && mkdir "asciidoctor" && cd "asciidoctor" && mkdir "main" && cd "main" 
  log "Download AsciidoctorJ..."
  curl -O http://search.maven.org/remotecontent?filepath=org/asciidoctor/asciidoctor-java-integration/0.1.4/asciidoctor-java-integration-0.1.4.jar
  log "Download JRuby..."
  curl -O http://search.maven.org/remotecontent?filepath=org/jruby/jruby-complete/1.7.4/jruby-complete-1.7.4.jar
  log "Download module.xml"
  curl -O https://raw.github.com/mgreau/when-websocket-met-asciidoctor/v0.1.0-alpha3.preview1/module/org/asciidoctor/main/module.xml
  cd ../../..
  log "Install asciidoctor module"
  cp -r org $APP_SERVER_HOME/modules

}

#
#
function installApp {
  log "Downloading and install app..."
  curl -OL https://github.com/mgreau/when-websocket-met-asciidoctor/releases/download/v0.1.0-alpha3.preview1/ad-editor-0.1.0-alpha3.war
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
  { sleep 9; echo waking up after 9 seconds; }
  url="http://localhost:8080/$app"
  open $url
  log "Have fun with $app!"
}

echo "--------- INSTALLING Real-time Collaborative editor for AsciiDoc -------"
checkParams
downloadAppServer
createAsciidoctorModule
installApp
cleanAndStatus
launchApp
