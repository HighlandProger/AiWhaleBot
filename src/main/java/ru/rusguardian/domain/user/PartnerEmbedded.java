package ru.rusguardian.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Embeddable
@Data
public class PartnerEmbedded {

    @OneToOne
    @JoinColumn(name = "invited_by")
    private Chat invitedBy;
    @Column(name = "balance")
    private double balance;

}
