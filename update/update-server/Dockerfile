FROM java:8

RUN mkdir -p /opt/argos/plugins
RUN mkdir -p /var/log/argos
RUN mkdir -p /etc/argos

ADD target/cache-ehcache-1.0-plugin.zip /opt/argos/plugins/cache-ehcache-plugin.zip
ADD target/update-trinity-1.0-SNAPSHOT-plugin.zip /opt/argos/plugins/update-trinity-plugin.zip
ADD target/aniketos-security-framework-provider-1.0-SNAPSHOT-plugin.zip /opt/argos/plugins/aniketos-security-framework-provider-plugin.zip
ADD target/update-server.jar /opt/argos/update-server.jar

VOLUME ["/var/log/argos", "/etc/argos"]

EXPOSE 30205 40205

ENTRYPOINT ["java -jar /opt/argos/update-server.jar"]
