import Express from 'express';

import User from './../models/User';
import ComplaintCategory from './../models/ComplaintCategory';
import RequireToken from './../middlewares/RequireToken';
import AdminCheck from './../middlewares/AdminCheck';

let categoriesRoutes = require('express').Router();


// auth: admin only
// params: 
categoriesRoutes.get('/complaints/categories/all', (req, res) => {
	ComplaintCategory
		.find()
		.select('-complaints')
		.exec((err, categories) => {
			if(!err){
				res.json({success: true, categories: categories});
			}else{
				res.json({success: false});
			}
		})
});


// params: title
categoriesRoutes.post('/complaints/categories/add', [RequireToken, AdminCheck], (req, res) => {
	let category = new ComplaintCategory();
	category.title = req.body.title;
	category.create((err, c) => {
		if(!err)
			res.json({success: true, id: c._id});
		else
			res.json({success: false, message: err.message});
	});
});

export default categoriesRoutes;
