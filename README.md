**Clone the repo**

https://github.com/eclipse-edc/Samples.git

**Build the connector**

`./gradlew transfer:transfer-00-prerequisites:connector:build
`

To run the provider, just run the following command

`java -Dedc.fs.config=transfer/transfer-00-prerequisites/resources/configuration/provider-configuration.properties -jar transfer/transfer-00-prerequisites/connector/build/libs/connector.jar
`

**Run** 

`docker compose up --build`

**Check Health**

http://localhost:8080/api/v1/health

**Swagger Link**

http://localhost:8080/swagger-ui/index.html

Run tests

`./gradlew test`

Coverage report will be generated at

**build/reports/jacoco/test/html/index.html**
