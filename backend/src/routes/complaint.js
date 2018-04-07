import RequireToken from './../middlewares/RequireToken';
import Complaint from './../models/Complaint';

let complaintRoutes = require('express').Router();


// params: title, category(id), description
complaintRoutes.post('/complaints/create', [RequireToken], (req, res) => {
	// lock and load
	let complaint = new Complaint();
	complaint.title = req.body.title;
	complaint.category = req.body.category;
	complaint.description = req.body.description;
	complaint.author = req.decoded.userId;
	// fire
	complaint.register((err, c) => {
		if(!err)
			res.json({success: true, id: c._id});
		else
			res.json({success: false});
	});
});

complaintRoutes.get('/complaints/all', (req, res) => {
	Complaint
		.find()
		.exec((err, complaints) => {
			if(!err){
				res.json({success: true, complaints: complaints})
			}else{
				res.json({success: false});
			}
		});
});

complaintRoutes.post('/complaints/:id/edit', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/:id/delete', (req, res) => res.json({success: true}));
complaintRoutes.get('/complaints/:id/details', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/:id/status/update', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/:id/upvote', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/:id/downvote', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/:id/watch', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/:id/unwatch', (req, res) => res.json({success: true}));

export default complaintRoutes;