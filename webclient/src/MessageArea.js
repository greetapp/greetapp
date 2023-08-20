import React from 'react';
import { Icon, IconButton, Snackbar } from '@mui/material';
import { useMessageAreaState } from 'react-aws-cognito-lambda-dynamodb-base-prototype-app';

const MessageArea = () => {
  const { message, dismissMessage } = useMessageAreaState();

  const handleClick = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    dismissMessage();
  };

  const handleOnExited = () => {
    dismissMessage();
  };

  return <Snackbar
    anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
    open={!!message}
    autoHideDuration={5000}
    onClose={handleClick}
    TransitionProps={{
      onExited: handleOnExited
    }}
    message={message}
    action={
      <IconButton color="inherit" onClick={handleClick}>
        <Icon>close</Icon>
      </IconButton>
    }
  />;
};

export default MessageArea;
