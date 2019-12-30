# MobileServer

How to start the MobileServer application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/MobileServer-2.0.0.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`

Test
---
1. Run only unit test with command `mvn test`
2. Run unit test with integration test with command `mvn verify -DskipIntegrationTests=false`