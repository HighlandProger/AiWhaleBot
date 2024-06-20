package ru.rusguardian.bot.command.prompts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.process.check.ProcessCheckChatRequestLimit;
import ru.rusguardian.service.process.get.ProcessGetTextLimitExpired;
import ru.rusguardian.telegram.bot.util.constants.MessageType;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.SendMessageUtil;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
@Slf4j
public class PromptCommand extends Command {

    protected static final String LIMIT_EXPIRED_FREE = "LIMIT_EXPIRED_FREE";
    protected static final String LIMIT_EXPIRED = "LIMIT_EXPIRED";
    @Autowired
    private ProcessCheckChatRequestLimit checkChatRequestLimit;
    @Autowired
    private ProcessGetTextLimitExpired getTextLimitExpired;

    @Override
    public CommandName getType() {
        return CommandName.PROMPT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        MessageType type = MessageType.getType(update);
        if (MessageType.TEXT == type) {
            commandContainerService.getCommand(CommandName.EXECUTE_TEXT_PROMPT).execute(update);
        }
        if (MessageType.VOICE == type) {
            commandContainerService.getCommand(CommandName.EXECUTE_VOICE_PROMPT).execute(update);
        }
    }

    protected boolean isChatLimitExpired(Chat chat, AIModel model) {
        return checkChatRequestLimit.getTotalAllowedCount(chat, model) <= 0;
    }

    protected int sendQuickReply(Update update) throws TelegramApiException {
        SendMessage reply = SendMessageUtil.getSimple(update, "Ответ обрабатывается");
        reply.setReplyToMessageId(TelegramUtils.getMessageId(update));
        return bot.execute(reply).getMessageId();
    }

    public EditMessageText getEditMessageWithResponse(Long chatId, String text, int messageId) {
        EditMessageText edit = new EditMessageText();
        edit.setChatId(chatId);
        edit.setMessageId(messageId);
        edit.setText(text);
        edit.setParseMode(ParseMode.HTML);

        return edit;
    }

    public void sendVoice(SendVoice voice) {
        bot.executeAsync(voice).exceptionally(e -> {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        });
    }

    protected CompletableFuture<Void> editForPrompt(EditMessageText editText) {
        return CompletableFuture.runAsync(() -> {
            if (editText.getText().contains("```")) {
                editText.setParseMode(ParseMode.MARKDOWN);
            }
            try {
                bot.execute(editText);
            } catch (TelegramApiException e) {
                log.warn(e.getMessage());
                editText.setParseMode(null);
                try {
                    bot.execute(editText);
                } catch (TelegramApiException ex) {
                    log.error("Error editing message to {}", editText);
                    throw new RuntimeException(ex);
                }
            }
            log.info("Chat {} successfully received prompt {}", editText.getChatId(), editText.getText().substring(0, 30) + "...");
        });
    }

    protected String getChatLimitExpiredString(Chat chat) {
        return getTextLimitExpired.get(chat);
    }
}
