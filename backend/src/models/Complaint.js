import mongoose from 'mongoose';
import bcrypt from 'bcrypt';

var Schema = mongoose.Schema;
var complaintSchema = new Schema({
	title: String,
	description: String,
	author: {type: Schema.Types.ObjectId, ref: 'Complaint'}
});

var Complaint = mongoose.model('Complaint', complaintSchema);

export default Complaint;