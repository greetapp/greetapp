import React, { useCallback, useEffect, useState } from 'react';
import {
  Button,
  Grid,
  TextField,
  Typography
} from '@mui/material';
import { useMessage, useSpinner, useUser } from 'react-aws-cognito-lambda-dynamodb-base-prototype-app';

const GREETAPP_URL = `${process.env.REACT_APP_GREETAPP_SERVICE_URL}/greet`;

const handleErrors = (response) => (
  new Promise((resolve, reject) => {
    if (!response.ok) {
      response.text()
        .then((body) => {
          try {
            const jsonBody = JSON.parse(body);
            reject(new Error(((jsonBody.error)) ? jsonBody.error : jsonBody));
          } catch (err) {
            reject(Error(body));
          }
        })
        .catch((err) => {
          reject(Error(`Error while parsing request error message: ${err}`));
        });
    } else {
      resolve(response);
    }
  })
);

const GreetingMessageForm = ({ greetingMessageUpdated }) => {
  const { user } = useUser();
  const { showSpinner, dismissSpinner } = useSpinner();
  const { showMessage } = useMessage();

  const onSubmit = (event) => {
    event.preventDefault();

    showSpinner();
    fetch(GREETAPP_URL, {
      method: 'PUT',
      headers: user.accessToken ? { 'x-amz-access-token': user.accessToken } : {},
      body: JSON.stringify({ message: event.target.message.value })
    }).then(handleErrors)
      .then(() => {
        showMessage('Message updated successfully');
        greetingMessageUpdated();
      })
      .catch((err) => {
        console.error('err', err, err.error, Object.keys(err));
        showMessage(`Error trying to update greeting message: ${err}`);
      })
      .finally(() => {
        dismissSpinner();
      });
  };

  const userLoggedIn = !!user.accessToken;

  return userLoggedIn
    ? (
      <div>
        <form onSubmit={onSubmit}>
          <Grid container spacing={1} alignItems="center">
            <Grid item xs={12}>
              <Typography>Change greeting message?</Typography>
            </Grid>
            <Grid item>
              <TextField name="message" label="New greeting message"></TextField>
            </Grid>
            <Grid item>
              <Button variant="outlined" type="submit">Save</Button>
            </Grid>
          </Grid>
        </form>
      </div>
    )
    : null;
};

const useFetchGreeting = (user) => {
  const { showSpinner, dismissSpinner } = useSpinner();

  const fetchGreeting = useCallback(() => (
    new Promise((resolve, reject) => {
      if (user) {
        showSpinner();
        fetch(GREETAPP_URL, {
          method: 'GET',
          headers: user.accessToken ? { 'x-amz-access-token': user.accessToken } : {}
        }).then((response) => {
          response.json().then((res) => {
            resolve(res.message);
          }).catch((err) => {
            reject(Error(`Erro ao decodificar JSON da mensagem: ${err}`));
          });
        }).catch((err) => {
          reject(Error(`Erro ao tentar recuperar mensagem: ${err}`));
        }).finally(() => {
          dismissSpinner();
        });
      } else {
        resolve();
      }
    })
  ), [dismissSpinner, showSpinner, user]);

  return { fetchGreeting };
};

const refreshGreeting = (fetchGreeting, setGreeting, showMessage, user) => {
  if (user) {
    fetchGreeting()
      .then((message) => setGreeting(message))
      .catch((err) => {
        showMessage(err);
        setGreeting();
      });
  } else {
    setGreeting();
  }
};

const GreetingMessage = () => {
  const { user } = useUser();
  const { showMessage } = useMessage();
  const [greeting, setGreeting] = useState();
  const { fetchGreeting } = useFetchGreeting(user);

  const greetingMessageUpdated = useCallback(() => {
    refreshGreeting(fetchGreeting, setGreeting, showMessage, user);
  }, [fetchGreeting, showMessage, user]);

  useEffect(() => {
    refreshGreeting(fetchGreeting, setGreeting, showMessage, user);
  }, [fetchGreeting, showMessage, user]);

  const userName = (user && user.name) ? user.name : 'Anonymous User';

  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <Typography variant="h3">
          {greeting
            ? `${greeting}, ${userName}!`
            : ''
          }
        </Typography>
      </Grid>
      <Grid item xs={12}>
        <GreetingMessageForm greetingMessageUpdated={greetingMessageUpdated} />
      </Grid>
    </Grid>
  );
};

const Greeting = () => {
  const { user, loginAnonymously } = useUser();

  useEffect(() => {
    if (!user) {
      loginAnonymously();
    }
  }, [loginAnonymously, user]);

  return <GreetingMessage />;
};

export default Greeting;
