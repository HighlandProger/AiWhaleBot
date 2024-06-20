package ru.rusguardian.service.ai.dto.stable_diffusion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StringToEmptyListDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.getText().isEmpty()) {
            return new ArrayList<>();
        }
        JsonNode node = p.readValueAsTree();
        if (node.isArray()) {
            List<String> result = new ArrayList<>();
            for (JsonNode item : node) {
                result.add(item.asText());
            }
            return result;
        } else {
            if (node.asText().isEmpty()) {
                return new ArrayList<>();
            }
            List<String> singleItemList = new ArrayList<>();
            singleItemList.add(node.asText());
            return singleItemList;
        }
    }
}