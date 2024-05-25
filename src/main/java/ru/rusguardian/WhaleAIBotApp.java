package ru.rusguardian;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class WhaleAIBotApp {

    public static void main(String[] args) throws TelegramApiException {
        SpringApplication.run(WhaleAIBotApp.class, "");

        /*TODO list:

        - add payment check

        - add Claude integration
        - add StableDiffusion integration
        - add Midjourney integration
        - add Gemini integration

        - change voice response for user
        - add voice response for user

        - change temperature feature
        - remove context feature

        - change language feature
        - add different language files

        - add prompts to AiRole
        - minor user naming for AIModel, AiRole, SubscriptionType

         */

    }
}