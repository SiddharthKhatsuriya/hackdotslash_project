import jwt from 'jsonwebtoken';

import User from './../models/User';
import config from './../config';

let AdminCheck = (req, res, next) => {
	let userId = req.decoded.userId;
	User.findOne({
		_id: userId
	}, (err, user) => {
		if(!err && user.admin)
			next();
		else
			res.status(403).json({success: false, message: 'authentication required'});
	})
};

export default AdminCheck;