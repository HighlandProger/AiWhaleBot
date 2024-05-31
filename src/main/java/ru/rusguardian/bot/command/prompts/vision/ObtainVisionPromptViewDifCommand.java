package ru.rusguardian.bot.command.prompts.vision;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
@RequiredArgsConstructor
public class ObtainVisionPromptViewDifCommand extends Command {

    private static final String INSTRUCTION_FILE_PATH = "text/prompt/vision/vision_instruction/";

    @Override
    public CommandName getType() {
        return CommandName.OBTAIN_VISION_PROMPT_VIEW_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        String prompt = TelegramUtils.getTextMessage(update).substring(CommandName.OBTAIN_VISION_PROMPT_VIEW_D.getViewName().length()).trim();
        Chat chat = getChat(update);
        if (prompt.isEmpty()) {
            bot.execute(SendMessage.builder()
                    .chatId(chat.getId())
                    .replyToMessageId(TelegramUtils.getMessageId(update))
                    .text(getTextFromFileByChatLanguage(INSTRUCTION_FILE_PATH, chat))
                    .build());
            return;
        }

        commandContainerService.getCommand(CommandName.EXECUTE_VISION_PROMPT).execute(update);
    }
}
