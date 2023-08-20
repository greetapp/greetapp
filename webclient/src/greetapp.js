import React from 'react';
import { createRoot } from 'react-dom/client';
import { makeAppConfig } from 'react-aws-cognito-lambda-dynamodb-base-prototype-app';

import BaseApp from './BaseApp';
import Greeting from './Greeting';

const myAppMessages = {
  LOGIN_SUCCESSFUL: 'User logged-in successfully',
  LOGIN_FAILED: 'Login failed',
  LOGOUT_SUCCESSFUL: 'User logged-out successfully',
  LOGOUT_FAILED: 'Log-out failed',
  LOGGED_OUT_USER: 'Logged-out User',
  LOG_COULD_NOT_LOGIN_WITH_REFRESHED_TOKENS: 'Could not login with refreshed tokens',
  LOG_COULD_NOT_DECODE_AUTHENTICATION_RESPONSE: 'Could not decode authentication response',
  LOG_COULD_NOT_GET_REFRESHED_TOKENS: 'Could not get refreshed tokens',
  LOG_NO_REFRESH_TOKEN_AVAILABLE: 'No refresh token available',
  LOG_COULD_NOT_REFRESH_TOKENS: 'Could not refresh tokens',
  LOG_COULD_NOT_GET_IDENTIFICATION_TOKENS: 'Could not get identification tokens with this authorization code'
};

const myEnv = {
  appHost: process.env.REACT_APP_HOST,
  appBasePath: process.env.REACT_APP_BASE_PATH,
  appLogoUrl: process.env.REACT_APP_LOGO_URL,
  appRegion: process.env.REACT_APP_AWS_REGION,
  appUserPoolId: process.env.REACT_APP_USER_POOL_ID,
  appUserPoolDomain: process.env.REACT_APP_USER_POOL_DOMAIN,
  appClientId: process.env.REACT_APP_USER_POOL_APP_CLIENT_ID,
  appIdentityPoolId: process.env.REACT_APP_IDENTITY_POOL_ID
};

const appConfig = makeAppConfig({
  ...myEnv,
  appRefreshTokenStorageKey: 'greetapp-refresh-token',
  appMessages: myAppMessages
});

const appRoutes = [
  {
    name: 'greeting',
    label: 'Greeting',
    path: '/',
    hideFromMenu: false,
    component: Greeting,
    options: { exact: true }
  }
];

const App = () => (
  <BaseApp appRoutes={appRoutes} appConfig={appConfig} />
);

const root = createRoot(document.getElementById('app_container'));
root.render(<App />);
