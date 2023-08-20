/* eslint-disable import/no-extraneous-dependencies */
/* eslint-disable no-console */
const esbuild = require('esbuild');
const dotenv = require('dotenv');

const productionMode = (process.env.NODE_ENV === 'production');

const args = process.argv.slice(2);
const serveMode = args.includes('--serve');

dotenv.config({ path: (productionMode) ? '.env.production' : '.env.development' });

const definitions = Object.keys(process.env)
  .filter((key) => key.startsWith('REACT_APP_'))
  .reduce((prev, key) => ({
    ...prev,
    [`process.env.${key}`]: JSON.stringify(process.env[key])
  }), {});

const buildOptions = {
  entryPoints: ['./src/greetapp.js'],
  loader: { '.js': 'jsx' },
  outfile: 'dist/greetapp.min.js',
  bundle: true,
  minify: true,
  sourcemap: true,
  logLevel: 'info',
  define: { ...definitions }
};

if (serveMode) {
  esbuild.serve(
    {
      servedir: 'dist',
      port: 5000,
      onRequest: ({
        method, path, remoteAddress, status, timeInMS
      }) => console.log(`${remoteAddress} - "${method} ${path}" ${status} [${timeInMS}ms]`)
    },
    buildOptions
  ).then((serveResult) => {
    if (serveResult) {
      console.log(` > Local: http://${serveResult.host}:${serveResult.port}\n`);
    }
  });
} else {
  esbuild.build(buildOptions);
}
