require('dotenv').config(); // Cargar las variables de entorno
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors'); // Importa el middleware cors
const ProfileController = require('./src/controllers/ProfileController');

const app = express();
app.use(cors());
app.use(express.json());

const PORT = 8079;

// Usar las variables para construir la URL de conexión a MongoDB
const mongoURI = `mongodb://${process.env.DB_USER}:${process.env.DB_PASS}@${process.env.DB_HOST}:${process.env.DB_PORT}/user_profiles?authSource=admin`;

mongoose.connect(mongoURI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
})
    .then(() => console.log('Connected to MongoDB'))
    .catch(err => console.error('Failed to connect to MongoDB', err));

app.post('/usuario', ProfileController.createProfile);
app.get('/usuario/:id', ProfileController.getProfile);
app.get('/usuario', ProfileController.getAllProfiles);
app.put('/usuario/:id', ProfileController.updateProfile);
app.delete('/usuario/:id', ProfileController.deleteProfile);

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});

app.use(cors({
    origin: '*', // Permite cualquier origen
    methods: ['GET', 'POST', 'PUT', 'DELETE'], // Métodos permitidos
    allowedHeaders: ['Content-Type', 'Authorization'] // Encabezados permitidos
}));
