package ru.rusguardian.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class PaymentController {

    @GetMapping("/payment")
    public ResponseEntity<String> getSuccessfulPayment(@RequestParam(name = "inv_id") String invId) {
        //TODO add functionality
//        Order order = orderHelperService.setOrderPurchased(Long.parseLong(invId));
//        orderHelperService.notifyOwnerAboutPurchase(order);
//        return new ResponseEntity<>("OK" + invId, HttpStatus.OK);
        return null;
    }
}
