package ru.rusguardian.service.process.create;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.constant.purchase.PurchaseProvider;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.Order;
import ru.rusguardian.domain.Subscription;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.payment.constants.RobokassaCurrency;
import ru.rusguardian.payment.web.CryptocloudService;
import ru.rusguardian.payment.web.RobokassaService;
import ru.rusguardian.service.data.OrderService;
import ru.rusguardian.service.data.SubscriptionService;

import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessCreateInvoice {

    private final RobokassaService robokassaService;
    private final CryptocloudService cryptocloudService;
    private final OrderService orderService;
    private final SubscriptionService subscriptionService;

    @Transactional
    public URL getRobokassaInvoiceUrl(Chat chat, SeparatePurchase separatePurchase) {
        Order order = orderService.save(new Order(chat, PurchaseProvider.ROBOKASSA, separatePurchase));
        URL url = robokassaService.getInvoiceUrlString(separatePurchase.getPrice(), RobokassaCurrency.USD, order.getId());
        order.setInvoiceUrl(url.toString());
        return url;
    }

    @Transactional
    public URL getCryptocloudInvoiceUrl(Chat chat, SeparatePurchase separatePurchase) {
        Order order = orderService.save(new Order(chat, PurchaseProvider.CRYPTOCLOUD, separatePurchase));
        URL url = cryptocloudService.getInvoiceUrlString(separatePurchase.getPrice(), String.valueOf(order.getId()));
        order.setInvoiceUrl(url.toString());
        return url;
    }

    @Transactional
    public URL getRobokassaInvoiceUrl(Chat chat, SubscriptionType subscriptionType, int months) {
        Subscription subscription = subscriptionService.findById(subscriptionType);
        double fullPrice = subscription.getOneMonthPrice() * months;
        Order order = orderService.save(new Order(chat, PurchaseProvider.ROBOKASSA, subscription, months));
        URL url = robokassaService.getInvoiceUrlString(fullPrice, RobokassaCurrency.USD, order.getId());
        order.setInvoiceUrl(url.toString());
        return url;
    }

    @Transactional
    public URL getCryptocloudInvoiceUrl(Chat chat, SubscriptionType subscriptionType, int months) {
        log.info("Getting cryptocloud invoice url for chat {}, subscritpion: {}, months: {}", chat.getId(), subscriptionType, months);
        Subscription subscription = subscriptionService.findById(subscriptionType);
        double fullPrice = subscription.getOneMonthPrice() * months;
        Order order = orderService.save(new Order(chat, PurchaseProvider.CRYPTOCLOUD, subscription, months));
        URL url = cryptocloudService.getInvoiceUrlString(fullPrice, String.valueOf(order.getId()));
        order.setInvoiceUrl(url.toString());
        return url;
    }

}
