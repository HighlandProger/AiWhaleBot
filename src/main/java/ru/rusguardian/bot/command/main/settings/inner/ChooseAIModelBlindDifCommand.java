package ru.rusguardian.bot.command.main.settings.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.AISettingsEmbedded;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
//TODO refactor
public class ChooseAIModelBlindDifCommand extends Command {

    private static final String FILE_PATH = "text/settings/choose_ai_model/";

    @Override
    public CommandName getType() {
        return CommandName.CHOOSE_AI_MODEL_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChat(update);

        String aiModelToChangeString = TelegramCallbackUtils.getArgFromCallback(update, 1);
        if (aiModelToChangeString != null) {
            changeChatAiModel(chat, aiModelToChangeString);
        }
        AIModel currentModel = chat.getAiSettingsEmbedded().getAiActiveModel();

        editMessage(update, getText(chat), getKeyboard(currentModel));
    }

    private String getText(Chat chat) {
        return MessageFormat.format(getTextFromFileByChatLanguage(FILE_PATH, chat)
                , chat.getAiSettingsEmbedded().getAiActiveModel().getModelName());
    }

    private void changeChatAiModel(Chat chat, String aiModelString) {
        AIModel model = AIModel.valueOf(aiModelString);

        AISettingsEmbedded settings = chat.getAiSettingsEmbedded();
        settings.setAiActiveModel(model);
        chatService.update(chat);
    }

    private InlineKeyboardMarkup getKeyboard(AIModel currentModel) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttonLines = new ArrayList<>();

        getChangeableModels().forEach(model -> {
            buttonLines.add(List.of((InlineKeyboardButton.builder()
                    .text(getText(currentModel, model))
                    .callbackData(getCallback(currentModel, model))
                    .build())));
        });
        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text(CommandName.BACK.getViewName())
                .callbackData(CommandName.SETTINGS_BLIND.getBlindName())
                .build();
        buttonLines.add(List.of(backButton));
        markup.setKeyboard(buttonLines);
        return markup;
    }

    private String getText(AIModel currentModel, AIModel model) {
        if (currentModel == model) {
            return "✅ " + model.name();
        }
        return model.name();
    }

    private String getCallback(AIModel currentModel, AIModel model) {
        if (currentModel == model) {
            return CommandName.EMPTY.getBlindName();
        }
        return TelegramCallbackUtils.getCallbackWithArgs(this.getType().name(), model.name());
    }

    private List<AIModel> getChangeableModels() {
        return Stream.concat(Stream.concat(
                        AIModel.getByBalanceType(AIModel.BalanceType.GPT_4).stream(),
                        AIModel.getByBalanceType(AIModel.BalanceType.GPT_3).stream()),
                AIModel.getByBalanceType(AIModel.BalanceType.CLAUDE).stream()
        ).toList();

    }
}
