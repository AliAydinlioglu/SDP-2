import config from 'config';
import packageJson from '../../package.json';

export const ping = () => ({ pong: true });

export const getVersion = () => ({
  env: config.get<string>('env'),
  version: packageJson.version,
  name: packageJson.name,
});
