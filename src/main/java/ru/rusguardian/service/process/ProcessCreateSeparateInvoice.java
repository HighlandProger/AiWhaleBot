package ru.rusguardian.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.constant.purchase.PurchaseProvider;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.domain.Order;
import ru.rusguardian.payment.constants.RobokassaCurrency;
import ru.rusguardian.payment.web.CryptocloudService;
import ru.rusguardian.payment.web.RobokassaService;
import ru.rusguardian.service.data.OrderService;

import java.net.URL;

@Service
@RequiredArgsConstructor
public class ProcessCreateSeparateInvoice {

    private final RobokassaService robokassaService;
    private final CryptocloudService cryptocloudService;
    private final OrderService orderService;

    @Transactional
    public URL getRobokassaInvoiceUrl(SeparatePurchase separatePurchase) {
        Order order = orderService.save(new Order(PurchaseProvider.ROBOKASSA, separatePurchase));
        URL url = robokassaService.getInvoiceUrlString(separatePurchase.getPrice(), RobokassaCurrency.USD, order.getId());
        order.setInvoiceUrl(url.toString());
        return url;
    }

    //TODO
    public URL getPayselectionInvoiceUrl() {
        return null;
    }

    @Transactional
    public URL getCryptocloudInvoiceUrl(SeparatePurchase separatePurchase) {
        Order order = orderService.save(new Order(PurchaseProvider.CRYPTOCLOUD, separatePurchase));
        URL url = cryptocloudService.getInvoiceUrlString(separatePurchase.getPrice(), String.valueOf(order.getId()));
        order.setInvoiceUrl(url.toString());
        return url;
    }


}
