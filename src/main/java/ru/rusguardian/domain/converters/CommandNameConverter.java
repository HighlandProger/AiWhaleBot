package ru.rusguardian.domain.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.rusguardian.bot.command.service.CommandName;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class CommandNameConverter implements AttributeConverter<CommandName, String> {

    @Override
    public String convertToDatabaseColumn(CommandName commandName) {
        if (commandName == null) {
            return null;
        }
        return commandName.name();
    }

    @Override
    public CommandName convertToEntityAttribute(String commandName) {
        if (commandName == null) {
            return null;
        }

        return Stream.of(CommandName.values())
                .filter(c -> c.name().equals(commandName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
