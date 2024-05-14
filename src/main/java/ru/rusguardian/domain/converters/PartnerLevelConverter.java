package ru.rusguardian.domain.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.rusguardian.constant.user.PartnerLevel;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class PartnerLevelConverter implements AttributeConverter<PartnerLevel, String> {

    @Override
    public String convertToDatabaseColumn(PartnerLevel object) {
        if (object == null) {
            return null;
        }
        return object.name();
    }

    @Override
    public PartnerLevel convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }

        return Stream.of(PartnerLevel.values())
                .filter(c -> c.name().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
