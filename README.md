# Steps to build and run
- Go to `src/main/resources/application.properties` and update the following properties. Note - Do not wrap the properties values inside quotes.
  - logs.s3.bucket
  - aws.access.key
  - aws.secret.key
  - aws.region.name
- Install mvn. For mac you can use `brew install mvn`
- Go to project root. Run `mvn spring-boot:run`

# Sample Curl Requests
- To get all the logs containing `hello world` from timestamp 1698796800 to 1698969599
  => `curl 'http://localhost:8080/api/v1/logs/search?q=hello%20world&to=1698796800&from=1698969599'`
- To make the search case insensitive, you can use `ignoreCase` param =>
`curl 'http://localhost:8080/api/v1/logs/search?q=hello%20world&to=1698796800&from=1698969599&ignoreCase=true'`
