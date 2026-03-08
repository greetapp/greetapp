import { Toolbar } from "@mui/material";
import {
	type AppConfig,
	type AppRoute,
	BaseAppScope,
} from "react-aws-cognito-lambda-dynamodb-base-prototype-app";

import AppBar from "./AppBar";
import MessageArea from "./MessageArea";
import SpinnerArea from "./SpinnerArea";

type BaseAppProps = {
	appConfig: AppConfig;
	appRoutes: AppRoute[];
};

const BaseApp = ({ appConfig, appRoutes }: BaseAppProps) => (
	<BaseAppScope appConfig={appConfig} routes={appRoutes}>
		<AppBar routes={appRoutes} />
		<Toolbar />
		<MessageArea />
		<SpinnerArea />
	</BaseAppScope>
);

export default BaseApp;
