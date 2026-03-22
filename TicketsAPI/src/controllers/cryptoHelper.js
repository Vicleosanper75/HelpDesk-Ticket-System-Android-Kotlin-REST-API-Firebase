const crypto = require('crypto');

// Encryption key derived from secret phrase (32 bytes for AES-256)
const ENCRYPTION_KEY = crypto.createHash('sha256').update('clave-super-secreta-123').digest();
// Static IV for simplicity (should be random in production and stored with ciphertext)
const IV = Buffer.alloc(16, 0);

/**
 * Encrypt message text using AES-256-CBC
 * @param {string} text - Plain text to encrypt
 * @returns {string} Hexadecimal encrypted string
 */
function encrypt(text) {
	const cipher = crypto.createCipheriv('aes-256-cbc', ENCRYPTION_KEY, IV);
	let encrypted = cipher.update(text, 'utf8', 'hex');
	encrypted += cipher.final('hex');
	return encrypted;
}

/**
 * Decrypt encrypted message
 * @param {string} encrypted - Hexadecimal encrypted string
 * @returns {string} Decrypted plain text
 */
function decrypt(encrypted) {
	const decipher = crypto.createDecipheriv('aes-256-cbc', ENCRYPTION_KEY, IV);
	let decrypted = decipher.update(encrypted, 'hex', 'utf8');
	decrypted += decipher.final('utf8');
	return decrypted;
}

module.exports = { encrypt, decrypt };