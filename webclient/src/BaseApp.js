import React from 'react';
import { Toolbar } from '@mui/material';
import { BaseAppScope, useBaseAppScopeState } from 'react-aws-cognito-lambda-dynamodb-base-prototype-app';

import AppBar from './AppBar';
import MessageArea from './MessageArea';
import SpinnerArea from './SpinnerArea';

const BaseApp = ({ appConfig, appRoutes }) => {
  const { routes } = useBaseAppScopeState(appRoutes);

  return <BaseAppScope appConfig={appConfig} routes={routes}>
      <AppBar routes={routes} />
      <Toolbar />
      <MessageArea />
      <SpinnerArea />
    </BaseAppScope>;
};

export default BaseApp;
