package ru.rusguardian.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ru.rusguardian.constant.ai.AssistantRole;
import ru.rusguardian.service.ai.constant.AIModel;

@Embeddable
@Data
public class AISettingsEmbedded {

    @Enumerated(EnumType.STRING)
    @Column(name = "assistant_role")
    private AssistantRole assistantRole;
    @Column(name = "ai_language")
    private String aiLanguage;
    private float temperature;
    @Enumerated(EnumType.STRING)
    @Column(name = "ai_active_model")
    private AIModel aiActiveModel;
    @Column(name = "is_context_enabled")
    private boolean isContextEnabled;
    @Column(name = "is_voice_response_enabled")
    private boolean isVoiceResponseEnabled;

}