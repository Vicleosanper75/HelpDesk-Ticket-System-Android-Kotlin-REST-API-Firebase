/**
 * Main routes
 * Health check endpoint for API status
 */

const express = require("express");
const router = express.Router();

const controller = require("../controllers/main.controller");

router.get('/', controller.index);

module.exports = router;