package ru.rusguardian.bot.command.main.text_command_distributor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;

@Component
@RequiredArgsConstructor
public class TextCommandDistributorCommand extends Command {

    @Override
    public CommandName getType() {
        return CommandName.TEXT_COMMAND_DISTRIBUTOR;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        String command = getViewTextMessage(update);

        if (command.startsWith("/start")) {
            commandContainerService.getCommand(CommandName.START_VIEW_D).execute(update);
            return;
        }
        if (command.equals("/account")) {
            commandContainerService.getCommand(CommandName.MY_ACCOUNT).execute(update);
            return;
        }
        if (command.equals("/premium")) {
            commandContainerService.getCommand(CommandName.SUBSCRIPTION).execute(update);
            return;
        }
        if (command.startsWith("/img")) {
            commandContainerService.getCommand(CommandName.OBTAIN_TEXT_2_IMAGE_PROMPT_VIEW_D).execute(update);
            return;
        }
        if (command.startsWith("/vision")) {
            commandContainerService.getCommand(CommandName.OBTAIN_VISION_PROMPT_VIEW_D).execute(update);
            return;
        }
        if (command.equals("/role")) {
            commandContainerService.getCommand(CommandName.GPT_ROLES_VIEW).execute(update);
            return;
        }
        if (command.equals("/settings")) {
            commandContainerService.getCommand(CommandName.SETTINGS).execute(update);
            return;
        }
        if (command.equals("/partner")) {
            commandContainerService.getCommand(CommandName.PARTNER_CABINET).execute(update);
            return;
        }

        sendMessage(update, "Функционал еще в разработке");
    }
}
