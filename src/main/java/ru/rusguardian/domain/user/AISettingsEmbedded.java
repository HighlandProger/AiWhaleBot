package ru.rusguardian.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.Data;
import ru.rusguardian.constant.ai.AIUserModel;
import ru.rusguardian.constant.ai.AssistantRole;
import ru.rusguardian.domain.converters.AIUserModelConverter;
import ru.rusguardian.domain.converters.AssistantRoleConverter;

@Embeddable
@Data
public class AISettingsEmbedded {

    @Convert(converter = AssistantRoleConverter.class)
    @Column(name = "assistant_role")
    private AssistantRole assistantRole;
    @Column(name = "ai_language")
    private String aiLanguage;
    private float temperature;
    @Convert(converter = AIUserModelConverter.class)
    @Column(name = "ai_user_model")
    private AIUserModel aiUserModel;
    @Column(name = "is_context_enabled")
    private boolean isContextEnabled;
    @Column(name = "is_voice_response_enabled")
    private boolean isVoiceResponseEnabled;

}
