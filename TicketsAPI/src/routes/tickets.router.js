/**
 * Ticket routes
 * Handles support ticket creation, management, and assignment
 */

const express = require("express");
const router = express.Router();
const auth = require('../../authMiddleware');
const controller = require("../controllers/tickets.controller");

router.use(express.json());

// ===== TICKET OPERATIONS =====
router.post('/createTicket', auth.authMiddleware, controller.createTicket);
router.put("/concludeTicket", auth.authMiddleware, controller.concludeTicket);
router.put("/reassignTicket", auth.authMiddleware, controller.reassignTicket);
router.get('/:id', auth.authMiddleware, controller.getTicketById);

module.exports = router;