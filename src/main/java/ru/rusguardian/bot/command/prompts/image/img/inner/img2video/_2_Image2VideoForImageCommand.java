package ru.rusguardian.bot.command.prompts.image.img.inner.img2video;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.dto.stable_diffusion.img2video.VideoModel;
import ru.rusguardian.service.data.UserDataDtoService;
import ru.rusguardian.telegram.bot.util.util.FileUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class _2_Image2VideoForImageCommand extends PromptCommand {

    private static final String VIEW_DATA = "IMAGE_2_VIDEO_SELECT_PROMPT";

    @Override
    public CommandName getType() {
        return CommandName._2_IMAGE_2_VIDEO_FOR_IMAGE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        VideoModel videoModel = VideoModel.valueOf(TelegramUtils.getCallbackQueryData(update));
        UserDataDtoService.addVideoModel(TelegramUtils.getChatId(update), videoModel);

        setNextCommand(update, CommandName._3_IMAGE_2_VIDEO_FOR_IMAGE);
        editMessage(update, getTextByViewDataAndChatLanguage(VIEW_DATA, getChatLanguage(update)), null);
    }

}
