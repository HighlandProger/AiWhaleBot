package ru.rusguardian.bot.command.prompts.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.process.get.ProcessGetTextLimitExpired;
import ru.rusguardian.service.process.prompt.ProcessPromptImage;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;

import static ru.rusguardian.bot.command.service.CommandName.EXECUTE_IMAGE_PROMPT_BLIND_D;
import static ru.rusguardian.bot.command.service.CommandName.OBTAIN_IMAGE_PROMPT_VIEW_D;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecuteImagePromptViewDifCommand extends PromptCommand {

    private final ProcessPromptImage processPromptImage;
    private final ProcessGetTextLimitExpired getTextLimitExpired;

    private static final String PREPARING_INFO_FILE_PATH = "text/prompt/image/image_preparing/";

    @Override
    public CommandName getType() {
        return EXECUTE_IMAGE_PROMPT_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        Chat chat = getChat(update);
        AIModel model = AIModel.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 1));

        Message lastBotMessage = (Message) update.getCallbackQuery().getMessage();
        String reply = lastBotMessage.getReplyToMessage().getText();
        String prompt = reply.substring(OBTAIN_IMAGE_PROMPT_VIEW_D.getViewName().length()).trim();
        boolean isChatLimitExpired = isChatLimitExpired(chat, model);

        if (!isChatLimitExpired) {
            processPromptImage.process(chat, model, prompt);
        }

        String quickResponse = getQuickResponse(chat, model, isChatLimitExpired);
        EditMessageText edit = EditMessageText.builder()
                .chatId(chat.getId())
                .text(quickResponse)
                .messageId(lastBotMessage.getMessageId())
                .build();

        bot.execute(edit);
    }

    private String getQuickResponse(Chat chat, AIModel model, boolean isChatLimitExpired) {
        return isChatLimitExpired
                ? getTextLimitExpired.get(chat, model)
                : getTextFromFileByChatLanguage(PREPARING_INFO_FILE_PATH, chat);
    }

}
