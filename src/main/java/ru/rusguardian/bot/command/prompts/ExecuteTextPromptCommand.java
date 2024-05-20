package ru.rusguardian.bot.command.prompts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.service.process.ProcessPromptText;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.SendMessageUtil;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecuteTextPromptCommand extends Command {

    private final ProcessPromptText processPromptText;

    @Override
    public CommandName getType() {
        return CommandName.EXECUTE_TEXT_PROMPT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        SendMessage reply = SendMessageUtil.getSimple(update, "Ответ обрабатывается");
        reply.setReplyToMessageId(TelegramUtils.getMessageId(update));
        int replyId = bot.execute(reply).getMessageId();

        CompletableFuture.runAsync(() -> {
            String response = processPromptText.prompt(update);
            try {
                bot.execute(getCustomEditMessage(update, response, replyId));
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        });

    }

    private EditMessageText getCustomEditMessage(Update update, String text, int messageId) {
        EditMessageText edit = new EditMessageText();
        edit.setChatId(TelegramUtils.getChatId(update));
        edit.setMessageId(messageId);
        edit.setText(text);
        edit.setParseMode(ParseMode.MARKDOWN);

        return edit;
    }

}
