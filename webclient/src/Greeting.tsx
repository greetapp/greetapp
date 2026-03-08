import { Button, Grid, TextField, Typography } from "@mui/material";
import {
	type Dispatch,
	type SetStateAction,
	useCallback,
	useEffect,
	useState,
} from "react";
import {
	useMessage,
	useSpinner,
	useUser,
} from "react-aws-cognito-lambda-dynamodb-base-prototype-app";

const GREETAPP_URL = `${import.meta.env.VITE_APP_GREETAPP_SERVICE_URL}/greet`;

const handleErrors = (response: Response) =>
	new Promise((resolve, reject) => {
		if (!response.ok) {
			response
				.text()
				.then((body) => {
					try {
						const jsonBody = JSON.parse(body);
						reject(new Error(jsonBody.error ? jsonBody.error : jsonBody));
					} catch (_err) {
						reject(Error(body));
					}
				})
				.catch((err) => {
					reject(Error(`Error while parsing request error message: ${err}`));
				});
		} else {
			resolve(response);
		}
	});

type GreetingMessageFormProps = {
	greetingMessageUpdated: () => void;
};

const GreetingMessageForm = ({
	greetingMessageUpdated,
}: GreetingMessageFormProps) => {
	const { user } = useUser();
	const { showSpinner, dismissSpinner } = useSpinner();
	const { showMessage } = useMessage();

	const onSubmit = (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();
		const formTarget = event.target as HTMLFormElement;
		const changedValues = {
			message: formTarget.message.value,
		};

		showSpinner();
		const headers: Record<string, string> = {
			"Content-Type": "application/json",
		};
		if (user.accessToken) {
			headers["x-amz-access-token"] = user.accessToken;
		}
		fetch(GREETAPP_URL, {
			method: "PUT",
			headers,
			body: JSON.stringify({ message: changedValues.message }),
		})
			.then(handleErrors)
			.then(() => {
				showMessage("Message updated successfully");
				greetingMessageUpdated();
			})
			.catch((err) => {
				console.error("err", err, err.error, Object.keys(err));
				showMessage(`Error trying to update greeting message: ${err}`);
			})
			.finally(() => {
				dismissSpinner();
			});
	};

	const userLoggedIn = !!user.accessToken;

	return userLoggedIn ? (
		<div>
			<form onSubmit={onSubmit}>
				<Grid container spacing={1} alignItems="center">
					<Grid size={12}>
						<Typography>Change greeting message?</Typography>
					</Grid>
					<Grid>
						<TextField name="message" label="New greeting message" />
					</Grid>
					<Grid>
						<Button variant="outlined" type="submit">
							Save
						</Button>
					</Grid>
				</Grid>
			</form>
		</div>
	) : null;
};

type User = {
	name?: string;
	accessToken?: string;
};

const useFetchGreeting = (user: User) => {
	const { showSpinner, dismissSpinner } = useSpinner();

	const fetchGreeting = useCallback(
		() =>
			new Promise<string | undefined>((resolve, reject) => {
				if (user) {
					showSpinner();
					fetch(GREETAPP_URL, {
						method: "GET",
						headers: user.accessToken
							? { "x-amz-access-token": user.accessToken }
							: {},
					})
						.then((response) => {
							response
								.json()
								.then((res) => {
									resolve(res.message);
								})
								.catch((err) => {
									reject(Error(`Erro ao decodificar JSON da mensagem: ${err}`));
								});
						})
						.catch((err) => {
							reject(Error(`Erro ao tentar recuperar mensagem: ${err}`));
						})
						.finally(() => {
							dismissSpinner();
						});
				} else {
					resolve(undefined);
				}
			}),
		[dismissSpinner, showSpinner, user],
	);

	return { fetchGreeting };
};

const refreshGreeting = (
	fetchGreeting: () => Promise<string | undefined>,
	setGreeting: Dispatch<SetStateAction<string | undefined>>,
	showMessage: (message: string) => void,
	user: User,
) => {
	if (user) {
		fetchGreeting()
			.then((message) => setGreeting(message))
			.catch((err) => {
				showMessage(err);
				setGreeting(undefined);
			});
	} else {
		setGreeting(undefined);
	}
};

const GreetingMessage = () => {
	const { user } = useUser();
	const { showMessage } = useMessage();
	const [greeting, setGreeting] = useState<string | undefined>();
	const { fetchGreeting } = useFetchGreeting(user);

	const greetingMessageUpdated = useCallback(() => {
		refreshGreeting(fetchGreeting, setGreeting, showMessage, user);
	}, [fetchGreeting, showMessage, user]);

	useEffect(() => {
		refreshGreeting(fetchGreeting, setGreeting, showMessage, user);
	}, [fetchGreeting, showMessage, user]);

	const userName = user?.name ? user.name : "Anonymous User";

	return (
		<Grid container spacing={2}>
			<Grid size={12}>
				<Typography variant="h3">
					{greeting ? `${greeting}, ${userName}!` : ""}
				</Typography>
			</Grid>
			<Grid size={12}>
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
