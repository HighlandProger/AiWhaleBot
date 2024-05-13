package ru.rusguardian;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class WhaleAIBotApp {

    @Autowired
    private static ApplicationContext context;

    public static void main(String[] args) throws TelegramApiException {
        SpringApplication.run(WhaleAIBotApp.class, "");

    }

    public void run() throws TelegramApiException {

    }
}