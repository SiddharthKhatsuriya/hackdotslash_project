import Express from 'express';

import userRoutes from './src/routes/user';

const app = Express();

const router = require('express').Router()

// allow remote origin
app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

app.get('/api', (req, res) => res.json({success: 'true'}));
app.use('/api', userRoutes);

app.listen(3000, () => console.log('app listening on port 3000!'));