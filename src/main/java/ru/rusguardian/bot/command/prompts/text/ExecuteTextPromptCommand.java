package ru.rusguardian.bot.command.prompts.text;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.process.prompt.ProcessPromptText;
import ru.rusguardian.service.process.prompt.ProcessPromptVoice;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecuteTextPromptCommand extends PromptCommand {

    private final ProcessPromptText processPromptText;
    private final ProcessPromptVoice processPromptVoice;

    @Override
    public CommandName getType() {
        return CommandName.EXECUTE_TEXT_PROMPT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        int replyId = sendQuickReply(update);

        Chat chat = getChat(update);
        AIModel model = chat.getAiSettingsEmbedded().getAiActiveModel();
        String prompt = TelegramUtils.getTextMessage(update);

        if (!isChatLimitExpired(chat, model)) {
            processPromptText.process(chat, prompt).thenAccept(response -> {
                if(!chat.getAiSettingsEmbedded().isVoiceResponseEnabled()) {
                    try {
                        bot.execute(getEditMessageWithResponse(chat.getId(), response, replyId));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                processPromptVoice.processText2Voice(chat, response).thenAccept(voiceResponse -> {
                    SendVoice voice = SendVoice.builder().voice(new InputFile(voiceResponse)).chatId(chat.getId()).replyToMessageId(replyId).build();
                    try {
                        bot.execute(voice);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                });
            }).exceptionally(e -> {
                log.error(e.getMessage());
                commandContainerService.getCommand(CommandName.ERROR).execute(update);
                throw new RuntimeException("EXCEPTION DURING FUTURE");
            });
        } else {
            String response;
            if (chat.getSubscriptionEmbedded().getSubscriptionInfo().getType() == SubscriptionType.FREE) {
                response = getTextFromFileByChatLanguage(LIMIT_EXPIRED_FREE, chat);
            } else {
                response = getTextFromFileByChatLanguage(LIMIT_EXPIRED, chat);
            }
            EditMessageText edit = getEditMessageWithResponse(chat.getId(), response, replyId);
            bot.execute(edit);
        }
    }


}
