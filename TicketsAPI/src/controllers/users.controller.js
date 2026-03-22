const model = require('../models/users');
const bcrypt = require('bcrypt');
const auth = require('../../authMiddleware');

/**
 * Register a new user
 * Password is hashed with bcrypt before storing
 */
const register = async (req, res) => {
	const { username, fullname, pwd, role } = req.body;

	try {
		const hashedPwd = await bcrypt.hash(pwd, 10);
		model.create(username, fullname, hashedPwd, role, (error, id) => {
			if (error) return res.status(500).send(`${error}`);
			res.status(201).json({ id: id });
		});
	} catch (exception) {
		return res.status(500).send(`${exception}`);
	}
};

/**
 * User login
 * Validates credentials and returns access + refresh tokens
 */
const login = (req, res) => {
	const { username, pwd } = req.body;
	model.login(username, async (error, user) => {
		if (error) return res.status(500).send(`${error}`);

		if (user == null) return res.status(404).send("User not found");

		try {
			if (await bcrypt.compare(pwd, user.PWD)) {
				const jsonUser = { name: username }
				const accessToken = auth.generateAccessToken(jsonUser);
				const refreshToken = auth.generateRefreshToken(jsonUser);
				model.loginToken(user.ID, refreshToken, (error) => {
					if (error) return res.status(500).send(`${error}`);
					res.status(200).json({ accessToken: accessToken, refreshToken: refreshToken });
				});
			} else {
				res.status(400).send("Invalid password");
			}
		} catch (exception) {
			return res.status(500).send(`${exception}`);
		}
	});
};

/**
 * Refresh access token using refresh token
 * Returns new access token with extended expiration
 */
const token = (req, res) => {
	const refreshToken = req.body.token;
	if (refreshToken == null) return res.status(401).send("No refresh token provided");
	
	model.tokenExistence(refreshToken, (error, alreadyExists) => {
		if (error) return res.status(500).send(`${error}`);

		if (!alreadyExists) return res.status(403).send("Token does not exist");

		auth.regenerateAccessToken(refreshToken, (error, user) => {
			if (error) return res.status(403).send(`${error}`);
			const accessToken = auth.generateAccessToken({ name: user.name });
			res.status(200).json({ accessToken: accessToken });
		});
	});
};

/**
 * Logout user
 * Removes refresh token from database
 */
const logout = (req, res) => {
	const refreshToken = req.body.token;
	if (refreshToken === null ||refreshToken === undefined || refreshToken === "") 
		return res.status(401).send("No token provided");
	
	model.logoutToken(refreshToken, (error) => {
		if (error) return res.status(500).send(`${error}`);
		res.status(204).send("Logout successful");
	});
}

/**
 * Clear all tokens from database
 * Requires special credentials for security
 */
const clearTokens = (req, res) => {
	const { fideman, tempranito } = req.body;

	if (fideman !== "Ou Yeah" || tempranito !== "Sííí") 
		return res.status(403).send("Forbidden");

	model.clearTokens((error, changes) => {
		if (error) return res.status(500).send(`${error}`);
		res.json(changes);
	});
};

/**
 * Get all users with their role information
 */
const index = (req, res) => {
	model.findAll((error, users) => {
		if (error) return res.status(500).send(`${error}`);
		res.json(users);
	});
};

/**
 * Get a single user by ID
 */
const getOne = (req, res) => {
	const { id } = req.params;

	model.findById(id, (error, user) => {
		if (error) return res.status(500).send(`${error}`);

		if (!user) return res.status(404).send("User does not exist");
		res.json(user);
	});
};

/**
 * Update user information
 * All fields are optional except user ID
 */
const update = async (req, res) => {
	const { id } = req.params;
	const { username, fullname, pwd } = req.body;
	
	try {
		const hashedPwd = (pwd != null) ? await bcrypt.hash(pwd, 10) : null;
		
		model.update(id, username, fullname, hashedPwd, (error, changes) => {
			if (error) return res.status(500).send(`${error}`);
			res.status(200).json(changes);
		});
	} catch (exception) {
		return res.status(500).send(`${exception}`);
	}
};

/**
 * Update user FCM token for push notifications
 */
