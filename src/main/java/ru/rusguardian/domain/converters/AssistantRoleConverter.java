package ru.rusguardian.domain.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.rusguardian.constant.ai.AssistantRole;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class AssistantRoleConverter implements AttributeConverter<AssistantRole, String> {

    @Override
    public String convertToDatabaseColumn(AssistantRole object) {
        if (object == null) {
            return null;
        }
        return object.name();
    }

    @Override
    public AssistantRole convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }

        return Stream.of(AssistantRole.values())
                .filter(c -> c.name().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
