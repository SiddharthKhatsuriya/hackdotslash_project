let complaintRoutes = require('express').Router();

complaintRoutes.get('/complaints/all', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/create', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/:id/edit', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/:id/delete', (req, res) => res.json({success: true}));
complaintRoutes.get('/complaints/:id/details', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/:id/status/update', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/:id/upvote', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/:id/downvote', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/:id/watch', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/:id/unwatch', (req, res) => res.json({success: true}));

export default complaintRoutes;