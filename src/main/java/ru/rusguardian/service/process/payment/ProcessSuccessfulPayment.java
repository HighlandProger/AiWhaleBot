package ru.rusguardian.service.process.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.bot.command.main.subscription.Type;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.Order;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.PartnerEmbedded;
import ru.rusguardian.domain.user.SubscriptionEmbedded;
import ru.rusguardian.domain.user.UserBalanceEmbedded;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.service.data.OrderService;
import ru.rusguardian.service.data.SubscriptionInfoService;
import ru.rusguardian.service.process.get.ProcessGetPartnerInfoDto;
import ru.rusguardian.service.process.get.dto.PartnerInfoDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ProcessSuccessfulPayment {

    private final OrderService orderService;
    private final ChatService chatService;
    private final ProcessGetPartnerInfoDto getPartnerInfoDto;
    private final SubscriptionInfoService subscriptionInfoService;

    @Transactional
    public void process(Long orderId) {
        Order order = orderService.findById(orderId);
        order.setPurchased(true);
        Chat user = order.getChat();
        Chat partner = user.getPartnerEmbeddedInfo().getInvitedBy();
        PartnerEmbedded partnerEmbedded = partner.getPartnerEmbeddedInfo();

        if (order.getSubscriptionType() == null && order.getSeparatePurchase() == null) {
            throw new RuntimeException("UNKNOWN ORDER:" + order);
        }
        if (order.getSeparatePurchase() != null) {
            updateUserExtraBalance(order);
        }
        if (order.getSubscriptionType() != null) {
            updateUserSubscription(order);
        }
        double partnerIncome = getPartnerIncome(partner, order);
        order.setPartnerIncome(partnerIncome);
        partnerEmbedded.setBalance(partnerEmbedded.getBalance() + partnerIncome);

        chatService.update(partner);
        chatService.update(user);
        orderService.update(order);
    }

    private double getPartnerIncome(Chat partner, Order order) {
        if (partner == null) {
            return 0;
        }
        PartnerInfoDto partnerInfoDto = getPartnerInfoDto.get(partner);
        return partnerInfoDto.getPartnerPercent() / 100 * order.getPrice();
    }

    private void updateUserExtraBalance(Order order) {
        UserBalanceEmbedded userBalance = order.getChat().getUserBalanceEmbedded();
        SeparatePurchase purchase = order.getSeparatePurchase();
        AIModel.BalanceType balanceType = purchase.getBalanceType();
        switch (balanceType) {
            case GPT_4 -> userBalance.setExtraGPT4Requests(userBalance.getExtraGPT4Requests() + purchase.getCount());
            case IMAGE -> userBalance.setExtraImageRequests(userBalance.getExtraImageRequests() + purchase.getCount());
            case CLAUDE -> userBalance.setClaudeTokens(userBalance.getClaudeTokens() + purchase.getCount());
            case MUSIC -> userBalance.setExtraSunoRequests(userBalance.getExtraSunoRequests() + purchase.getCount());
            default -> throw new RuntimeException("UNKNOWN BALANCE TYPE " + balanceType);
        }
    }

    private void updateUserSubscription(Order order) {
        SubscriptionEmbedded subscriptionEmbedded = order.getChat().getSubscriptionEmbedded();
        if (isUserAlreadyHasSubscription(subscriptionEmbedded)) {
            returnUnusedDaysOnBalance(order.getChat());
        }

        SubscriptionType subscriptionType = order.getSubscriptionType();
        subscriptionEmbedded.setSubscriptionInfo(subscriptionInfoService.findById(subscriptionType));
        subscriptionEmbedded.setPurchaseTime(LocalDateTime.now());
        subscriptionEmbedded.setPurchaseType(order.getPurchaseProvider());
        subscriptionEmbedded.setExpirationTime(getExpirationTime(subscriptionType));
    }

    private LocalDateTime getExpirationTime(SubscriptionType subscriptionType) {
        if(subscriptionType.getTimeType() == Type.MONTH) {return LocalDateTime.now().plusMonths(1);}
        if(subscriptionType.getTimeType() == Type.YEAR) {return LocalDateTime.now().plusYears(1);}
        throw new RuntimeException("UNKNOWN TIME TYPE " + subscriptionType.getTimeType());
    }

    private boolean isUserAlreadyHasSubscription(SubscriptionEmbedded subscriptionEmbedded) {
        return subscriptionEmbedded.getExpirationTime() != null
                && subscriptionEmbedded.getExpirationTime().isAfter(LocalDateTime.now());
    }

    //TODO test
    private void returnUnusedDaysOnBalance(Chat chat) {
        PartnerEmbedded partnerEmbedded = chat.getPartnerEmbeddedInfo();
        SubscriptionEmbedded subscriptionEmbedded = chat.getSubscriptionEmbedded();
        long unusedDays = ChronoUnit.DAYS.between(LocalDateTime.now(), subscriptionEmbedded.getExpirationTime());
        long fullSubscriptionDays = ChronoUnit.DAYS.between(subscriptionEmbedded.getPurchaseTime(), subscriptionEmbedded.getExpirationTime());
        float unusedValue = ((float) unusedDays / fullSubscriptionDays) * subscriptionEmbedded.getSubscriptionInfo().getPrice();

        partnerEmbedded.setBalance(partnerEmbedded.getBalance() + unusedValue);
    }
}
