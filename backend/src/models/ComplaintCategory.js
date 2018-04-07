import mongoose from 'mongoose';

var Schema = mongoose.Schema;
var complaintCategorySchema = new Schema({
	title: {type: String, required: true},
	complaints: [{type: Schema.Types.ObjectId, ref: 'Complaint'}]
});

complaintCategorySchema.methods.create = function(cb){
	this.save((err, c) => {
		if(!err)
			cb(null, c);
		else
			cb(err);
	});
}

var ComplaintCategory = mongoose.model('ComplaintCategory', complaintCategorySchema);

export default ComplaintCategory;