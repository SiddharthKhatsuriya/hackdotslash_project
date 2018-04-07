import mongoose from 'mongoose';
import bcrypt from 'bcrypt';

var Schema = mongoose.Schema;
var complaintSchema = new Schema({
	title: {
		type: String,
		required: [true, 'title is required']
	},
	description: {
		type: String,
		required: [true, 'title is required']
	},
	image: String,
	category: {type: Schema.Types.ObjectId, ref: 'ComplaintCategory'},
	votes: {type: Number, default: 0},
	author: {type: Schema.Types.ObjectId, ref: 'User'}
});

complaintSchema.methods.register = function(cb){
	this.save((err, c) => {
		// do wonderful things here
		cb(err, c);
	})
}

var Complaint = mongoose.model('Complaint', complaintSchema);

export default Complaint;