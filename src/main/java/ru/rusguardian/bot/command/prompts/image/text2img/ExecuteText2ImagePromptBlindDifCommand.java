package ru.rusguardian.bot.command.prompts.image.text2img;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.process.prompt.ProcessImagePrompt;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.InputFileUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static ru.rusguardian.bot.command.service.CommandName.EXECUTE_TEXT_2_IMAGE_PROMPT_BLIND_D;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecuteText2ImagePromptBlindDifCommand extends PromptCommand {

    private final ProcessImagePrompt processImagePrompt;

    private static final String IMAGE_PREPARING = "IMAGE_PREPARING";
    private static final String IMAGE_READY_PATTERN = "IMAGE_READY";
    private static final String IMG_PREFIX = "/img";

    @Override
    public CommandName getType() {
        return EXECUTE_TEXT_2_IMAGE_PROMPT_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        Chat chatOwner = getChatOwner(update);
        Long initialChatId = getInitialChatId(update);
        AIModel model = AIModel.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 1));
        AILanguage language = chatOwner.getAiSettingsEmbedded().getAiLanguage();

        Message lastBotMessage = (Message) update.getCallbackQuery().getMessage();
        String reply = lastBotMessage.getReplyToMessage().getText();
        String prompt = reply.substring(IMG_PREFIX.length()).trim();
        boolean isChatLimitExpired = isChatLimitExpired(chatOwner, model);

        if (isChatLimitExpired(chatOwner, model)) {
            edit(getEditMessageWithResponse(initialChatId, getChatLimitExpiredString(chatOwner), TelegramUtils.getMessageId(update)));
            return;
        }

        processImagePrompt.processText2ImageUrls(chatOwner, model, prompt)
                .thenCompose(urls -> CompletableFuture.supplyAsync(
                        () -> getSendPhoto(initialChatId, language, model.getModelName(), prompt, urls),
                        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS)
                ))
                .thenCompose(bot::executeAsync)
                .exceptionally(e -> {
                    log.error("Exception with ProcessImagePrompt. Model: {}, prompt: {}, error: {}", model, prompt, e.getMessage());
                    errorCommand.execute(update);
                    throw new RuntimeException(e);
                });

        String quickResponse = getQuickResponse(chatOwner, isChatLimitExpired);
        edit(EditMessageText.builder()
                .chatId(initialChatId)
                .text(quickResponse)
                .messageId(lastBotMessage.getMessageId())
                .build());
    }

    private SendPhoto getSendPhoto(Long chatId, AILanguage language, String model, String prompt, List<String> urls) {
        return SendPhoto.builder()
                .photo(InputFileUtil.getInputFileFromURL(urls.get(0)))
                .chatId(chatId)
                .caption(getCaption(language, model, prompt, urls))
                .parseMode(ParseMode.HTML).build();
    }

    private String getCaption(AILanguage language, String model, String prompt, List<String> urls) {
        return MessageFormat.format(getTextByViewDataAndChatLanguage(IMAGE_READY_PATTERN, language), model, urls.size(), prompt, getPhotoLinks(urls));
    }

    private String getPhotoLinks(List<String> urls) {
        List<String> buttons = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            buttons.add(String.format("<a href=\"%s\">%s</a>", urls.get(i), i + 1));
        }
        return String.join(", ", buttons);
    }

    private String getQuickResponse(Chat chat, boolean isChatLimitExpired) {
        return isChatLimitExpired
                ? getChatLimitExpiredString(chat)
                : getTextByViewDataAndChatLanguage(IMAGE_PREPARING, chat.getAiSettingsEmbedded().getAiLanguage());
    }

}
