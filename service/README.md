# greetapp-service

Service that provides endpoints to get and update a greeting message, using Amazon DynamoDB as database and Amazon Cognito for authentication.

## Configure AWS resources

Follow the commented instructions in [/conf/greetapp-cloudformation.yaml](/conf/greetapp-cloudformation.yaml) to create the AWS resources needed for the app.

To get the User Pool public keys in PEM format, you can first get them in JWK format in the URL https://cognito-idp.<region>.amazonaws.com/<user-pool-id>/.well-known/jwks.json and then perform JWK to PEM conversion in an online service such as https://8gwifi.org/jwkconvertfunctions.jsp.

Then create the local `.env.development` and `.env.production` files, following the provided examples [.env.development.example](.env.development.example) and [.env.production.example](.env.production.example) to use the created AWS resources.


## Build

With JDK 17:
```bash
mvn clean package
```

## Running on local environment

Run the application with `java -jar`, using the env command to load variables from the `.env.development` file to the execution environment.
```bash
env $(cat .env.development) java -jar target/target/greetapp-1.0-SNAPSHOT.jar
```

The IAM user running the application locally should have the same privileges that were given to the EC2_greetappServiceInstance_Role.

Then you can test the endpoints. Note that you need to provide a header with a Cognito Access Token to perform the update.

```
curl -X GET http://localhost:8080/greet
{"message":"Hello"}

curl -X PUT --data '{"message":"Hi"}' http://localhost:8080/greet
{"error":"Unauthorized. Please provide access token."}

curl -X PUT -H "x-amz-access-token: eyJraW..." --data '{"message":"Hi"}' http://localhost:8080/greet
{"status":"OK"}

curl -X GET http://localhost:8080/greet
{"message":"Hi"}
```
