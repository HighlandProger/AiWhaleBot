package ru.rusguardian.bot.command.prompts.vision;

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
import ru.rusguardian.service.process.prompt.ProcessPromptVision;
import ru.rusguardian.telegram.bot.util.util.FileUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

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

        String imageUrl = FileUtils.getFileDownloadLink(TelegramUtils.getFileDownloadPath(update, bot), bot.getBotToken());
        String prompt = TelegramUtils.getTextMessage(update).substring(CommandName.OBTAIN_VISION_PROMPT_VIEW_D.getViewName().length()).trim();

        Chat chat = getChat(update);
        AIModel model = AIModel.GPT_4_OMNI;

        if (!isChatLimitExpired(chat, model)) {
            processPromptVision.process(chat, imageUrl, prompt).thenAccept(response -> {
                try {
                    bot.execute(getEditMessageWithResponse(chat.getId(), response, replyId));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
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
