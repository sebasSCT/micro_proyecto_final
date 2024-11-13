const mongoose = require('mongoose');

const ProfileSchema = new mongoose.Schema({
    user_id: String,
    nickname: String,
    personal_url: String,
    contact_public: Boolean,
    address: String,
    bio: String,
    organization: String,
    country: String,
    social_links: {
        twitter: { type: String },
        linkedin: { type: String },
    },
    identificacion: String,
    estado: Boolean,
});

const Profile = mongoose.model('Profile', ProfileSchema);
module.exports = Profile;
