const model = require('../models/tickets');

/**
 * Create a new ticket
 * @param {number} createdBy - User ID of ticket creator
 * @param {string} title - Ticket title/description
 * @param {number} responsible - User ID of responsible person
 * @returns {object} Created ticket ID
 */
const createTicket = (req, res) => {
	const { createdBy, title, responsible } = req.body;
	
	model.createTicket(createdBy, title, responsible, (error, id) => {
		if (error) return res.status(500).send(`${error}`);
		res.status(201).json({ ID: id });
	});
};

/**
 * Mark a ticket as concluded
 * Ticket ID is provided in request body
 */
const concludeTicket = (req, res) => {
	const { id } = req.body;

	if (!id) {
		return res.status(400).json({ message: "Missing ticket ID" });
	}

	model.concludeTicket(id, (error, result) => {
		if (error) {
			console.error("Error concluding ticket:", error.message);
			return res.status(500).json({ message: "Internal server error" });
		}

		if (result.changes === 0) {
			return res.status(404).json({ message: "Ticket not found" });
		}

		res.status(200).json({ message: "Ticket concluded successfully" });
	});
};

/**
 * Get a single ticket by ID
 * @param {number} id - Ticket ID from URL params
 * @returns {object} Ticket details
 */
const getTicketById = (req, res) => {
	const { id } = req.params;

	model.findById(id, (error, ticket) => {
		if (error) {
			console.error("Error getting ticket:", error.message);
			return res.status(500).json({ message: "Internal server error" });
		}

		if (!ticket) {
			return res.status(404).json({ message: "Ticket not found" });
		}

		res.status(200).json(ticket);
	});
};

/**
 * Reassign a ticket to a different user
 * @param {number} id - Ticket ID
 * @param {number} newResponsibleId - New responsible user ID
 * @returns {object} Success message
 */
const reassignTicket = (req, res) => {
	const { id, newResponsibleId } = req.body;

	if (!id || !newResponsibleId) {
		return res.status(400).json({ message: "Missing data (id or newResponsibleId)" });
	}

	model.reassignTicket(id, newResponsibleId, (error, result) => {
		if (error) {
			console.error("Error reassigning ticket:", error.message);
			return res.status(500).json({ message: "Internal server error" });
		}

		if (result.changes === 0) {
			return res.status(404).json({ message: "Ticket not found" });
		}

		return res.status(200).json({ message: "Ticket reassigned successfully" });
	});
};

module.exports = {
	createTicket,
	concludeTicket,
	getTicketById,
	reassignTicket
};