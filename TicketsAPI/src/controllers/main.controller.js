/**
 * Health check endpoint
 * Returns a simple response to verify API is running
 */
const index = (req, res) => {
	res.send("Hello World!!!");
};

module.exports = {
	index,
};