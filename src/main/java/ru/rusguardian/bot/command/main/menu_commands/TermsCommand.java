package ru.rusguardian.bot.command.main.menu_commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandMapping;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;

import javax.swing.text.html.HTML;

@Component
@RequiredArgsConstructor
@CommandMapping(viewCommands = "/terms")
public class TermsCommand extends Command {

    private static final String VIEW_DATA = "TERMS";

    @Override
    public CommandName getType() {
        return CommandName.TERMS;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChatOwner(update);
        SendMessage message = SendMessage.builder()
                .text(getTextByViewDataAndChatLanguage(VIEW_DATA, chat.getAiSettingsEmbedded().getAiLanguage()))
                .chatId(chat.getId())
                .disableWebPagePreview(true)
                .parseMode(ParseMode.HTML)
                .build();
        sendMessage(message);
    }
}
