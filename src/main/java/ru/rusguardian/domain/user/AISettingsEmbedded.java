package ru.rusguardian.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.constant.ai.AITemperature;
import ru.rusguardian.service.ai.constant.AIModel;

@Embeddable
@Data
public class AISettingsEmbedded {

    @Column(name = "assistant_role_name")
    private String assistantRoleName;
    @Enumerated(EnumType.STRING)
    @Column(name = "ai_language")
    private AILanguage aiLanguage;
    @Enumerated(EnumType.STRING)
    private AITemperature temperature;
    @Enumerated(EnumType.STRING)
    @Column(name = "ai_active_model")
    private AIModel aiActiveModel;
    @Column(name = "is_context_enabled")
    private boolean isContextEnabled;
    @Column(name = "is_voice_response_enabled")
    private boolean isVoiceResponseEnabled;

}
