package ru.rusguardian.domain.converters;

import jakarta.persistence.AttributeConverter;
import ru.rusguardian.constant.ai.AIUserModel;

import java.util.stream.Stream;

public class AIUserModelConverter implements AttributeConverter<AIUserModel, String> {

    @Override
    public String convertToDatabaseColumn(AIUserModel object) {
        if (object == null) {
            return null;
        }
        return object.name();
    }

    @Override
    public AIUserModel convertToEntityAttribute(String objectName) {
        if (objectName == null) {
            return null;
        }

        return Stream.of(AIUserModel.values())
                .filter(c -> c.name().equals(objectName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
