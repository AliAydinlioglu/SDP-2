import Koa from 'koa';
import { getLogger } from './core/logging';
import bodyParser from 'koa-bodyparser';
import InstallRest from './rest';
import config from 'config';
import koaCors from '@koa/cors';

const CORS_ORIGINS = config.get<string[]>('cors.origins'); 
const CORS_MAX_AGE = config.get<number>('cors.maxAge');

const app = new Koa();

app.use(
    koaCors({
      origin: (ctx) => {
        if (CORS_ORIGINS.indexOf(ctx.request.header.origin!) !== -1) {
          return ctx.request.header.origin!;
        }
        return CORS_ORIGINS[0] || '';
      },
      allowHeaders: ['Accept', 'Content-Type', 'Authorization'],
      maxAge: CORS_MAX_AGE, 
    }),
  );
  

app.use(bodyParser());

InstallRest(app);

app.listen(9000, () => {
    getLogger().info('🚀 Server listening on http://127.0.0.1:9000');
});