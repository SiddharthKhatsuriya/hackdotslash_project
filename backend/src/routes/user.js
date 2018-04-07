import Express from 'express';

import User from './../models/User';
import AdminCheck from './../middlewares/AdminCheck';

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
	User.authenticate(req.body.email, req.body.password, (err, id, token) => {
		if(!err){
			res.json({success: true, id: id, token: token});
		}else{
			res.json({success: false, message: err.message});
		}
	});
});

userRoutes.get('/user/:id/profile', (req, res) => {
	User
		.findOne({
			_id: req.params.id
		})
		.select('-_id -__v -password -complaints -admin')
		.exec((err, user) => {
			if(!err && user){
				res.json({success: true, user: user});
			}else{
				res.json({success: false, message: 'user not found'});
			}
		});
});

export default userRoutes;