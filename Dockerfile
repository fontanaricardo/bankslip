FROM java:openjdk-8u91-jdk
EXPOSE 8080
VOLUME /tmp
COPY target/bankslip-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
