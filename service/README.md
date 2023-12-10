# greetapp-service

Service that provides endpoints to get and update a greeting message, using Amazon DynamoDB as database and Amazon Cognito for authentication.


## Build

With JDK 17:
```bash
mvn clean package
```

## Configure AWS resources

Follow the commented instructions in [/conf/deployment-bucket-cloudformation.yaml](/conf/deployment-bucket-cloudformation.yaml) and [/conf/greetapp-cloudformation.yaml](/conf/greetapp-cloudformation.yaml) to create the AWS resources needed for the app.

To get the User Pool public keys in PEM format, you can first get them in JWK format in the URL https://cognito-idp.<region>.amazonaws.com/<user-pool-id>/.well-known/jwks.json and then perform JWK to PEM conversion in an online service such as https://8gwifi.org/jwkconvertfunctions.jsp.

Then confgure the necessary environment variables in the lambda function (it will look like the dummy values below), to use the created AWS resources.

```bash
COGNITO_USERPOOL_CLIENTID=abc123def456ghi789jkl012mn
COGNITO_USERPOOL_PUBLICKEY1_KID=aBc123dEf456gHi789jKl012mNo345pQr678sTu901v=
COGNITO_USERPOOL_PUBLICKEY1_PEM=MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhzg3V1oQvgS/LkSmPIoqMWj4HM6nFYjx2mTxs8oz08Lyrz9AbjYCpV1cpSVp3LpU6t31/SW10ZuEcSlnyWtEOUX3Hw7P3rtXVR/exH1Pt0IkwRV7H6jwXFiS3pBz1Z2L9Zzsanv2ZWVMvLSYoYp2yT6btSHSyk1e62UbXoaP/gZnwUPhQYQblaPCQakobVLduIpfFx+pb8GHizcdLz6vWlaahysrvmsbcjUl+KaCNEql4mtYZCg+7Wr//FAjDJbQAfJLGHzRu57dfbkLHN4ApcALZ52IFwvqGkAIT7dfa5sAS91pXHNJNUkks+Xnmj4+j6xO+CC+sopAg1cOMaEf6QIDAQAB
COGNITO_USERPOOL_PUBLICKEY2_KID=dEf456gHi789jKl012mNo345pQr678sTu901vWx234y=
COGNITO_USERPOOL_PUBLICKEY2_PEM=MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnOqUJuynXkn+3lQoyO4UNlWIAndz9bIauZuSf01PgghuB8xSx4QL8QHzou2q3RxyW30HCuxddoer4gFThDi9d64KaLIKUmplveMxNNDmi3baRJgF5x2NGdY4G3yU2R09b4zO6E3PMePJ8WetQuk4jgTih5dsnYpJ6Uv01bSZyE4BFH6xlj/7rNNpMkdt9PKewoSrV4ajZ3Q8O1qGoEBhWarPzYqXC6q/K/0Mv3Gm53yKHldtAPaVayGbO8Rw0N8ck/nFJSegMn2D9nidl63rTwQ7jXVCmqnJUc4kamZSjqXoP9nuETbwtZJggkMr0xLz6xGyEwAu2k105W5jyTJeFwIDAQAB
```
