/**
 * SQLite3 Reference Queries
 * Useful queries for database inspection and testing
 * Ctrl+Shift+Q: Execute query (in SQLite DB tool)
 */

-- ===== USER ROLES =====
-- Insert user roles (run once during database initialization)
-- INSERT INTO [USER_ROLES] ([ROLE_NAME]) VALUES ("Operaria");
-- INSERT INTO [USER_ROLES] ([ROLE_NAME]) VALUES ("Soporte");
-- INSERT INTO [USER_ROLES] ([ROLE_NAME]) VALUES ("Sistemas");

-- Retrieve all user roles
SELECT [ID], [ROLE_NAME], [CREATED_AT]
FROM [USER_ROLES];

-- ===== USERS =====
-- Get list of all users with their roles
SELECT [U].[ID], [U].[USER_NAME], [U].[FULL_NAME], [U].[PWD], [U].[ONLINE], [U].[CREATED_AT], [R].[ROLE_NAME] AS [Role], [U].[ACTIVE]
FROM [USERS] AS [U]
INNER JOIN [USER_ROLES] AS [R] ON [U].[ROLE] = [R].[ID];

-- ===== AUTHENTICATION =====
-- Get all currently active tokens
SELECT [U].[FULL_NAME], [T].[TOKEN], [T].[CREATED_AT]
FROM [TOKENS] AS [T]
INNER JOIN [USERS] AS [U] ON [T].[BELONGS_TO] = [U].[ID];

-- ===== MESSAGES =====
-- Get messages from a specific conversation (between users 4 and 1)
SELECT [S].[USER_NAME] AS [From], [R].[USER_NAME] AS [To], [M].[CONTENT], [M].[CREATED_AT], [M].[DELIVERED]
FROM [MESSAGES] AS [M]
INNER JOIN [USERS] AS [S] ON [S].[ID] = [M].[SENDER]
INNER JOIN [USERS] AS [R] ON [R].[ID] = [M].[RECEIVER]
LEFT JOIN [TICKETS_CHATS] AS [TC] ON [TC].[MESSAGE] = [M].[ID]
LEFT JOIN [TICKETS] AS [T] ON [T].[ID] = [TC].[TICKET]
WHERE (([M].[SENDER] = 4 AND [M].[RECEIVER] = 1) OR ([M].[SENDER] = 1 AND [M].[RECEIVER] = 4)) AND [T].[ID] = 1
ORDER BY [M].[CREATED_AT];

-- ===== CONTACTS =====
-- Get list of contacts with last message (for user 1)
SELECT [Contact], [LastMessage], [SendedBy], [SendedAt]
FROM (
	SELECT *, ROW_NUMBER() OVER (PARTITION BY [Contact] ORDER BY [SendedAt] DESC) AS [Row_Num]
	FROM (
		SELECT CASE WHEN [M].[SENDER] = 1 THEN [R].[USER_NAME] WHEN [M].[RECEIVER] = 1 THEN [S].[USER_NAME] END AS [Contact], [M].[CONTENT] AS [LastMessage], [S].[USER_NAME] AS [SendedBy], [M].[CREATED_AT] AS [SendedAt]
		FROM [MESSAGES] AS [M]
		INNER JOIN [USERS] AS [S] ON [S].[ID] = [M].[SENDER]
		INNER JOIN [USERS] AS [R] ON [R].[ID] = [M].[RECEIVER]
		LEFT JOIN [TICKETS_CHATS] AS [TC] ON [TC].[MESSAGE] = [M].[ID]
		LEFT JOIN [TICKETS] AS [T] ON [T].[ID] = [TC].[TICKET]
		WHERE [Contact] IS NOT NULL AND [T].[ID] IS NULL
	)
) AS [T]
WHERE [T].[Row_Num] = 1;

