package ru.rusguardian.domain.user;

import jakarta.persistence.*;
import lombok.Data;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.constant.ai.AITemperature;
import ru.rusguardian.domain.AssistantRoleData;
import ru.rusguardian.service.ai.constant.AIModel;

@Embeddable
@Data
public class AISettingsEmbedded {

    @ManyToOne
    @JoinColumn(name = "assistant_role_id")
    private AssistantRoleData assistantRole;
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
