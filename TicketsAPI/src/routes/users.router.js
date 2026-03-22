/**
 * User routes
 * Handles user authentication, registration, and profile management
 */

const express = require("express");
const router = express.Router();
const auth = require('../../authMiddleware');

router.use(express.json());

const controller = require("../controllers/users.controller");

// ===== AUTHENTICATION ROUTES =====
router.post('/register', controller.register);
router.post('/login', controller.login);
router.post('/token', controller.token);
router.post('/logout', controller.logout);

// ===== PROTECTED ROUTES =====
router.post('/clearTokens', auth.authMiddleware, controller.clearTokens);
router.get('/roles', auth.authMiddleware, controller.receiveRoles);

// ===== USER MANAGEMENT ROUTES =====
router.get('/', auth.authMiddleware, controller.index);
router.get('/:id', auth.authMiddleware, controller.getOne);
router.post('/update/:id', auth.authMiddleware, controller.update);
router.post('/updateToken', auth.authMiddleware, controller.updateToken);
router.post('/setOnlineStatus/:id', auth.authMiddleware, controller.setOnlineStatus);
router.post('/delete/:id', auth.authMiddleware, controller.destroy);
router.post('/reset-password', auth.authMiddleware, controller.resetPassword);

module.exports = router;