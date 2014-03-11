#!/bin/bash
#
#  The main goal is to download and install :
#  - the Java EE 7 app server
#  - the asciidoctor module
#  - the asciidoctor-backends data
#  - the ad-editor app
###########

APP_SERVER_HOME=""

function usage {
  echo "usage : install.sh -s {serverName} -v {appVersion}"
  echo "OPTIONS :"
  echo "  -X : debug mode to shwo process steps"
  echo "  -s {serverName}  : Java EE 7 server name (default: wildfly)
  echo "  -v {appVersion}  : ad-editor version (default: last release)"
  echo "ex : install.sh -s wildfly -v 0.1.0-alpha3"
}

#Récupère les options
while getopts s:v:X opt; do
  case $opt in
    s) server="$OPTARG" ;;
    v) version="$OPTARG" ;;
    X) debug=true ;;
  esac;
done

#Check no params
if [ $# -eq 0 ]; then
    usage;
    exit;
fi

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

  if [ -z "$serverName" ]; then
     echo "No serverName specifiec (-s), Wildfly used by default"
  fi

  log "Paramètres de la commande => serverName : $serverName "
}

#
# Download the target Java EE 7 app server
function downloadAppServer {
    curl -O http://download.jboss.org/wildfly/8.0.0.Final/wildfly-8.0.0.Final.tar.gz
    tar xzf wildfly-8.0.0.Final.tar.gz
    APP_SERVER_HOME=$( echo pwd )
}

#
# AsciidoctorJ + JRuby + asciidoctor-backends
function downloadAsciidoctorDeps {

}

#
#
function downloadApp {

}

#
#
function deployToTarget {

}

#
#
function cleanAndStatus {

}


echo "--------- START AD-EDITOR INSTALL -------"
checkParams
defineTargetDirectory
downloadAppServer
downloadAsciidoctorDeps
downloadApp
deployToTarget
cleanAndStatus


