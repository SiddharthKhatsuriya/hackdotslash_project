import RequireToken from './../middlewares/RequireToken';

import User from './../models/User';
import Comment from './../models/Comment';
import Complaint from './../models/Complaint';

let commentsRoutes = require('express').Router();

commentsRoutes.get('/complaints/:id/comments/all', RequireToken, (req, res) => {
	Complaint
		.findOne({
			_id: req.params.id
		})
		.populate({
			path: 'comments',
			populate: {
				path: 'author',
				select: 'fname'
			}
		})
		.exec((err, complaint) => {
			if(!err){
				res.json({success: true, comments: complaint.comments});
			}else{
				res.json({success: false});
			}
		});
});

// params: text
commentsRoutes.post('/complaints/:id/comments/post', RequireToken, (req, res) => {
	Complaint
		.findOne({
			_id: req.params.id
		})
		.exec((err, complaint) => {
			if(!err){
				let comment = new Comment();
				comment.text = req.body.text;
				comment.author = req.decoded.userId;
				comment.save((err, c) => {
					if(!err){
						complaint.comments.push(c._id);
						complaint.save(err => {
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
			}else{
				res.json({success: false});
			}
		});
});

export default commentsRoutes;