const updateToken = (req, res) => {
	const { userId, fcmToken } = req.body;

	if (!userId || !fcmToken) {
		return res.status(400).send("Missing data (userId or fcmToken)");
	}

	model.updateFcmToken(userId, fcmToken, (error, changes) => {
		if (error) return res.status(500).send(`${error}`);
		res.json({ message: "Token updated successfully", changes: changes });
	});
};

/**
 * Set user online/offline status
 */
const setOnlineStatus = (req, res) => {
	const { id } = req.params;
	const { online } = req.body;
	
	model.setOnlineStatus(id, online, (error, changes) => {
		if (error) return res.status(500).send(`${error}`);
		res.json(changes);
	});
};

/**
 * Soft delete user (sets ACTIVE to 0)
 */
const destroy = (req, res) => {
	const { id } = req.params;
	
	model.destroy(id, (error, changes) => {
		if (error) return res.status(500).send(`${error}`);
		res.json(changes);
	});
};

/**
 * Get all available user roles
 */
const receiveRoles = (req, res) => {
  model.getRoles((error, rows) => {
	if (error) return res.status(500).send(`${error}`);
	res.json(rows);
  });
};

/**
 * Reset user password
 */
const resetPassword = async (req, res) => {
  const { username, newPwd } = req.body;

  if (!username || !newPwd) {
    return res.status(400).send("Missing data (username or newPwd)");
  }

  try {
    const hashedPwd = await bcrypt.hash(newPwd, 10);

    model.resetPassword(username, hashedPwd, (error, changes) => {
      if (error) return res.status(500).send(`${error}`);

      if (changes === 0) {
        return res.status(404).send("User not found");
      }

      return res.json({ message: "Password updated", changes });
    });
  } catch (exception) {
    return res.status(500).send(`${exception}`);
  }
};

module.exports = {
	register,
	login,
	token,
	logout,
	clearTokens,
	index,
	getOne,
	update,
	setOnlineStatus,
	destroy,
	receiveRoles,
	updateToken,
	resetPassword
}
};

const update = async (req, res) => {
	const { id } = req.params;
	const { username, fullname, pwd } = req.body;
	
	try {
		const hashedPwd = (pwd != null) ? await bcrypt.hash(pwd, 10) : null;
		
		model.update(id, username, fullname, hashedPwd, (error, changes) => {
			if (error) return res.status(500).send(`${error}`);
			
			res.json(changes);
		});
		res.status(201);
	} catch (exception) {
		return res.status(500).send(`${exception}`);
	}
};

const updateToken = (req, res) => {
	const { userId, fcmToken } = req.body;

	if (!userId || !fcmToken) {
		return res.status(400).send("Faltan datos (userId o fcmToken)");
	}

	model.updateFcmToken(userId, fcmToken, (error, changes) => {
		if (error) return res.status(500).send(`${error}`);
		
		res.json({ message: "Token actualizado correctamente", changes: changes });
	});
};

const setOnlineStatus = (req, res) => {
	const { id } = req.params;
	const { online } = req.body;
	
	model.setOnlineStatus(id, online, (error, changes) => {
		if (error) return res.status(500).send(`${error}`);
		
		res.json(changes);
	});
};

const destroy = (req, res) => {
	const { id } = req.params;
	
	model.destroy(id, (error, changes) => {
		if (error) return res.status(500).send(`${error}`);
		
		res.json(changes);
	});
};

// Obtener roles desde la base de datos
const receiveRoles = (req, res) => {
  model.getRoles((error, rows) => {
	if (error) return res.status(500).send(`${error}`);
	res.json(rows);
  });
};

// Ejemplo: POST /users/reset-password
const resetPassword = async (req, res) => {
  const { username, newPwd } = req.body;

  if (!username || !newPwd) {
    return res.status(400).send("Faltan datos (username o newPwd)");
  }

  try {
    const hashedPwd = await bcrypt.hash(newPwd, 10);

    model.resetPassword(username, hashedPwd, (error, changes) => {
      if (error) return res.status(500).send(`${error}`);

      if (changes === 0) {
        return res.status(404).send("Usuario no encontrado");
      }

      return res.json({ message: "Contraseña actualizada", changes });
    });
  } catch (exception) {
    return res.status(500).send(`${exception}`);
  }
};



module.exports = {
	register,
	login,
	token,
	logout,
	clearTokens,
	index,
	getOne,
	update,
	setOnlineStatus,
	destroy,
	receiveRoles,
	updateToken,
	resetPassword
}