package ru.rusguardian.service.process.get;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.process.get.dto.PartnerInfoDto;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessGetTextPartner {

    private final ProcessGetPartnerInfoDto processGetPartnerInfoDto;

    public String get(Chat chat, String textPattern) {
        PartnerInfoDto partnerInfo = processGetPartnerInfoDto.get(chat);
        return MessageFormat.format(textPattern,
                getDate(),
                partnerInfo.getBalance(),

                partnerInfo.getLevel().getName(),
                partnerInfo.getPartnerPercent(),
                partnerInfo.getInvitedCount(),
                partnerInfo.getSubscriptionPurchasesCount(),
                partnerInfo.getExtraPurchasesCount(),
                partnerInfo.getReferralPurchasesSum(),
                partnerInfo.getPartnerIncome(),

                String.valueOf(chat.getId()));
    }

    private String getDate() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru"));
        return formatter.format(currentTime);
    }
}
