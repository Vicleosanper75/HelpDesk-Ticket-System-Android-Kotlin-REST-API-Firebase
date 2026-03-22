const model = require('../models/messages');
const { encrypt, decrypt } = require('./cryptoHelper');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
const userModel = require('../models/users'); 
const admin = require('../../firebaseAdmin');

// ===== MULTER CONFIGURATION - FILE UPLOAD HANDLING =====
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        const dir = 'uploads';
        if (!fs.existsSync(dir)){
            fs.mkdirSync(dir);
        }
        cb(null, dir);
    },
    filename: function (req, file, cb) {
        // Use timestamp to ensure unique filename
        cb(null, Date.now() + path.extname(file.originalname));
    }
});

const upload = multer({ storage: storage });

/**
 * Send message with optional image attachment
 * Encrypts message content and sends push notification to recipient
 */
const sendMessage = (req, res) => {
    upload.single('image')(req, res, function (err) {
        if (err) {
            return res.status(500).send(`File upload error: ${err}`);
        }

        const { ticket, sender, receiver, content } = req.body;
        let finalContent = content;

        // If image was uploaded, format as [image]path/to/file.jpg
        if (req.file) {
            finalContent = `[image]${req.file.path.replace(/\\/g, "/")}`;
        }

        if (!finalContent) {
            return res.status(400).send("Message must contain text or image");
        }

        model.create(sender, receiver, encrypt(finalContent), (error, id) => {
            if (error) return res.status(500).send(`${error}`);

            // Send push notification to receiver
            if (receiver) {
                userModel.getFcmToken(receiver, (err, fcmToken) => {
                    if (!err && fcmToken) {
                        const messagePayload = {
                            token: fcmToken,
                            notification: {
                                title: "New Message",
                                body: finalContent.startsWith('[image]') ? '📷 Photo' : finalContent
                            },
                            data: {
                                senderId: sender.toString(),
                                ticketId: ticket ? ticket.toString() : "null"
                            }
                        };

                        admin.messaging().send(messagePayload)
                            .catch((error) => console.log("Notification error:", error));
                    }
                });
            }       

            // Link message to ticket if provided
            if (!ticket || ticket === "null" || ticket === "undefined") {
                return res.json({ id: id });
            }

            model.sendMessageToTicket(ticket, id, (error, changes) => {
                if (error) return res.status(500).send(`${error}`);
                res.json({ id: id, changes: changes });
            });
        });
    });
};

/**
 * Mark a single message as delivered
 */
const markDelivered = (req, res) => {
	const { id } = req.params;

	model.delivered(id, (error, changes) => {
		if (error) return res.status(500).send(`${error}`);
		res.json(changes);
	});
};

/**
 * Get chat list with last message for each contact
 * Automatically decrypts message content
 */
const receiveChats = (req, res) => {
	const { id } = req.params;

	model.getChats(id, (error, contacts) => {
		if (error) return res.status(500).send(`${error}`);
		
		// Decrypt last message for each contact
		contacts.forEach(contact => {
			if (contact.LastMessage != null) {
				contact.LastMessage = decrypt(contact.LastMessage);
			}
		});

		res.json(contacts);
	});
}

/**
 * Get all messages in a conversation
 * Supports filtering by ticket, user and contact
 */
const receiveMessages = (req, res) => {
	const { ticket, user, contact } = req.query;
	
	model.getMessagesFromContact(ticket, user, contact, (error, messages) => {
		if (error) return res.status(500).send(`${error}`);
		
		// Decrypt all message content
		messages.forEach(message => {
			message.CONTENT = decrypt(message.CONTENT);
		});

		res.json(messages);
	});
};

/**
 * Mark messages as read for a ticket
 */
const markAsRead = (req, res) => {
    const { ticketId, userId } = req.body;

    model.markAsRead(ticketId, userId, (error, count) => {
        if (error) return res.status(500).send(error.message);
        res.json({ status: "success", updated: count });
    });
};

/**
 * Get concluded/closed tickets with pagination
 * Returns latest messages for each ticket
 */
const getConcluded = (req, res) => {
    const { userId, page, limit } = req.query;
    const p = parseInt(page) || 1;
    const l = parseInt(limit) || 15;

    model.getConcludedTickets(userId, p, l, (error, rows) => {
        if (error) return res.status(500).send(error.message);
        
        // Decrypt messages before sending
        rows.forEach(r => {
            if(r.LastMessage) r.LastMessage = decrypt(r.LastMessage);
        });
        
        res.json(rows);
    });
};

module.exports = {
	sendMessage,
	markDelivered,
	receiveChats,
	receiveMessages,
    markAsRead,
    getConcluded
}