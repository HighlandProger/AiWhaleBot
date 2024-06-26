package ru.rusguardian.bot.command.prompts.image.img.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.prompts.image.img.inner.service.ImageUrlDtoService;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.process.prompt.ProcessPromptVision;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
@RequiredArgsConstructor
public class GPT4VisionForImageExecuteCommand extends PromptCommand {

    private final ProcessPromptVision processPromptVision;

    @Override
    public CommandName getType() {
        return CommandName.GPT_4_VISION_FOR_IMAGE_EXECUTE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        int replyId = sendReplyToImageRequest(update, getChatLanguage(update)).getMessageId();

        String prompt = TelegramUtils.getTextMessage(update);
        String initImageUrl = ImageUrlDtoService.getImageUrlAndRemove(TelegramUtils.getChatId(update));
        Chat chat = getChatOwner(update);
        setNullCompletedCommand(update);
        processPromptVision.process(chat, initImageUrl, prompt)
                .thenAccept(response -> editForPrompt(EditMessageText.builder().chatId(chat.getId()).text(response).messageId(replyId).build()))
                .exceptionally(e -> {
                    errorCommand.execute(update);
                    throw new RuntimeException(e);
                });
    }

}
