package ru.rusguardian.bot.command.prompts;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.telegram.bot.util.constants.MessageType;

@RequiredArgsConstructor
@Component
public class PromptCommand extends Command {

//    @Autowired
//    private AiService chatClientService;

    @Override
    public CommandName getType() {
        return CommandName.PROMPT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        MessageType type = MessageType.getType(update);
        if (MessageType.TEXT == type) {
            commandContainerService.getCommand(CommandName.EXECUTE_TEXT_PROMPT).execute(update);
        }
        if (MessageType.VOICE == type) {
            commandContainerService.getCommand(CommandName.EXECUTE_VOICE_PROMPT).execute(update);
        }
    }
}
