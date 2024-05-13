package ru.rusguardian.domain.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.rusguardian.constant.user.SubscriptionType;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class SubscriptionTypeConverter implements AttributeConverter<SubscriptionType, String> {

    @Override
    public String convertToDatabaseColumn(SubscriptionType commandName) {
        if (commandName == null) {
            return null;
        }
        return commandName.name();
    }

    @Override
    public SubscriptionType convertToEntityAttribute(String commandName) {
        if (commandName == null) {
            return null;
        }

        return Stream.of(SubscriptionType.values())
                .filter(c -> c.name().equals(commandName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
