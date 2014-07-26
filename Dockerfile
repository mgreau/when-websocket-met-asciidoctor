FROM jboss/wildfly

#Create module asciidoctor-module
RUN cd /opt/wildfly/modules && mkdir org && cd org && mkdir asciidoctor && cd asciidoctor && mkdir main

RUN cd /opt/wildfly/modules/org/asciidoctor/main && curl -O https://raw.githubusercontent.com/mgreau/when-websocket-met-asciidoctor/adj-1.5.0/module/org/asciidoctor/main/module.xml
RUN cd /opt/wildfly/modules/org/asciidoctor/main && curl -O http://search.maven.org/remotecontent?filepath=log4j/log4j/1.2.17/log4j-1.2.17.jar
RUN cd /opt/wildfly/modules/org/asciidoctor/main && curl -O http://search.maven.org/remotecontent?filepath=org/slf4j/slf4j-log4j12/1.7.5/slf4j-log4j12-1.7.5.jar
RUN cd /opt/wildfly/modules/org/asciidoctor/main && curl -O http://search.maven.org/remotecontent?filepath=org/slf4j/slf4j-api/1.7.5/slf4j-api-1.7.5.jar
RUN cd /opt/wildfly/modules/org/asciidoctor/main && curl -O http://search.maven.org/remotecontent?filepath=org/jruby/jruby-complete/1.7.4/jruby-complete-1.7.4.jar

#Error with the curl command with bintray
#RUN cd /opt/wildfly/modules/org/asciidoctor/main && curl -O https://dl.bintray.com/lordofthejars/maven/org/asciidoctor/asciidoctorj/1.5.0.preview2/asciidoctorj-1.5.0.preview2.jar
ADD asciidoctorj-1.5.0.preview2.jar /opt/wildfly/modules/org/asciidoctor/main/

#Add backend for slides
RUN cd /opt/wildfly/standalone/ && mkdir data && cd data && mkdir asciidoctor-backends
ADD asciidoctor-backends /opt/wildfly/standalone/data/asciidoctor-backends/

#Add ad-editor from local build
ADD ad-editor.war /opt/wildfly/standalone/deployments/

#Override the command to be sure that the server starts before all components are copied
CMD ["/opt/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
