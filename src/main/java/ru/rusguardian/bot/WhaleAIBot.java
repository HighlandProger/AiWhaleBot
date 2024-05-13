package ru.rusguardian.bot;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.rusguardian.bot.command.service.ProcessUpdateService;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhaleAIBot extends TelegramLongPollingBot {

    @PostConstruct
    private void init() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
    }

    @Autowired
    private ProcessUpdateService service;

    @Value("${bot.token}")
    private String botToken;

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Value("${bot.username}")
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {
        log.info(update.toString());
        service.process(update);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

}
