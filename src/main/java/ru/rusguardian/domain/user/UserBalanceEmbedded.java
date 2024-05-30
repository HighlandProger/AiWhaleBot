package ru.rusguardian.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class UserBalanceEmbedded {

    @Column(name = "claude_tokens")
    private int claudeTokens;
    @Column(name = "extra_gpt_4_requests")
    private int extraGPT4Requests;
    @Column(name = "extra_image_requests")
    private int extraImageRequests;

}
