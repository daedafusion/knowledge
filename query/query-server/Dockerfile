FROM java:8

RUN mkdir -p /opt/argos/plugins
RUN mkdir -p /var/log/argos
RUN mkdir -p /etc/argos

ADD target/cache-ehcache-1.0-plugin.zip /opt/argos/plugins/cache-ehcache-plugin.zip
ADD target/query-trinity-1.0-SNAPSHOT-plugin.zip /opt/argos/plugins/query-trinity-plugin.zip
ADD target/query-ontology-dsl-1.0-SNAPSHOT-plugin.zip /opt/argos/plugins/query-ontology-dsl-plugin.zip
ADD target/aniketos-security-framework-provider-1.0-SNAPSHOT-plugin.zip /opt/argos/plugins/aniketos-security-framework-provider-plugin.zip
ADD target/query-server.jar /opt/argos/query-server.jar

VOLUME ["/var/log/argos", "/etc/argos"]

EXPOSE 30201 40201

ENTRYPOINT ["java -jar /opt/argos/query-server.jar"]
