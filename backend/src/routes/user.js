import Express from 'express';

import User from './../models/User';

let userRoutes = require('express').Router();

userRoutes.post('/user/register', (req, res) => {
	// requires fname, lname, password and email address
	let user = new User();
	user.fname = req.body.fname;
	user.lname = req.body.lname;
	user.password = req.body.password;
	user.email = req.body.email;
	user.register(err => {
		if(!err)
			res.json({success: true});
		else
			res.json({success: false, message: err.message});
	})
});

userRoutes.post('/user/login', (req, res) => {
	User.authenticate(req.body.email, req.body.password, (err, token) => {
		if(!err){
			res.json({success: true, token: token});
		}else{
			res.json({success: false, message: err.message});
		}
	});
});

userRoutes.get('/user/:username/profile', (req, res) => res.json({user: 'true'}));

export default userRoutes;