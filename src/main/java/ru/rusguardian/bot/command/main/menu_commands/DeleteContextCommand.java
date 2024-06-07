package ru.rusguardian.bot.command.main.menu_commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.data.ChatCompletionMessageService;

@Component
@RequiredArgsConstructor
public class DeleteContextCommand extends Command {

    private final ChatCompletionMessageService chatCompletionMessageService;

    private static final String VIEW_DATA = "DELETE_CONTEXT";

    @Override
    public CommandName getType() {
        return CommandName.DELETE_CONTEXT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChat(update);
        chatCompletionMessageService.deleteContext(chat.getId());
        sendMessage(update, getTextByViewDataAndChatLanguage(VIEW_DATA, chat.getAiSettingsEmbedded().getAiLanguage()));
    }
}