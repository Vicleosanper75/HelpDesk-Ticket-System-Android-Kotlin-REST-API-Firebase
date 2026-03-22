const admin = require("firebase-admin");
const fs = require('fs');
const path = require('path');

/**
 * Initialize Firebase Admin SDK with service account credentials
 * Enables Cloud Messaging for push notifications
 * 
 * serviceAccountKey.json is required to use Firebase features
 * See serviceAccountKey-example.json for the required format
 */

const serviceAccountPath = path.join(__dirname, 'serviceAccountKey.json');

// Check if serviceAccountKey.json exists and initialize Firebase
if (fs.existsSync(serviceAccountPath)) {
	try {
		const serviceAccount = require("./serviceAccountKey.json");
		admin.initializeApp({
			credential: admin.credential.cert(serviceAccount)
		});
		console.log('✓ Firebase Admin SDK initialized successfully');
	} catch (error) {
		console.warn('⚠ Warning: Could not initialize Firebase Admin SDK');
		console.warn('Error: ' + error.message);
		console.warn('Push notifications will not work. See DOCUMENTATION.md for setup.');
	}
} else {
	console.warn('⚠ Warning: serviceAccountKey.json not found');
	console.warn('To enable push notifications, download your Firebase service account key from:');
	console.warn('  Firebase Console → Project Settings → Service Accounts → Generate New Private Key');
	console.warn('Then rename/copy it as serviceAccountKey.json in the project root');
	console.warn('See DOCUMENTATION.md for detailed Firebase setup instructions');
}

module.exports = admin;