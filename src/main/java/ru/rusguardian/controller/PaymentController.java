package ru.rusguardian.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rusguardian.service.process.payment.ProcessPurchaseOrder;

@RestController
@RequestMapping("api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final ProcessPurchaseOrder processPurchaseOrder;

    @GetMapping("/robokassa")
    public ResponseEntity<String> setRobokassaPurchasedOrder(@RequestParam(name = "inv_id") String invId) {
        log.info("Accepted purchased robokassa order id {}", invId);
        processPurchaseOrder.process(Long.parseLong(invId));
        return new ResponseEntity<>("OK" + invId, HttpStatus.OK);
    }

    @PostMapping("/cryptocloud")
    public ResponseEntity<String> setCryptocloudPurchasedOrder(@RequestParam(name = "order_id") String orderId,
                                                               @RequestParam(name = "invoice_id") String invoiceId){
        log.info("Accepted purchased cryptocloud order id {}, invoice id {}", orderId, invoiceId);
        //TODO minor add extra info about cryptocloud invoice_id to Order
        processPurchaseOrder.process(Long.parseLong(orderId));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
