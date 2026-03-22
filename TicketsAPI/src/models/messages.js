const db = require('./sqlite');

/**
 * Create a new message in the database
 * Initially set as unread (READ = 0)
 * @param {number} sender - Sender user ID
 * @param {number} receiver - Receiver user ID
 * @param {string} content - Message content (encrypted)
 * @param {function} callback - Callback function with (error, messageId)
 */
const create = (sender, receiver, content, callback) => {
	const sql = `INSERT INTO [MESSAGES] ([SENDER], [RECEIVER], [CONTENT], [READ]) VALUES (?, ?, ?, 0)`;

	db.run(sql, [sender, receiver, content], function(error) {
		if (error) return callback(error);
		callback(null, this.lastID);
	});
};

/**
 * Mark a message as delivered
 * Updates DELIVERED flag and DELIVERED_AT timestamp
 * @param {number} id - Message ID
 * @param {function} callback - Callback function with (error, changes)
 */
const delivered = (id, callback) => {
	const sql = `UPDATE [MESSAGES] SET [DELIVERED] = 1, [DELIVERED_AT] = ? WHERE [ID] = ?`;

	db.run(sql, [new Date(), id], function(error) {
		if (error) return callback(error);
		callback(null, this.changes);
	});
};

/**
 * Get all conversations for a user including unread message counts
 * Returns one entry per ticket with last message and contact info
 * @param {number} userId - User ID to fetch conversations for
 * @param {function} callback - Callback function with (error, conversations)
 */
const getChats = (userId, callback) => {
	const sql = `
		WITH LastMessagePerTicket AS (
			SELECT
				T.ID AS TicketId,
				T.TITLE,
				T.STATE,
				T.CREATED_BY,
				T.RESPONSIBLE,
				M.CONTENT AS LastMessage,
				M.CREATED_AT AS SendedAt,
				CASE
					WHEN T.CREATED_BY = ? THEN T.RESPONSIBLE
					ELSE T.CREATED_BY
				END AS ContactId,
				ROW_NUMBER() OVER(PARTITION BY T.ID ORDER BY M.CREATED_AT DESC) as rn
			FROM TICKETS AS T
			LEFT JOIN TICKETS_CHATS AS TC ON T.ID = TC.TICKET
			LEFT JOIN MESSAGES AS M ON TC.MESSAGE = M.ID
			WHERE T.CREATED_BY = ? OR T.RESPONSIBLE = ?
		),
		UnreadCounts AS (
			SELECT 
				TC.TICKET,
				COUNT(*) as UnreadCount
			FROM MESSAGES M
			JOIN TICKETS_CHATS TC ON M.ID = TC.MESSAGE
			WHERE M.SENDER != ? 
			  AND (M.READ = 0 OR M.READ IS NULL)
			GROUP BY TC.TICKET
		)
		SELECT
			'ticket_' || LM.TicketId AS ConversationId,
			LM.TicketId,
			LM.TITLE,
			LM.STATE,
			LM.CREATED_BY,
			LM.RESPONSIBLE,
			LM.ContactId,
			U.USER_NAME as Contact,
			LM.LastMessage,
			LM.SendedAt,
			IFNULL(UC.UnreadCount, 0) as UNREAD_COUNT
		FROM LastMessagePerTicket AS LM
		LEFT JOIN USERS AS U ON U.ID = LM.ContactId
		LEFT JOIN UnreadCounts AS UC ON UC.TICKET = LM.TicketId
		WHERE LM.rn = 1;
	`;
	
	db.all(sql, [userId, userId, userId, userId], (error, rows) => {
		if (error) {
			console.error("Error in getChats:", error.message);
			return callback(error);
		}
		callback(null, rows);
	});
};

/**
 * Get messages from a conversation
 * Can retrieve either ticket-specific messages or direct messages between two users
 * @param {number} ticket - Ticket ID (null for direct messages)
 * @param {number} user - User ID
 * @param {number} contact - Contact user ID
 * @param {function} callback - Callback function with (error, messages)
 */
