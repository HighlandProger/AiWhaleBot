package ru.rusguardian.service.process.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.domain.Order;
import ru.rusguardian.domain.UserSubscription;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.PartnerEmbedded;
import ru.rusguardian.domain.user.UserBalanceEmbedded;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.service.data.OrderService;
import ru.rusguardian.service.data.SubscriptionService;
import ru.rusguardian.service.data.UserSubscriptionService;
import ru.rusguardian.service.process.get.ProcessGetPartnerInfoDto;
import ru.rusguardian.service.process.get.dto.PartnerInfoDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProcessPurchaseOrder {

    private final OrderService orderService;
    private final ChatService chatService;
    private final ProcessGetPartnerInfoDto getPartnerInfoDto;
    private final SubscriptionService subscriptionService;
    private final UserSubscriptionService userSubscriptionService;
    private final TelegramLongPollingBot bot;

    @Transactional
    public void process(Long orderId) {
        Order order = orderService.findById(orderId);
        order.setPurchased(true);
        order.setPurchasedAt(LocalDateTime.now());
        Chat user = order.getChat();
        Chat partner = user.getPartnerEmbeddedInfo().getInvitedBy();

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

        updatePartnerBalance(partner, partnerIncome);
        chatService.update(user);
        orderService.update(order);

        sendNotificationAboutPurchase(order);
    }

    private void updatePartnerBalance(Chat partner, double partnerIncome){
        if (partner == null || partnerIncome == 0) {return;}
        PartnerEmbedded partnerEmbedded = partner.getPartnerEmbeddedInfo();
        partnerEmbedded.setBalance(partnerEmbedded.getBalance() + partnerIncome);
        chatService.update(partner);
    }

    private void sendNotificationAboutPurchase(Order order) {
        SendMessage message = new SendMessage();
        message.setChatId(order.getChat().getId());
        message.setText(String.format("Заказ №%s успешно оплачен. Благодарим вас за покупку!", order.getId()));

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
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
            default -> throw new RuntimeException("UNKNOWN BALANCE TYPE " + balanceType);
        }
    }

    private void updateUserSubscription(Order order) {
        if (isUserAlreadyHasSubscription(order.getChat().getId())) {
            returnUnusedDaysOnBalance(order.getChat());
            userSubscriptionService.removeUserSubscriptions(order.getChat().getId());
        }

        LocalDateTime now = LocalDateTime.now();
        UserSubscription userSubscription = new UserSubscription();
        userSubscription.setChat(order.getChat());
        userSubscription.setSubscription(subscriptionService.findById(order.getSubscriptionType()));
        userSubscription.setStartTime(now);
        userSubscription.setExpirationTime(now.plusMonths(order.getSubscriptionMonths()));
        userSubscription.setOrder(order);
        userSubscription.setPurchaseProvider(order.getPurchaseProvider());

        userSubscriptionService.save(userSubscription);
    }

    private boolean isUserAlreadyHasSubscription(Long chatId) {
        Optional<UserSubscription> currentUserSubscriptionOptional = userSubscriptionService.getCurrentUserSubscriptionOptional(chatId);
        return currentUserSubscriptionOptional.isPresent();
    }

    //TODO test
    private void returnUnusedDaysOnBalance(Chat chat) {
        UserSubscription userSubscription = userSubscriptionService.getCurrentUserSubscription(chat.getId());
        Order order = userSubscription.getOrder();
        if(order == null) {return;}

        PartnerEmbedded partnerEmbedded = chat.getPartnerEmbeddedInfo();
        long unusedDays = ChronoUnit.DAYS.between(LocalDateTime.now(), userSubscription.getExpirationTime());
        long fullSubscriptionDays = ChronoUnit.DAYS.between(userSubscription.getStartTime(), userSubscription.getExpirationTime());
        double unusedValue = ((double) unusedDays / fullSubscriptionDays) * order.getPrice();

        partnerEmbedded.setBalance(partnerEmbedded.getBalance() + unusedValue);
    }
}
