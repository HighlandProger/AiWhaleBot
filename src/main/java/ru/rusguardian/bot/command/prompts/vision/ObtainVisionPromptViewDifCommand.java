package ru.rusguardian.bot.command.prompts.vision;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
@RequiredArgsConstructor
public class ObtainVisionPromptViewDifCommand extends Command {

    private static final String VISION_INSTRUCTION = "VISION_INSTRUCTION";

    @Override
    public CommandName getType() {
        return CommandName.OBTAIN_VISION_PROMPT_VIEW_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        String prompt = getViewTextMessage(update).substring(CommandName.OBTAIN_VISION_PROMPT_VIEW_D.getViewName().length()).trim();
        AILanguage language = getChatLanguage(update);
        Long initialChatId = getInitialChatId(update);
        if (prompt.isEmpty()) {
            bot.execute(SendMessage.builder()
                    .chatId(initialChatId)
                    .replyToMessageId(TelegramUtils.getMessageId(update))
                    .text(getTextByViewDataAndChatLanguage(VISION_INSTRUCTION, language))
                    .build());
            return;
        }

        commandContainerService.getCommand(CommandName.EXECUTE_VISION_PROMPT).execute(update);
    }
}
