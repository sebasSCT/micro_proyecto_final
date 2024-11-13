const jwt = require('jsonwebtoken');

function decodetoken (token)
{   
    const claims = jwt.decode(token);
    
    const userId = claims.id;
    
    return userId;
}

module.exports = { decodetoken };