const getMessagesFromContact = (ticket, user, contact, callback) => {
	const conditional = (ticket == null) ? `(([M].[SENDER] = ? AND [M].[RECEIVER] = ?) OR ([M].[SENDER] = ? AND [M].[RECEIVER] = ?)) AND [T].[ID] IS NULL` : `[T].[ID] = ?`;
	
	const sql = `SELECT [M].[SENDER] AS [SENDER_ID], [M].[RECEIVER] AS [RECEIVER_ID], [S].[USER_NAME] AS [From], [R].[USER_NAME] AS [To], [M].[CONTENT], [M].[CREATED_AT], [M].[DELIVERED]
	FROM [MESSAGES] AS [M]
	INNER JOIN [USERS] AS [S] ON [S].[ID] = [M].[SENDER]
	INNER JOIN [USERS] AS [R] ON [R].[ID] = [M].[RECEIVER]
	LEFT JOIN [TICKETS_CHATS] AS [TC] ON [TC].[MESSAGE] = [M].[ID]
	LEFT JOIN [TICKETS] AS [T] ON [T].[ID] = [TC].[TICKET]
	WHERE ${conditional}
	ORDER BY [M].[CREATED_AT];`;

	const params = (ticket == null) ? [user, contact, contact, user] : [ticket];
	
	db.all(sql, params, (error, rows) => {
		if (error) return callback(error);
		callback(null, rows);
	});
};

/**
 * Send a message to a ticket (creates relationship in TICKETS_CHATS)
 * @param {number} ticket - Ticket ID
 * @param {number} message - Message ID
 * @param {function} callback - Callback function with (error, changes)
 */
const sendMessageToTicket = (ticket, message, callback) => {
	const sql = `INSERT INTO [TICKETS_CHATS] ([TICKET], [MESSAGE]) VALUES (?, ?);`;
	
	db.run(sql, [ticket, message], function(error) {
		if (error) return callback(error);
		callback(null, this.changes);
	});
};

/**
 * Mark all unread messages in a ticket as read
 * Only marks messages sent by others (not by the user)
 * @param {number} ticketId - Ticket ID
 * @param {number} userId - User ID marking messages as read
 * @param {function} callback - Callback function with (error, changes)
 */
const markAsRead = (ticketId, userId, callback) => {
	const query = `
		UPDATE MESSAGES 
		SET READ = 1 
		WHERE ID IN (
			SELECT MESSAGE FROM TICKETS_CHATS WHERE TICKET = ?
		)
		AND SENDER != ? 
		AND (READ = 0 OR READ IS NULL)
	`;

	db.run(query, [ticketId, userId], function(error) {
		if (error) return callback(error, null);
		callback(null, this.changes);
	});
};

/**
 * Get all concluded (closed) tickets for a user with pagination
 * Includes last message and unread count
 * @param {number} userId - User ID
 * @param {number} page - Page number (default 1)
 * @param {number} limit - Messages per page (default 15)
 * @param {function} callback - Callback function with (error, tickets)
 */
const getConcludedTickets = (userId, page = 1, limit = 15, callback) => {
	const offset = (page - 1) * limit;
	const sql = `
		WITH LastMessagePerTicket AS (
			SELECT
				T.ID AS TicketId, T.TITLE, T.STATE, T.CREATED_BY, T.RESPONSIBLE,
				M.CONTENT AS LastMessage, M.CREATED_AT AS SendedAt,
				CASE WHEN T.CREATED_BY = ? THEN T.RESPONSIBLE ELSE T.CREATED_BY END AS ContactId,
				ROW_NUMBER() OVER(PARTITION BY T.ID ORDER BY M.CREATED_AT DESC) as rn
			FROM TICKETS AS T
			LEFT JOIN TICKETS_CHATS AS TC ON T.ID = TC.TICKET
			LEFT JOIN MESSAGES AS M ON TC.MESSAGE = M.ID
			WHERE (T.CREATED_BY = ? OR T.RESPONSIBLE = ?) AND T.STATE = 1
		)
		SELECT 'ticket_' || LM.TicketId AS ConversationId, LM.TicketId, LM.TITLE, LM.STATE, 
			   LM.CREATED_BY, LM.RESPONSIBLE, LM.ContactId, U.USER_NAME as Contact,
			   LM.LastMessage, LM.SendedAt, 0 as UNREAD_COUNT
		FROM LastMessagePerTicket AS LM
		LEFT JOIN USERS AS U ON U.ID = LM.ContactId
		WHERE LM.rn = 1
		ORDER BY LM.SendedAt DESC
		LIMIT ? OFFSET ?;
	`;
	db.all(sql, [userId, userId, userId, limit, offset], callback);
};

module.exports = {
	create,
	delivered,
	getChats,
	getMessagesFromContact,
	sendMessageToTicket,
	markAsRead,
	getConcludedTickets
};