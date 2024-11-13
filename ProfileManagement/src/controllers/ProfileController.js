const UserProfile = require('../models/UserProfile');

exports.createProfile = async (req, res) => {
    try {
        const profile = await UserProfile.create(req.body);
        res.status(201).json({ id: profile._id });
    } catch (error) {
        res.status(400).json({ error: 'Error creating profile', details: error });
    }
};

exports.getProfile = async (req, res) => {
    try {
        const profile = await UserProfile.findOne({ user_id: req.params.id });
        if (profile) {
            res.json(profile);
        } else {
            res.status(404).json({ error: 'Profile not found' });
        }
    } catch (error) {
        res.status(500).json({ error: 'Error retrieving profile', details: error });
    }
};

exports.updateProfile = async (req, res) => {
    try {
        const result = await UserProfile.updateOne({ user_id: req.params.id }, { $set: req.body });
        res.json({ modifiedCount: result.nModified });
    } catch (error) {
        res.status(400).json({ error: 'Error updating profile', details: error });
    }
};

exports.getAllProfiles = async (req, res) => {
    const { page = 1, limit = 10 } = req.query;
    try {
        const profiles = await UserProfile.find()
            .skip((page - 1) * limit)
            .limit(Number(limit));
        const totalProfiles = await UserProfile.countDocuments();
        res.json({
            profiles,
            pagination: {
                currentPage: page,
                perPage: limit,
                totalProfiles,
                totalPages: Math.ceil(totalProfiles / limit)
            }
        });
    } catch (error) {
        res.status(500).json({ error: 'Error fetching profiles', details: error });
    }
};
