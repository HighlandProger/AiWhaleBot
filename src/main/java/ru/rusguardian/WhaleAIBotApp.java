package ru.rusguardian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class WhaleAIBotApp {

    public static void main(String[] args) throws TelegramApiException {
        SpringApplication.run(WhaleAIBotApp.class, "");

        /*TODO list:

        - add Claude integration
        - add Midjourney integration
        - add Gemini integration

        - change voice response for user

        - remove context feature

        - add different language files

        - minor user naming for AIModel, AiRole, SubscriptionType

         */

    }
}