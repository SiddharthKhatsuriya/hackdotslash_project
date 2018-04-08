import RequireToken from './../middlewares/RequireToken';

import User from './../models/User';
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
		.select('-__v')
		.exec((err, complaints) => {
			if(!err){
				res.json({success: true, complaints: complaints})
			}else{
				res.json({success: false});
			}
		});
});

complaintRoutes.post('/complaints/:id/edit', (req, res) => res.json({success: true}));

// query: id
complaintRoutes.post('/complaints/:id/delete', [RequireToken], (req, res) => {
	Complaint.delete(req.decoded.userId, req.params.id, (err) => {
		if(!err)
			res.json({success: true});
		else
			res.json({success: false});
	});
});

// query: id
complaintRoutes.get('/complaints/:id/details', (req, res) => {
	Complaint
		.findOne({
			_id: req.params.id
		})
		.select('-__v')
		.exec((err, c) => {
			if(!err && c)
				res.json({success: true, complaint: c});
			else
				res.json({success: false});
		});
});

complaintRoutes.post('/complaints/:id/status/update', (req, res) => res.json({success: true}));

complaintRoutes.post('/complaints/:id/upvote', [RequireToken], (req, res) => {
	Complaint.findOne({
		_id: req.params.id
	}, (err, complaint) => {
		if(!err && complaint){

		}else{
			res.json({success: false});
		}
	})
});

complaintRoutes.post('/complaints/:id/downvote', [RequireToken], (req, res) => {

});

// params: token
complaintRoutes.post('/complaints/:id/watch', [RequireToken], (req, res) => {
	User.findOne({
		_id: req.decoded.userId
	}, (err, user) => {
		if(!err && user){
			Complaint.findOne({
				_id: req.params.id
			}, (err, complaint) => {
				if(!err && complaint){
					let alreadyWatching = user.watched.some((el) => {
						if(el.toString() == req.params.id)
							return true;
					});
					if(!alreadyWatching){
						complaint.watchers.push(user._id);
						complaint.save((err) => {
							if(!err){
								user.watched.push(complaint._id);
								user.save((err) => {
									if(!err){
										res.json({success: true});
									}else{
										res.json({success: false});
									}
								});
							}else{
								res.json({success: false});
							}
						});
					}else{
						res.json({success: false});
					}
				}else{
					res.json({success: false});
				}
			});
		}else{
			res.json({success: false});
		}
	})
});

// params: token
complaintRoutes.post('/complaints/:id/unwatch', [RequireToken], (req, res) => {
	Complaint.findOne({
		_id: req.params.id
	}, (err, complaint) => {
		User.findOne({
			_id: req.decoded.userId
		}, (err, user) => {
			if(complaint && user){
				complaint.watchers.pull(user._id);
				complaint.save((err) => {
					if(!err){
						user.watched.pull(complaint._id);
						user.save((err) => {
							if(!err){
								res.json({success: true});
							}else{
								res.json({success: false});
							}
						})
					}else{
						res.json({success: false});
					}
				})
			}
		});
	})
});

export default complaintRoutes;