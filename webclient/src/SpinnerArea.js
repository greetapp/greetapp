import { Backdrop, CircularProgress, useTheme } from "@mui/material";
import { useSpinnerAreaState } from "react-aws-cognito-lambda-dynamodb-base-prototype-app";

const SpinnerArea = () => {
	const { showing } = useSpinnerAreaState();
	const theme = useTheme();

	return (
		<Backdrop open={showing} style={{ zIndex: 1 + theme.zIndex.drawer }}>
			<CircularProgress />
		</Backdrop>
	);
};

export default SpinnerArea;
