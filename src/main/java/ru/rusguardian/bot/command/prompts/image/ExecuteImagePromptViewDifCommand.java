package ru.rusguardian.bot.command.prompts.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.process.get.ProcessGetTextLimitExpired;
import ru.rusguardian.service.process.prompt.ProcessPromptText2Image;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.InputFileUtil;

import java.text.MessageFormat;

import static ru.rusguardian.bot.command.service.CommandName.EXECUTE_IMAGE_PROMPT_BLIND_D;
import static ru.rusguardian.bot.command.service.CommandName.OBTAIN_IMAGE_PROMPT_VIEW_D;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecuteImagePromptViewDifCommand extends PromptCommand {

    private final ProcessPromptText2Image processPromptText2Image;
    private final ProcessGetTextLimitExpired getTextLimitExpired;

    private static final String IMAGE_PREPARING = "IMAGE_PREPARING";
    private static final String IMAGE_READY_PATTERN = "IMAGE_READY";

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
            processPromptText2Image.processUrl(chat, model, prompt).thenAccept(url ->
                    sendResponseToUser(url, chat, model.getModelName(), prompt)
            ).exceptionally(ex -> {
                log.error("EXCEPTION DURING EXECUTING ProcessPromptText2Image. Model: {}, prompt: {}, ExMessage: {}", model, prompt, ex.getMessage());
                errorCommand.execute(update);
                throw new RuntimeException(ex);
            });
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
                : getTextByViewDataAndChatLanguage(IMAGE_PREPARING, chat.getAiSettingsEmbedded().getAiLanguage());
    }

    private void sendResponseToUser(String fileUrl, Chat chat, String model, String prompt) {
        InputFile file = InputFileUtil.getInputFileFromURL(fileUrl);
        SendPhoto photo = SendPhoto.builder().photo(file).chatId(chat.getId()).caption(getCaption(chat, model, prompt)).parseMode(ParseMode.HTML).build();
        try {
            bot.execute(photo);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCaption(Chat chat, String model, String prompt) {
        return MessageFormat.format(getTextByViewDataAndChatLanguage(IMAGE_READY_PATTERN, chat.getAiSettingsEmbedded().getAiLanguage()), model, prompt);
    }

}
