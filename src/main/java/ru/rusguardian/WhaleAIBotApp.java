package ru.rusguardian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WhaleAIBotApp {

    public static void main(String[] args) {
        SpringApplication.run(WhaleAIBotApp.class, "");

        /*TODO list:

        -! add Claude integration
        -! add Midjourney integration
        - add Gemini integration

        - change voice response for user

        - minor user naming for AIModel, AiRole, SubscriptionType

         */

    }
}