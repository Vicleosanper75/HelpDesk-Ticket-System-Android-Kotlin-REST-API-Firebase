const jwt = require('jsonwebtoken');
const SECRET_KEY = process.env.ACCESS_TOKEN_SECRET;
const REFRESH_KEY = process.env.REFRESH_TOKEN_SECRET;

/**
 * Middleware to validate JWT tokens on protected routes
 * Extracts token from Authorization header and verifies it
 */
const authMiddleware = (req, res, next) => {
	const authHeader = req.headers['authorization'];
	const token = authHeader && authHeader.split(' ')[1];

	if (token == null) return res.status(401).json({ error: 'Missing token' });

	jwt.verify(token, SECRET_KEY, (err, decoded) => {
		if (err) return res.status(403).json({ error: 'Invalid token' });
		req.user = decoded;
		next();
	});
};

/**
 * Generate a new access token with 9-hour expiration
 * @param {Object} user - User data to include in token
 * @returns {string} Signed JWT access token
 */
const generateAccessToken = (user) => {
	return jwt.sign(user, SECRET_KEY, { expiresIn: '9h' });
};

/**
 * Generate a refresh token with no expiration
 * Used to obtain new access tokens when expired
 * @param {Object} user - User data to include in token
 * @returns {string} Signed JWT refresh token
 */
const generateRefreshToken = (user) => {
	return jwt.sign(user, REFRESH_KEY);
};

/**
 * Verify and decode a refresh token
 * @param {string} refreshToken - The refresh token to verify
 * @param {Function} callback - Returns (error, decoded)
 */
const regenerateAccessToken = (refreshToken, callback) => {
	jwt.verify(refreshToken, REFRESH_KEY, callback);
};

module.exports = {
	authMiddleware,
	generateAccessToken,
	generateRefreshToken,
	regenerateAccessToken
};