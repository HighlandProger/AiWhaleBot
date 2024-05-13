package ru.rusguardian.bot.command.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommandContainerService {

    private final Map<CommandName, Command> commandMap;

    public CommandContainerService(List<Command> commands) {
        this.commandMap = commands.stream().collect(Collectors.toMap(Command::getType, Function.identity()));
    }

    public Command getCommand(CommandName commandName) {
        return commandMap.get(commandName);
    }

}
