const jwt = require('jsonwebtoken');
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
        const authHeader = req.headers.authorization;
        if (!authHeader || !authHeader.startsWith('Bearer ')) {
            return res.status(401).json({ error: 'Token de autenticación no proporcionado o inválido' });
        }

        const token = authHeader.replace('Bearer ', '');
        let userIdFromToken;
        try {
            const decodedToken = jwt.verify(token, process.env.JWT_SECRET); // Asegúrate de tener tu clave secreta en las variables de entorno
            userIdFromToken = decodedToken.id;
        } catch (error) {
            return res.status(401).json({ error: 'Token no válido' });
        }

        if (userIdFromToken !== req.params.id) {
            return res.status(403).json({ error: 'No se puede actualizar: el ID del token no coincide con el ID del usuario' });
        }

        const result = await UserProfile.updateOne({ user_id: req.params.id }, { $set: req.body });
        res.json({ modifiedCount: result.nModified });
    } catch (error) {
        res.status(400).json({ error: 'Error actualizando el perfil', details: error });
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

exports.deleteProfile = async (req, res) => {
    try {
        // Obtener el token del encabezado Authorization
        const authHeader = req.headers.authorization;
        if (!authHeader || !authHeader.startsWith('Bearer ')) {
            return res.status(401).json({ error: 'Token de autenticación no proporcionado o inválido' });
        }

        // Extraer el token y obtener el ID del token
        const token = authHeader.replace('Bearer ', '');
        let userIdFromToken;
        try {
            const decodedToken = jwt.verify(token, process.env.JWT_SECRET);
            userIdFromToken = decodedToken.id;
        } catch (error) {
            return res.status(401).json({ error: 'Token no válido' });
        }

        // Verificar que el ID en el token coincida con el ID en los parámetros de la solicitud
        if (userIdFromToken !== req.params.id) {
            return res.status(403).json({ error: 'No se puede eliminar: el ID del token no coincide con el ID del usuario' });
        }

        // Intentar actualizar el perfil y cambiar su estado a false (borrado lógico)
        const result = await UserProfile.updateOne(
            { user_id: req.params.id },
            { $set: { estado: false } }  // Marcamos el estado como false (borrado lógico)
        );

        if (result.nModified > 0) {
            res.status(204).send(); // Sin contenido, indicando que se marcó correctamente como eliminado
        } else {
            res.status(404).json({ error: 'Profile not found' });
        }
    } catch (error) {
        res.status(500).json({ error: 'Error al marcar el perfil como eliminado', details: error });
    }
};