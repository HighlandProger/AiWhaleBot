package ru.rusguardian.bot.command.main.gpt_roles;

import ru.rusguardian.constant.ai.AssistantRole;

public interface GPTRolesDescription {

    default String getDescriptionText(AssistantRole type) {
        return String.format("""
                👤 Выберите роль для GPT:
                                
                %s - %s
                """, type.getName(), type.getDescription());
    }
}
