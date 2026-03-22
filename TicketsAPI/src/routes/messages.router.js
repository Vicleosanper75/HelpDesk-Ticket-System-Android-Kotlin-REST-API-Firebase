/**
 * Message routes
 * Handles messaging between users, ticket chat management, and notification delivery
 */

const express = require("express");
const router = express.Router();
const auth = require('../../authMiddleware');

router.use(express.json());

const controller = require("../controllers/messages.controller");

// ===== MESSAGE OPERATIONS =====
router.post('/sendMessage', auth.authMiddleware, controller.sendMessage);
router.post('/markDelivered/:id', auth.authMiddleware, controller.markDelivered);
router.put('/markAsRead', auth.authMiddleware, controller.markAsRead);

// ===== CONVERSATION RETRIEVAL =====
router.get('/getChats/:id', auth.authMiddleware, controller.receiveChats);
router.get('/getMessages', auth.authMiddleware, controller.receiveMessages);
router.get('/getConcludedTickets', auth.authMiddleware, controller.getConcluded);

module.exports = router;