package ru.rusguardian.bot.command.prompts.text;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
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

        Chat chatOwner = getChatOwner(update);
        Long initialChatId = getInitialChatId(update);
        AIModel model = chatOwner.getAiSettingsEmbedded().getAiActiveModel();
        String prompt = getViewTextMessage(update);

        if (!isChatLimitExpired(chatOwner, model)) {
            processPromptText.process(chatOwner, prompt).thenAccept(response -> {
                if (!chatOwner.getAiSettingsEmbedded().isVoiceResponseEnabled()) {
                    editForPrompt(getEditMessageWithResponse(initialChatId, response, replyId));
                    return;
                }
                processPromptVoice.processText2Voice(chatOwner, response).thenAccept(voiceResponse -> {
                    sendVoice(SendVoice.builder().voice(new InputFile(voiceResponse)).chatId(initialChatId).replyToMessageId(replyId).build());
                });
            }).exceptionally(e -> {
                log.error(e.getMessage());
                commandContainerService.getCommand(CommandName.ERROR).execute(update);
                throw new RuntimeException("EXCEPTION DURING FUTURE");
            });
        } else {
            String response;
            if (chatOwner.getSubscriptionEmbedded().getSubscriptionInfo().getType() == SubscriptionType.FREE) {
                response = getTextByViewDataAndChatLanguage(LIMIT_EXPIRED_FREE, chatOwner.getAiSettingsEmbedded().getAiLanguage());
            } else {
                response = getTextByViewDataAndChatLanguage(LIMIT_EXPIRED, chatOwner.getAiSettingsEmbedded().getAiLanguage());
            }
            edit(getEditMessageWithResponse(initialChatId, response, replyId));
        }
    }


}
