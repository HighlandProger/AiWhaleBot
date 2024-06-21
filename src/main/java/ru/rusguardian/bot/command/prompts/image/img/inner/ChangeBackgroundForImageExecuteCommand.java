package ru.rusguardian.bot.command.prompts.image.img.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.prompts.image.img.inner.service.ImageUrlDtoService;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.process.prompt.ProcessImagePrompt;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
@RequiredArgsConstructor
public class ChangeBackgroundForImageExecuteCommand extends PromptCommand {

    private final ProcessImagePrompt processImagePrompt;

    @Override
    public CommandName getType() {
        return CommandName.CHANGE_BACKGROUND_FOR_IMAGE_EXECUTE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        int replyId = sendReplyToImageRequest(update, getChatLanguage(update)).getMessageId();
        String prompt = TelegramUtils.getTextMessage(update);
        String initImageUrl = ImageUrlDtoService.getImageUrlAndRemove(TelegramUtils.getChatId(update));
        Chat chat = getChatOwner(update);
        processImagePrompt.processChangeBackgroundImageUrl(chat, initImageUrl, prompt)
                .thenAccept(url -> sendPromptImage(url, String.valueOf(chat.getId()), replyId))
                .exceptionally(e -> {
                    errorCommand.execute(update);
                    throw new RuntimeException(e);
                });
    }
}
