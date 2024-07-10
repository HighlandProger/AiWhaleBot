package ru.rusguardian.bot.command.prompts.image.img.inner.img2video;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.dto.stable_diffusion.img2video.VideoModel;
import ru.rusguardian.service.data.UserDataDtoService;
import ru.rusguardian.service.process.prompt.ProcessImagePrompt;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

import java.text.MessageFormat;

@Component
@RequiredArgsConstructor
public class _3_Image2VideoForImageCommand extends PromptCommand {

    private static final String VIEW_DATA = "IMAGE_2_VIDEO_RESULT";
    private final ProcessImagePrompt processImagePrompt;

    @Override
    public CommandName getType() {
        return CommandName._3_IMAGE_2_VIDEO_FOR_IMAGE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        String prompt = TelegramUtils.getTextMessage(update);

        setNullCompletedCommand(update);
        UserDataDtoService.addPrompt(TelegramUtils.getChatId(update), prompt);
        int replyId = sendQuickReply(update);
        Chat chat = getChatOwner(update);
        processImagePrompt.processImg2VideoUrl(chat, UserDataDtoService.getDtoByIdAndRemove(chat.getId()))
                .thenAccept(url -> sendPromptVideo(SendVideo.builder()
                        .video(new InputFile(url))
                        .chatId(chat.getId())
                        .replyToMessageId(replyId)
                        .caption(getCaption(url, chat.getAiSettingsEmbedded().getAiLanguage()))
                        .parseMode(ParseMode.HTML)
                        .build()))
                .exceptionally(e -> {
                    errorCommand.execute(update);
                    throw new RuntimeException(e);
                });
    }

    private String getCaption(String responseUrl, AILanguage language){
        return MessageFormat.format(getTextByViewDataAndChatLanguage(VIEW_DATA, language), responseUrl);
    }

}
