import mongoose from 'mongoose';
import jwt from 'jsonwebtoken';
import bcrypt from 'bcrypt';

import config from './../config';

var Schema = mongoose.Schema;
var userSchema = new Schema({
	fname: {
		type: String,
		required: [true, 'first name is required']
	},
	lname: {
		type: String,
		required: [true, 'last name is required']
	},
	email:  {
		type: String,
		required: [true, 'email is required']
	},
	password:  {
		type: String,
		required: [true, 'password is required']
	},
	watched: [{type: Schema.Types.ObjectId, ref: 'Complaint'}],
	upvoted: [{type: Schema.Types.ObjectId, ref: 'Complaint'}],
	dob: Date,
	admin: {type: Boolean, default: false},
	complaints: [{type: Schema.Types.ObjectId, ref: 'Complaint'}]
});

userSchema.methods.register = function(cb){
	User.findOne({
		email: this.email
	}, (err, user) => {
		if(user == null){
			bcrypt.hash(this.password, 10, (err, hash) => {
				this.password = hash;
				this.admin = true;		// <<<<#######################
				this.save((err) => {
					if(!err)
						cb(null);
					else
						cb(err);
				});
			})
		}else{
			cb(new Error('email already in use'));
		}
	});
}


// cb(err, userId, payload)
userSchema.statics.authenticate = function(email, password, cb){
	User.findOne({
		email: email
	}, (err, user) => {
		if(!user && !password)
			cb(new Error('email/password does not match'));
		else{
			bcrypt.compare(password, user.password, (err, matches) => {
				if(matches){
					let payload = {userId: user._id};
					let signed = jwt.sign(payload, config.jsonsecret, {
						expiresIn: 24 * 60 * 60
					});
					cb(null, user._id, signed);
				}else{
					cb(new Error('email/password does not match'));
				}
			});
		}
	})
}

var User = mongoose.model('User', userSchema);

export default User;