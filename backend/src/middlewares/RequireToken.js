import jwt from 'jsonwebtoken';

import config from './../config';

let RequireToken = (req, res, next) => {
	let token = req.body.token ? req.body.token : req.query.token;
	jwt.verify(token, config.jsonsecret, (err, decoded) => {
		if(!err){
			req.decoded = decoded;
			next();
		}else{
			res.status(403).json({success: false, message: 'authentication token invalid/not provided'});
		}
	});
};

export default RequireToken;