import Express from 'express';
import mongoose from 'mongoose';
import bodyParser from 'body-parser';

import config from './src/config';
import userRoutes from './src/routes/user';
import complaintRoutes  from './src/routes/complaint';

const app = Express();

const router = require('express').Router()

// exception
let unless = function(path, middleware) {
    return function(req, res, next) {
        if (req.path.search(path) != -1) {
            return next();
        } else {
            return middleware(req, res, next);
        }
    };
};

// parsing params from body
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json()); // for parsing application/json

// allow remote origin
app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

app.get('/api', (req, res) => res.json({success: 'true'}));
app.use('/api', userRoutes);
app.use('/api', complaintRoutes);

// loading config vars
app.set('jsonsecret', config.jsonsecret);

// database
mongoose.connect('mongodb://127.0.0.1/resolve');
var db = mongoose.connection;
app.locals.db = db;
db.on('error', () => console.error.bind(console, "Can not connect to the database"));
db.once('open', function() {
	console.log("Connected to the database..");
	app.listen(3000, () => console.log('app listening on port 3000...'));
});