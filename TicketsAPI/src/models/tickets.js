const db = require('./sqlite');

/**
 * Create a new support ticket
 * @param {number} userCreatedBy - User ID of ticket creator
 * @param {string} title - Ticket title/description
 * @param {number} userResponsible - User ID assigned to handle ticket
 * @param {function} callback - Callback function with (error, ticketId)
 */
const createTicket = (userCreatedBy, title, userResponsible, callback) => {
	const sql = `INSERT INTO [TICKETS] ([CREATED_BY], [TITLE], [RESPONSIBLE]) VALUES (?, ?, ?);`;
	
	db.run(sql, [userCreatedBy, title, userResponsible], function(error) {
		if (error) return callback(error);
		callback(null, this.lastID);
	});
};

/**
 * Mark a ticket as concluded (closed)
 * Sets STATE = 1 to indicate completed ticket
 * @param {number} ticketId - Ticket ID to conclude
 * @param {function} callback - Callback function with (error, result)
 */
const concludeTicket = (ticketId, callback) => {
	const sql = `UPDATE TICKETS SET STATE = 1 WHERE ID = ?;`;

	db.run(sql, [ticketId], function(error) {
		if (error) return callback(error);
		callback(null, { changes: this.changes });
	});
};

/**
 * Get a ticket by ID
 * @param {number} ticketId - Ticket ID to retrieve
 * @param {function} callback - Callback function with (error, ticket)
 */
const findById = (ticketId, callback) => {
	const sql = `SELECT * FROM TICKETS WHERE ID = ?;`;

	db.get(sql, [ticketId], (error, row) => {
		if (error) return callback(error);
		callback(null, row);
	});
};

/**
 * Reassign a ticket to a different user
 * @param {number} ticketId - Ticket ID to reassign
 * @param {number} newResponsibleId - New user ID to assign ticket to
 * @param {function} callback - Callback function with (error, result)
 */
const reassignTicket = (ticketId, newResponsibleId, callback) => {
	const sql = `UPDATE TICKETS SET RESPONSIBLE = ? WHERE ID = ?;`;

	db.run(sql, [newResponsibleId, ticketId], function (error) {
		if (error) return callback(error);
		callback(null, { changes: this.changes });
	});
};

module.exports = {
	createTicket,
	concludeTicket,
	findById,
	reassignTicket
};