import React, { useRef, useState } from 'react';
import {
  AppBar as MuiAppBar,
  Button,
  Divider,
  Icon,
  IconButton,
  Menu,
  MenuItem,
  Toolbar
} from '@mui/material';
import { useAppBarState } from 'react-aws-cognito-lambda-dynamodb-base-prototype-app';

const LoginButton = ({ hideLoginButton, appExternalLoginUrl }) => (
  (hideLoginButton)
    ? null
    : <Button color="inherit" href={appExternalLoginUrl}>Login</Button>
);

const AccountButton = ({ hideAccountButton, userName, logoffAndShowMessage }) => {
  const [accountMenuOpened, setAccountMenuOpened] = useState(false);
  const accountMenuRef = useRef(null);

  const handleAccountMenuClick = () => {
    setAccountMenuOpened(true);
  };

  const handleAccountMenuClose = () => {
    setAccountMenuOpened(false);
  };

  const handleLogoff = () => {
    logoffAndShowMessage();
    setAccountMenuOpened(false);
  };

  return (hideAccountButton)
    ? null
    : (
      <>
        <IconButton color="inherit" ref={accountMenuRef} onClick={handleAccountMenuClick}>
          <Icon>account_circle</Icon>
        </IconButton>
        <Menu open={accountMenuOpened}
            onClose={handleAccountMenuClose}
            anchorEl={accountMenuRef.current}
            anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
            transformOrigin={{ vertical: 'top', horizontal: 'right' }}>
          <MenuItem disabled={true}>{userName}</MenuItem>
          <Divider />
          <MenuItem onClick={handleLogoff}>Logoff</MenuItem>
        </Menu>
      </>
    );
};

const AppBar = ({ routes }) => {
  const {
    hideLoginButton,
    appExternalLoginUrl,
    hideAccountButton,
    userName,
    logoffAndShowMessage
  } = useAppBarState(routes);

  return <MuiAppBar>
    <Toolbar>
      <div style={{ marginLeft: 'auto' }}>
        <LoginButton hideLoginButton={hideLoginButton} appExternalLoginUrl={appExternalLoginUrl} />
        <AccountButton
          hideAccountButton={hideAccountButton}
          userName={userName}
          logoffAndShowMessage={logoffAndShowMessage} />
      </div>
    </Toolbar>
  </MuiAppBar>;
};

export default AppBar;
