/// <reference types="vite/client" />

interface ImportMetaEnv {
	readonly VITE_APP_HOST: string;
	readonly VITE_APP_BASE_PATH: string;
	readonly VITE_APP_LOGO_URL: string;
	readonly VITE_APP_AWS_REGION: string;
	readonly VITE_APP_USER_POOL_ID: string;
	readonly VITE_APP_USER_POOL_DOMAIN: string;
	readonly VITE_APP_USER_POOL_APP_CLIENT_ID: string;
	readonly VITE_APP_IDENTITY_POOL_ID: string;
}

interface ImportMeta {
	readonly env: ImportMetaEnv;
}
