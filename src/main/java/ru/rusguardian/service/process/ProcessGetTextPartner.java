package ru.rusguardian.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.PartnerEmbedded;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessGetTextPartner {

    public String get(Chat chat, String textPattern) {
        PartnerEmbedded partnerInfo = chat.getPartnerEmbeddedInfo();
        return MessageFormat.format(textPattern,
                getDate(),
                partnerInfo.getBalance(),

                partnerInfo.getPartnerLevel().getName(),
                partnerInfo.getPartnerLevel().getPercentsOfPurchase(),
                partnerInfo.getReferralsCount(),
                partnerInfo.getReferralsSubscriptionsPurchaseCount(),
                partnerInfo.getReferralsExtraPurchaseCount(),
                partnerInfo.getReferralsPurchaseValue(),
                partnerInfo.getBalance(),

                String.valueOf(chat.getId()));
    }

    private String getDate() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("ru"));
        return formatter.format(currentTime);
    }
}
