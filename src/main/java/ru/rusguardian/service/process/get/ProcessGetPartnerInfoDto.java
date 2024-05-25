package ru.rusguardian.service.process.get;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.user.PartnerLevel;
import ru.rusguardian.domain.Order;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.service.data.OrderService;
import ru.rusguardian.service.process.get.dto.PartnerInfoDto;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProcessGetPartnerInfoDto {

    private final OrderService orderService;
    private final ChatService chatService;

    public PartnerInfoDto get(Chat partnerChat) {
        List<Chat> referrals = chatService.findAllReferrals(partnerChat.getId());
        List<Order> purchasedReferralOrders = orderService.findAllPurchasedReferralsOrders(referrals.stream().map(Chat::getId).toList());

        double referralsPurchasedSum = purchasedReferralOrders.stream().map(Order::getPrice).reduce(0.0, Double::sum);
        double partnerIncome = purchasedReferralOrders.stream().map(Order::getPartnerIncome).filter(Objects::nonNull).reduce(0.0, Double::sum);
        PartnerLevel partnerLevel = PartnerLevel.getByPurchaseBalance(referralsPurchasedSum);

        PartnerInfoDto partnerInfoDto = new PartnerInfoDto();
        partnerInfoDto.setPartnerIncome(partnerIncome);
        partnerInfoDto.setPartnerPercent(partnerLevel.getPercentsOfPurchase());
        partnerInfoDto.setBalance(partnerChat.getPartnerEmbeddedInfo().getBalance());
        partnerInfoDto.setLevel(partnerLevel);
        partnerInfoDto.setInvitedCount(referrals.size());
        partnerInfoDto.setExtraPurchasesCount(purchasedReferralOrders.stream().filter(el -> el.getSeparatePurchase() != null).count());
        partnerInfoDto.setReferralPurchasesSum(referralsPurchasedSum);
        partnerInfoDto.setSubscriptionPurchasesCount(purchasedReferralOrders.stream().filter(el -> el.getSubscriptionType() != null).count());

        return partnerInfoDto;
    }
}
