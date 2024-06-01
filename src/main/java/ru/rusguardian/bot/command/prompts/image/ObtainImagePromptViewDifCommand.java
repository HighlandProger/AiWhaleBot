package ru.rusguardian.bot.command.prompts.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ObtainImagePromptViewDifCommand extends Command {

    private static final String IMAGE_INSTRUCTION = "IMAGE_INSTRUCTION";
    private static final String CHOOSE_IMAGE_MODEL = "CHOOSE_IMAGE_MODEL";


    @Override
    public CommandName getType() {
        return CommandName.OBTAIN_IMAGE_PROMPT_VIEW_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        String prompt = TelegramUtils.getTextMessage(update).substring(CommandName.OBTAIN_IMAGE_PROMPT_VIEW_D.getViewName().length()).trim();
        Chat chat = getChat(update);
        if (prompt.isEmpty()) {
            bot.execute(SendMessage.builder()
                    .chatId(chat.getId())
                    .replyToMessageId(TelegramUtils.getMessageId(update))
                    .text(getTextByViewDataAndChatLanguage(IMAGE_INSTRUCTION, chat.getAiSettingsEmbedded().getAiLanguage()))
                    .build());
            return;
        }

        bot.execute(SendMessage.builder()
                .chatId(chat.getId())
                .replyToMessageId(TelegramUtils.getMessageId(update))
                .text(getTextByViewDataAndChatLanguage(CHOOSE_IMAGE_MODEL, chat.getAiSettingsEmbedded().getAiLanguage()))
                .replyMarkup(getKeyboard())
                .build());

    }

    private InlineKeyboardMarkup getKeyboard() {
        InlineKeyboardButton dalle3 = InlineKeyboardButton.builder().text(AIModel.DALL_E_3.name()).callbackData(getCallback(AIModel.DALL_E_3)).build();
        InlineKeyboardButton stableDiffusion = InlineKeyboardButton.builder().text(AIModel.STABLE_DIFFUSION.name()).callbackData(getCallback(AIModel.STABLE_DIFFUSION)).build();
        InlineKeyboardButton midjourney = InlineKeyboardButton.builder().text(AIModel.MIDJOURNEY.name()).callbackData(getCallback(AIModel.MIDJOURNEY)).build();

        return ReplyMarkupUtil.getInlineKeyboardByButtons(List.of(List.of(dalle3, stableDiffusion), List.of(midjourney)));
    }

    private String getCallback(AIModel model) {
        return TelegramCallbackUtils.getCallbackWithArgs(CommandName.EXECUTE_IMAGE_PROMPT_BLIND_D.getBlindName(), model.name());
    }
}

