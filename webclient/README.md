# greetapp-webclient

Web app that greets the user with messages from the greetapp service, using Amazon Cognito for authentication.

## Configure AWS resources

Follow the commented instructions in [/conf/deployment-bucket-cloudformation.yaml](/conf/deployment-bucket-cloudformation.yaml) and [/conf/greetapp-cloudformation.yaml](/conf/greetapp-cloudformation.yaml) to create the AWS resources needed for the app.

Then create the local `.env.development` and `.env.production` files, following the provided examples [.env.development.example](.env.development.example) and [.env.production.example](.env.production.example) to use the created AWS resources.

## Run in development mode

```sh
npm install
npm run start
```

The app will be available on localhost:5000.

## Build for deploy

```sh
npm run build
```

After executing the build script, the `dist` folder will contain the files for publishing.
