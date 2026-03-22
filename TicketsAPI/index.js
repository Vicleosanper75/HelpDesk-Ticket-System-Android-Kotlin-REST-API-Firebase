require('dotenv').config();

const express = require("express");
const path = require('path');

const app = express();

// ===== ROUTE DEFINITIONS =====
app.use(require('./src/routes/main.router'));
app.use('/users', require('./src/routes/users.router'));
app.use('/messages', require('./src/routes/messages.router'));
app.use('/tickets', require('./src/routes/tickets.router'));
app.use('/uploads', express.static(path.join(__dirname, 'uploads'))); // Static files for uploaded images

// ===== SERVER INITIALIZATION =====
const PORT = process.env.PORT || 3001;

app.listen(PORT, () => console.log(`Server running at http://localhost:${PORT}`));
