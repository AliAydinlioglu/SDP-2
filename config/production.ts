export default {
  cors: {
    origins: [''],
  },
  log: {
    level: 'info',
    disabled: false,
  },
  auth: {
    maxDelay: 3000,
    jwt: {
      expirationInterval: 7 * 24 * 60 * 60, // s (7 days)
    },
    
  },
};
