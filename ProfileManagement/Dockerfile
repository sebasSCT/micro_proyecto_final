# Usa la imagen oficial de Node.js con la versión correcta (v20.11.1)
FROM node:20.11.1

# Establece el directorio de trabajo en /app
WORKDIR /app

# Copia el package.json y package-lock.json (si existe)
COPY package*.json ./

# Instala las dependencias de Node.js
RUN npm install

# Copia todo el código fuente a /app
COPY . .

# Expon el puerto que usará la aplicación (por ejemplo, 8079)
EXPOSE 8079

# Ejecuta el archivo serve.js
CMD ["node", "server.js"]
