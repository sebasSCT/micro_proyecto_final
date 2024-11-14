require('dotenv').config();
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

function getDecodedKey() {
    return Buffer.from(process.env.JWT_SECRET, 'base64');
}

exports.updateProfile = async (req, res) => {
    console.log(process.env.JWT_SECRET);
    try {
        const authHeader = req.headers.authorization;
        if (!authHeader || !authHeader.startsWith('Bearer ')) {
            return res.status(401).json({ error: 'Token de autenticación no proporcionado o inválido' });
        }

        const token = authHeader.replace('Bearer ', '');
        let userIdFromToken;
        try {
            const decodedToken = jwt.verify(token, getDecodedKey()); // Asegúrate de tener tu clave secreta en las variables de entorno
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
        const authHeader = req.headers.authorization;
        if (!authHeader || !authHeader.startsWith('Bearer ')) {
            return res.status(401).json({ error: 'Token de autenticación no proporcionado o inválido' });
        }

        const token = authHeader.replace('Bearer ', '');
        let userIdFromToken;

        try {
            // Usamos la clave secreta decodificada para verificar el token
            const decodedToken = jwt.verify(token, getDecodedKey());
            userIdFromToken = decodedToken.id;
        } catch (error) {
            console.log("Error al verificar el token:", error.message);
            return res.status(401).json({ error: 'Token no válido' });
        }

        if (userIdFromToken !== req.params.id) {
            return res.status(403).json({ error: 'No se puede eliminar: el ID del token no coincide con el ID del usuario' });
        }

        const result = await UserProfile.updateOne(
            { user_id: req.params.id, estado: true }, // Asegura que solo intente actualizar si el estado es true
            { $set: { estado: false } }               // Cambia el estado a false
        );

        // Verificar si el perfil fue encontrado y actualizado, independientemente de nModified
        if (result.matchedCount > 0) {
            // El perfil fue localizado, y si `estado` fue `true`, ya debería estar en `false` ahora.
            return res.status(204).send(); // Se actualizó (o no era necesario modificar, pero existía)
        } else {
            // Si matchedCount es 0, el perfil no fue encontrado
            return res.status(404).json({ error: 'Perfil no encontrado' });
        }

    } catch (error) {
        res.status(500).json({ error: 'Error al marcar el perfil como eliminado', details: error });
    }
};