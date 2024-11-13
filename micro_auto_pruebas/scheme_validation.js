const Ajv = require('ajv');
const ajv = new Ajv();
const userSchema = require('./user-schema.json'); 

function validateUser(data) {
  const validate = ajv.compile(userSchema);
  const valid = validate(data);

  if (!valid) {
    console.log(validate.errors);
    return false;
  }
  return true;
}

module.exports = { validateUser };
