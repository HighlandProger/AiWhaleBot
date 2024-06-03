package ru.rusguardian.bot.command.main.menu_commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;

@Component
@RequiredArgsConstructor
public class TermsCommand extends Command {

    private static final String VIEW_DATA = "TERMS";

    @Override
    public CommandName getType() {
        return CommandName.TERMS;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        sendMessage(update, getTextByViewDataAndChatLanguage(VIEW_DATA, getChat(update).getAiSettingsEmbedded().getAiLanguage()));
    }
}
