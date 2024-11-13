package main

import (
	"fmt"
	"github.com/twilio/twilio-go"
	twilioApi "github.com/twilio/twilio-go/rest/api/v2010"
	"log"
)

func EnviarWhatsApp(to string, message string) {
	twilioConfig := GetTwilioConfig(true)
	client := twilio.NewRestClientWithParams(twilio.ClientParams{
		Username: twilioConfig.SID,
		Password: twilioConfig.AuthToken,
	})

	params := &twilioApi.CreateMessageParams{}
	params.SetTo(to)
	params.SetFrom(twilioConfig.FromPhone)
	params.SetBody(message)

	resp, err := client.Api.CreateMessage(params)
	if err != nil {
		log.Fatal("Error al enviar WhatsApp:", err)
	}

	fmt.Println("WhatsApp enviado con éxito! SID:", *resp.Sid)
}
