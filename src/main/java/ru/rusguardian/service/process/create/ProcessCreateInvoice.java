package ru.rusguardian.service.process.create;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.constant.purchase.PurchaseProvider;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.Order;
import ru.rusguardian.domain.SubscriptionInfo;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.payment.constants.RobokassaCurrency;
import ru.rusguardian.payment.web.CryptocloudService;
import ru.rusguardian.payment.web.RobokassaService;
import ru.rusguardian.service.data.OrderService;
import ru.rusguardian.service.data.SubscriptionInfoService;

import java.net.URL;

@Service
@RequiredArgsConstructor
public class ProcessCreateInvoice {

    private final RobokassaService robokassaService;
    private final CryptocloudService cryptocloudService;
    private final OrderService orderService;
    private final SubscriptionInfoService subscriptionInfoService;

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


    //TODO functional paySelection
    public URL getPayselectionInvoiceUrl() {
        return null;
    }


    @Transactional
    public URL getRobokassaInvoiceUrl(Chat chat, SubscriptionType subscriptionType) {
        SubscriptionInfo info = subscriptionInfoService.findById(subscriptionType);
        Order order = orderService.save(new Order(chat, PurchaseProvider.ROBOKASSA, info));
        URL url = robokassaService.getInvoiceUrlString(info.getPrice(), RobokassaCurrency.USD, order.getId());
        order.setInvoiceUrl(url.toString());
        return url;
    }

    @Transactional
    public URL getCryptocloudInvoiceUrl(Chat chat, SubscriptionType subscriptionType) {
        SubscriptionInfo info = subscriptionInfoService.findById(subscriptionType);
        Order order = orderService.save(new Order(chat, PurchaseProvider.CRYPTOCLOUD, info));
        URL url = cryptocloudService.getInvoiceUrlString(info.getPrice(), String.valueOf(order.getId()));
        order.setInvoiceUrl(url.toString());
        return url;
    }

}
