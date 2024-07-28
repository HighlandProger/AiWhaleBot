package ru.rusguardian.service.process.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.Order;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.service.data.OrderService;
import ru.rusguardian.service.data.SubscriptionService;
import ru.rusguardian.service.process.create.ProcessCreateChat;
import ru.rusguardian.service.process.get.ProcessGetPartnerInfoDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
//TODO need test!
class ProcessPurchaseOrderTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ProcessGetPartnerInfoDto getPartnerInfoDto;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private ProcessPurchaseOrder processPurchaseOrder;
    @Autowired
    private ProcessCreateChat processCreateChat;

    private Order order;
    private Chat chat;
    private Chat partnerChat;

    @BeforeEach
    void setUp() {
        partnerChat = chatService.findById(1L);
        chat = chatService.findById(2L);

        order = new Order();
        order.setId(1L);
        order.setChat(chat);
        order.setPrice(100.0f);
        order.setSeparatePurchase(null);
        order.setSubscriptionType(SubscriptionType.ALPHA);

        order = orderService.save(order);
    }

    @Test
    void testProcess_shouldUpdateOrderAsPurchased() {
        processPurchaseOrder.process(order.getId());

        assertTrue(order.isPurchased());
        assertEquals(15.0, partnerChat.getPartnerEmbeddedInfo().getBalance());
    }

    @Test
    void testProcess_shouldThrowExceptionForUnknownOrder() {
        // Настройка данных
        order.setSeparatePurchase(null);
        order.setSubscriptionType(null);

        // Ожидаемый результат
        Exception exception = assertThrows(RuntimeException.class, () -> {
            processPurchaseOrder.process(order.getId());
        });

        assertTrue(exception.getMessage().contains("UNKNOWN ORDER"));
    }

    @Test
    void testProcess_shouldUpdateUserBalanceForSeparatePurchase() {
        order.setSeparatePurchase(SeparatePurchase.GPT4_100);
        order.setSubscriptionType(null);
        order.setPrice(SeparatePurchase.GPT4_100.getPrice());

        processPurchaseOrder.process(order.getId());

        assertEquals(100, chat.getUserBalanceEmbedded().getExtraGPT4Requests());
        assertEquals(SeparatePurchase.GPT4_100.getPrice() * 0.15, partnerChat.getPartnerEmbeddedInfo().getBalance());
    }
}
