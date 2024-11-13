package main

import (
	"github.com/gin-gonic/gin"
	"net/http"
)

type NotificationRequest struct {
	Email   string `json:"email"`
	Message string `json:"message"`
}

type NotificationRequestMessage struct {
	To      string `json:"to"`
	Message string `json:"message"`
}

func SendNotification(c *gin.Context) {
	var request NotificationRequest

	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	err := SaveNotification(request.Email, request.Message)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to save notification"})
		return
	}

	err = SendEmail(request.Email, request.Message) // Implementación del envío de correo
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to send email"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"status": "Notification sent successfully!"})
}

func SendWhatsApp(c *gin.Context) {
	var request NotificationRequestMessage
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Llama a la función para enviar WhatsApp
	EnviarWhatsApp(request.To, request.Message)

	c.JSON(http.StatusOK, gin.H{"status": "WhatsApp message sent successfully!"})
}

func SendSMS(c *gin.Context) {
	var request NotificationRequestMessage
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Llama a la función para enviar SMS
	EnviarSMS(request.To, request.Message)

	c.JSON(http.StatusOK, gin.H{"status": "SMS message sent successfully!"})
}
