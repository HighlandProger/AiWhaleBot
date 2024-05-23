package ru.rusguardian.bot.command.prompts.text;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.process.prompt.ProcessPromptText;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecuteTextPromptCommand extends PromptCommand {

    private final ProcessPromptText processPromptText;

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
            processPromptText.process(chat, prompt).thenAccept(responseDto -> {
                String response = responseDto.getChoices().get(0).getMessage().getContent();
                try {
                    bot.execute(getEditMessageWithResponse(chat.getId(), response, replyId));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }).exceptionally(e -> {
                log.error(e.getMessage());
                commandContainerService.getCommand(CommandName.ERROR).execute(update);
                //TODO minor correct exceptionally obtain
                return null;
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
