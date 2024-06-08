package ru.rusguardian.bot.command.prompts.vision;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.process.prompt.ProcessPromptVision;
import ru.rusguardian.telegram.bot.util.util.FileUtils;

import java.net.URL;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecuteVisionPromptCommand extends PromptCommand {

    private final ProcessPromptVision processPromptVision;

    @Override
    public CommandName getType() {
        return CommandName.EXECUTE_VISION_PROMPT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        int replyId = sendQuickReply(update);

        URL imageUrl = FileUtils.getFileUrlFromUpdate(update, bot);
        String prompt = getViewTextMessage(update).substring(CommandName.OBTAIN_VISION_PROMPT_VIEW_D.getViewName().length()).trim();

        Chat chat = getChatOwner(update);
        Long initialChatId = getInitialChatId(update);
        AIModel model = AIModel.GPT_4_OMNI;

        if (!isChatLimitExpired(chat, model)) {
            processPromptVision.process(chat, imageUrl.toString(), prompt).thenAccept(response -> {
                editForPrompt(getEditMessageWithResponse(chat.getId(), response, replyId));
            }).exceptionally(e -> {
                log.error(e.getMessage());
                commandContainerService.getCommand(CommandName.ERROR).execute(update);
                throw new RuntimeException("EXCEPTION DURING FUTURE");
            });
        } else {
            String response;
            if (chat.getSubscriptionEmbedded().getSubscriptionInfo().getType() == SubscriptionType.FREE) {
                response = getTextByViewDataAndChatLanguage(LIMIT_EXPIRED_FREE, chat.getAiSettingsEmbedded().getAiLanguage());
            } else {
                response = getTextByViewDataAndChatLanguage(LIMIT_EXPIRED, chat.getAiSettingsEmbedded().getAiLanguage());
            }
            editForPrompt(getEditMessageWithResponse(initialChatId, response, replyId));
        }
    }
}
