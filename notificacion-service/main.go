package main

import (
	"github.com/gin-contrib/cors"
	"github.com/prometheus/client_golang/prometheus/promhttp"
	"log"
	"os"
	"os/signal"
	"syscall"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/swaggo/files"
	"github.com/swaggo/gin-swagger"
)

func main() {
	// Conectar a MongoDB
	ConnectMongo()

	// Conectar a RabbitMQ
	conn := ConnectRabbitMQ()
	defer conn.Close()

	// Configurar API para notificaciones
	r := gin.Default()

	r.Use(cors.Default()) // Permite todos los orígenes

	r.POST("/send", SendNotification)
	r.GET("/notification", GetAllNotifications)
	r.GET("/notification/:id", GetNotification)

	r.POST("/sendWhatsapp", SendWhatsApp)
	r.POST("/sendSMS", SendSMS)

	// Agregar ruta de métricas en el mismo servidor
	r.GET("/metrics", gin.WrapH(promhttp.Handler()))

	r.GET("/health", HealthCheck)

	// Ruta para acceder a Swagger UI
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	// Iniciar el servidor en un goroutine
	go func() {
		if err := r.Run(":8080"); err != nil {
			log.Fatalf("Error starting server: %s", err)
		}
	}()

	// Consumir mensajes desde RabbitMQ
	go ConsumeFromQueue(conn)

	// Esperar señal de finalización
	signalChan := make(chan os.Signal, 1)
	signal.Notify(signalChan, os.Interrupt, syscall.SIGTERM)
	<-signalChan

	log.Println("Shutting down gracefully...")
}

// HealthCheck maneja el endpoint /health
func HealthCheck(c *gin.Context) {
	c.JSON(http.StatusOK, gin.H{
		"status":  "UP",
		"message": "The application is running smoothly.",
	})
}
