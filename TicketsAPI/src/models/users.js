const db = require('./sqlite');

/**
 * Create a new user in the database
 * @param {string} username - User's login username
 * @param {string} fullname - User's full name
 * @param {string} pwd - Hashed password
 * @param {number} role - User role ID
 * @param {function} callback - Callback function with (error, userId)
 */
const create = (username, fullname, pwd, role, callback) => {
	const sql = `INSERT INTO [USERS] ([USER_NAME], [FULL_NAME], [PWD], [ROLE]) VALUES (?, ?, ?, ?);`;

	db.run(sql, [username, fullname, pwd, role], function(error) {
		if (error) return callback(error);
		callback(null, this.lastID);
	});
};

/**
 * Find user by username and verify they are active
 * @param {string} username - User's login username
 * @param {function} callback - Callback function with (error, user)
 */
const login = (username, callback) => {
	const sql = `SELECT * FROM [USERS] WHERE [USER_NAME] = ? AND [ACTIVE] = 1;`;

	db.get(sql, [username], (error, user) => {
		if (error) return callback(error);
		callback(null, user);
	});
};

/**
 * Store a refresh token in the database for JWT authentication
 * @param {number} id - User ID
 * @param {string} token - Refresh token value
 * @param {function} callback - Callback function with (error)
 */
const loginToken = (id, token, callback) => {
	const sql = `INSERT INTO [TOKENS] ([BELONGS_TO], [TOKEN]) VALUES (?, ?);`;

	db.run(sql, [id, token], function(error) {
		if (error) return callback(error);
		callback(null);
	});
};

/**
 * Check if a refresh token exists in the database
 * @param {string} token - Refresh token to verify
 * @param {function} callback - Callback function with (error, exists)
 */
const tokenExistence = (token, callback) => {
	const sql = `SELECT COUNT(*) AS [Count] FROM [TOKENS] WHERE [TOKEN] = ?;`;

	db.get(sql, [token], (error, result) => {
		if (error) return callback(error);
		callback(null, result.Count > 0);
	});
};

/**
 * Delete a refresh token (logout)
 * @param {string} token - Refresh token to remove
 * @param {function} callback - Callback function with (error)
 */
const logoutToken = (token, callback) => {
	const sql = `DELETE FROM [TOKENS] WHERE [TOKEN] = ?;`;

	db.run(sql, [token], function(error) {
		if (error) return callback(error);
		callback(null);
	});
};

/**
 * Clear all tokens from the database
 * Used for system maintenance
 * @param {function} callback - Callback function with (error, changes)
 */
const clearTokens = (callback) => {
	const sql = `DELETE FROM tokens;`;
	
	db.run(sql, function (error) {
		if (error) return callback(error);
		callback(null, this.changes);
	});
};

/**
 * Get all users with their role information
 * Joins USERS and USER_ROLES tables
 * @param {function} callback - Callback function with (error, users)
 */
const findAll = (callback) => {
	const sql = `SELECT [U].[ID], [U].[USER_NAME], [U].[FULL_NAME], [U].[PWD], [U].[ONLINE], [U].[CREATED_AT], [U].[ROLE], [R].[ROLE_NAME], [U].[ACTIVE]
	FROM [USERS] AS [U]
	INNER JOIN [USER_ROLES] AS [R] ON [U].[ROLE] = [R].[ID];`;

	db.all(sql, (error, rows) => {
		if (error) return callback(error);
		callback(null, rows);
	});
};

/**
 * Find a single user by ID with role information
 * @param {number} id - User ID
 * @param {function} callback - Callback function with (error, user)
 */
const findById = (id, callback) => {
	const sql = `SELECT [U].[ID], [U].[USER_NAME], [U].[FULL_NAME], [U].[PWD], [U].[ONLINE], [U].[CREATED_AT], [U].[ROLE], [R].[ROLE_NAME], [U].[ACTIVE]
	FROM [USERS] AS [U]
	INNER JOIN [USER_ROLES] AS [R] ON [U].[ROLE] = [R].[ID]
	WHERE [U].[ID] = ?;`;

	db.get(sql, [id], (error, row) => {
		if (error) return callback(error);
		callback(null, row);
	});
};

