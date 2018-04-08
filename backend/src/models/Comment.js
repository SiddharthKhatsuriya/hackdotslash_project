import mongoose from 'mongoose';
import jwt from 'jsonwebtoken';
import bcrypt from 'bcrypt';

import config from './../config';

var Schema = mongoose.Schema;
var commentSchema = new Schema({
	text: {type: String},
	author: {type: Schema.Types.ObjectId, ref: 'Complaint'},
});

var Comment = mongoose.model('Comment', commentSchema);

export default Comment;