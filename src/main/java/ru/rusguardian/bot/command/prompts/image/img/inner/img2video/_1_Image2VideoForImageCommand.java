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
public class _1_Image2VideoForImageCommand extends PromptCommand {

    private static final String VIEW_DATA = "IMG_2_VIDEO_CHOOSE_VIDEO_MODEL";

    @Override
    public CommandName getType() {
        return CommandName._1_IMAGE_2_VIDEO_FOR_IMAGE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chatOwner = getChatOwner(update);
        if (isChatLimitExpired(chatOwner, AIModel.STABLE_DIFFUSION)) {
            edit(getEditMessageWithResponse(TelegramUtils.getChatId(update), getChatLimitExpiredString(chatOwner), TelegramUtils.getMessageId(update)));
            return;
        }

        String initImageUrl = FileUtils.getFileUrlFromMessage(((Message) update.getCallbackQuery().getMessage()).getReplyToMessage(), bot).toString();
        UserDataDtoService.addImageUrl(TelegramUtils.getChatId(update), initImageUrl);
        setNextCommand(update, CommandName._2_IMAGE_2_VIDEO_FOR_IMAGE);
        editMessage(update, getTextByViewDataAndChatLanguage(VIEW_DATA, getChatLanguage(update)), getKeyboard());
    }

    private InlineKeyboardMarkup getKeyboard(){
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (VideoModel model : VideoModel.values()){
            buttons.add(List.of(InlineKeyboardButton.builder().text(model.getValue()).callbackData(model.name()).build()));
        }
        buttons.add(List.of(InlineKeyboardButton.builder().text(CommandName.BACK.getViewName()).callbackData(CommandName.OBTAIN_IMAGE_REQUEST.getBlindName()).build()));
        return new InlineKeyboardMarkup(buttons);
    }
}
