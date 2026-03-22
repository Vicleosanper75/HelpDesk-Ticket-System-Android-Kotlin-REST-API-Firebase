const path = require('path');
const sqlite = require('sqlite3');

/**
 * Initialize SQLite Database Connection
 * Creates database.db file and initializes all required tables
 */
const db = new sqlite.Database(
	path.resolve(__dirname, '../../database.db'),
	(error) => {
		if (error) return console.error(error);

		// Enable foreign key constraints for data integrity
		db.run("PRAGMA foreign_keys = ON;");
		
		// ===== CREATE TABLES =====
		
		// User roles table (Operaria, Soporte, Sistemas)
		db.run(`CREATE TABLE IF NOT EXISTS [USER_ROLES] (
			[ID] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
			[ROLE_NAME] TEXT NOT NULL UNIQUE,
			[CREATED_AT] DATETIME NOT NULL DEFAULT (DATETIME(CURRENT_TIMESTAMP,'localtime')));`
		);

		// Users table with authentication data
		db.run(`CREATE TABLE IF NOT EXISTS [USERS] (
			[ID] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
			[USER_NAME] TEXT NOT NULL,
			[FULL_NAME] TEXT NOT NULL,
			[PWD] TEXT NOT NULL,
			[ONLINE] BIT NOT NULL DEFAULT 0,
			[CREATED_AT] DATETIME NOT NULL DEFAULT (DATETIME(CURRENT_TIMESTAMP,'localtime')),
			[ROLE] INTEGER NOT NULL,
			[ACTIVE] BIT NOT NULL DEFAULT 1,
			FOREIGN KEY([ROLE]) REFERENCES [USER_ROLES]([ID]));`
		);

		// Messages table with encrypted content
		db.run(`CREATE TABLE IF NOT EXISTS [MESSAGES] (
			[ID] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
			[SENDER] INTEGER NOT NULL,
			[RECEIVER] INTEGER NOT NULL,
			[CONTENT] TEXT NOT NULL,
			[CREATED_AT] DATETIME NOT NULL DEFAULT (DATETIME(CURRENT_TIMESTAMP,'localtime')),
			[DELIVERED] INTEGER NOT NULL DEFAULT 0,
			[DELIVERED_AT] DATETIME,
			FOREIGN KEY([SENDER]) REFERENCES [USERS]([ID]),
			FOREIGN KEY([RECEIVER]) REFERENCES [USERS]([ID]));`
		);

		// JWT Refresh Tokens
		db.run(`CREATE TABLE IF NOT EXISTS [TOKENS] (
			[BELONGS_TO] INTEGER NOT NULL,
			[TOKEN] TEXT NOT NULL PRIMARY KEY,
			[CREATED_AT] DATETIME NOT NULL DEFAULT (DATETIME(CURRENT_TIMESTAMP,'localtime')),
			FOREIGN KEY([BELONGS_TO]) REFERENCES [USERS]([ID]));`
		);

		// Support Tickets
		db.run(`CREATE TABLE IF NOT EXISTS [TICKETS] (
			[ID] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
			[TITLE] TEXT NOT NULL,
			[RESPONSIBLE] INTEGER NOT NULL,
			[STATE] BIT NOT NULL DEFAULT 0,
			[CREATED_BY] INTEGER NOT NULL,
			[CREATED_AT] DATETIME NOT NULL DEFAULT (DATETIME(CURRENT_TIMESTAMP,'localtime')),
			FOREIGN KEY([RESPONSIBLE]) REFERENCES [USERS]([ID]),
			FOREIGN KEY([CREATED_BY]) REFERENCES [USERS]([ID]));`
		);

		// Ticket-Message Relationship (Links messages to tickets)
		db.run(`CREATE TABLE IF NOT EXISTS [TICKETS_CHATS] (
			[TICKET] INTEGER NOT NULL,
			[MESSAGE] INTEGER NOT NULL,
			FOREIGN KEY([TICKET]) REFERENCES [TICKETS]([ID]),
			FOREIGN KEY([MESSAGE]) REFERENCES [MESSAGES]([ID]));`
		);
	}
);

module.exports = db;