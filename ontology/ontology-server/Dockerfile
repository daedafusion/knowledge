FROM java:8

RUN mkdir -p /opt/argos/plugins
RUN mkdir -p /var/log/argos
RUN mkdir -p /etc/argos

ADD target/cache-ehcache-1.0-plugin.zip /opt/argos/plugins/cache-ehcache-plugin.zip
ADD target/ontology-storage-hibernate-1.0-SNAPSHOT-plugin.zip /opt/argos/plugins/ontology-storage-hibernate-plugin.zip
ADD target/aniketos-security-framework-provider-1.0-SNAPSHOT-plugin.zip /opt/argos/plugins/aniketos-security-framework-provider-plugin.zip
ADD target/ontology-server.jar /opt/argos/ontology-server.jar

VOLUME ["/var/log/argos", "/etc/argos"]

EXPOSE 30202 40202

ENTRYPOINT ["java -jar /opt/argos/ontology-server.jar"]
