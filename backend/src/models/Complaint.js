import mongoose from 'mongoose';
import bcrypt from 'bcrypt';

import ComplaintCategory from './ComplaintCategory';
import User from './User';

var Schema = mongoose.Schema;
var complaintSchema = new Schema({
	description: {
		type: String,
		required: [true, 'title is required']
	},
	watchers: [{type: Schema.Types.ObjectId, ref: 'User'}],
	image: String,
	category: {type: Schema.Types.ObjectId, ref: 'ComplaintCategory'},
	votes: {type: Number, default: 0},
	lat: {type: Number},
	lng: {type: Number},
	upvoters: [{type: Schema.Types.ObjectId, ref: 'User'}],
	comments: [{type: Schema.Types.ObjectId, ref: 'Comment'}],
	author: {type: Schema.Types.ObjectId, ref: 'User'}
});

complaintSchema.methods.register = function(cb){
	this.save((err, c) => {
		// do wonderful things here
		ComplaintCategory
			.findOne({
				_id: this.category
			}, (err, cat) => {
				if(!err){
					cat.complaints.push(c._id);
					cat.save((err) => {
						if(!err){
							User.findOne({
								_id: this.author
							}, (err, user) => {
								if(!err && user){
									user.complaints.push(c._id);
									user.save((err) => {
										cb(err, c);
									})
								}else{
									cb(err);
								}
							})
						}else{
							cb(err);
						}
					})
				}else{
					cb(err);
				}
			})
	})
}

// callback(error)
complaintSchema.statics.delete = function(userId, id, cb){
	Complaint.findOne({
		_id: id
	}, (err, complaint) => {
		if(!err){
			if(complaint.author.toString() == userId){
				ComplaintCategory.findOne({
					_id: complaint.category
				}, (err, category) => {
					category.complaints.pull(complaint._id);
					category.save((err) => {
						User.findOne({
							_id: complaint.author
						}, (err, user) => {
							if(!err && user){
								user.complaints.pull(complaint._id);
								user.save((err) => {
									if(!err){
										complaint.remove((err) => {
											if(!err){
												cb(null);
											}else{
												cb(err);
											}
										})
									}else{
										cb(err);
									}
								});
							}else{
								res.json({success: false});
							}
						})
					});
				})
			}else{
				cb(new Error('can not lose what is not yours to begin with'));
			}
		}else{
			cb(err);
		}
	});
}

var Complaint = mongoose.model('Complaint', complaintSchema);

export default Complaint;