import Express from 'express';

let userRoutes = require('express').Router();

userRoutes.post('/user/register', (req, res) => res.json({user: 'true'}));
userRoutes.post('/user/login', (req, res) => res.json({user: 'true'}));
userRoutes.get('/user/:username/profile', (req, res) => res.json({user: 'true'}));

export default userRoutes;