/**
 * Update user information
 * All fields (username, fullname, password) are optional
 * @param {number} id - User ID
 * @param {string} username - New username (optional)
 * @param {string} fullname - New full name (optional)
 * @param {string} pwd - New hashed password (optional)
 * @param {function} callback - Callback function with (error, changes)
 */
const update = (id, username, fullname, pwd, callback) => {
	const sql = `UPDATE [USERS] SET [USER_NAME] = COALESCE(?, [USER_NAME]), [FULL_NAME] = COALESCE(?, [FULL_NAME]), [PWD] = COALESCE(?, [PWD]) WHERE [ID] = ?;`;
	
	db.run(sql, [username, fullname, pwd, id], function (error) {
		if (error) return callback(error);
		callback(null, this.changes);
	});
};

/**
 * Update user's FCM (Firebase Cloud Messaging) token
 * Used for push notifications
 * @param {number} id - User ID
 * @param {string} token - FCM token from client
 * @param {function} callback - Callback function with (error, changes)
 */
const updateFcmToken = (id, token, callback) => {
	const sql = `UPDATE [USERS] SET [fcm_token] = ? WHERE [ID] = ?;`;

	db.run(sql, [token, id], function(error) {
		if (error) return callback(error);
		callback(null, this.changes);
	});
};

/**
 * Set user online/offline status
 * @param {number} id - User ID
 * @param {boolean} isOnline - Online status
 * @param {function} callback - Callback function with (error, changes)
 */
const setOnlineStatus = (id, isOnline, callback) => {
	const sql = `UPDATE [USERS] SET [ONLINE] = ? WHERE [ID] = ?;`;
	
	db.run(sql, [isOnline, id], function (error) {
		if (error) return callback(error);
		callback(null, this.changes);
	});
};

/**
 * Soft delete user (sets ACTIVE to 0)
 * Does not physically remove user from database
 * @param {number} id - User ID
 * @param {function} callback - Callback function with (error, changes)
 */
const destroy = (id, callback) => {
	const sql = `UPDATE [USERS] SET [ACTIVE] = 0 WHERE [ID] = ?;`;
	
	db.run(sql, [id], function (error) {
		if (error) {
			return callback(error);
		}
		callback(null, this.changes);
	});
};

/**
 * Get all available user roles
 * @param {function} callback - Callback function with (error, roles)
 */
const getRoles = (callback) => {
	const sql = `SELECT [ID], [ROLE_NAME], [CREATED_AT] FROM [USER_ROLES] ORDER BY [ROLE_NAME];`;
	db.all(sql, [], (error, rows) => {
		if (error) return callback(error);
		callback(null, rows);
	});
};

/**
 * Reset user password
 * @param {string} username - User's full name to identify user
 * @param {string} hashedPwd - New hashed password
 * @param {function} callback - Callback function with (error, changes)
 */
const resetPassword = (username, hashedPwd, callback) => {
	const sql = `UPDATE [USERS] SET [PWD] = ? WHERE [FULL_NAME] = ?;`;

	db.run(sql, [hashedPwd, username], function (error) {
		if (error) return callback(error);
		callback(null, this.changes);
	});
};

/**
 * Get user's FCM token for push notifications
 * @param {number} id - User ID
 * @param {function} callback - Callback function with (error, token)
 */
const getFcmToken = (id, callback) => {
	const sql = `SELECT [fcm_token] FROM [USERS] WHERE [ID] = ?;`;

	db.get(sql, [id], (error, row) => {
		if (error) return callback(error);
		callback(null, row ? row.fcm_token : null);
	});
};

module.exports = {
	create,
	login,
	loginToken,
	tokenExistence,
	logoutToken,
	clearTokens,
	findAll,
	findById,
	update,
	setOnlineStatus,
	destroy,
	getRoles,
	updateFcmToken,
	resetPassword,
	getFcmToken
};