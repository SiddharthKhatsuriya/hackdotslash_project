import Express from 'express';

const app = Express();

app.get('/api', (req, res) => res.json({status: 'running'}));
app.listen(3000, () => console.log('app listening on port 3000!'));