let complaintRoutes = require('express').Router();

complaintRoutes.get('/complaints/all', (req, res) => res.json({success: true}));
complaintRoutes.post('/complaints/create', (req, res) => res.json({success: true}));
complaintRoutes.get('/complaints/:idall', (req, res) => res.json({success: true}));

export default complaintRoutes;