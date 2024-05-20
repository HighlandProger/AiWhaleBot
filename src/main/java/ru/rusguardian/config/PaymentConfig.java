package ru.rusguardian.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.rusguardian.payment.properties.RobokassaProperties;
import ru.rusguardian.payment.web.CryptocloudService;
import ru.rusguardian.payment.web.RobokassaService;

@Configuration
public class PaymentConfig {

    @Bean
    public RobokassaService robokassaService(
            @Value("${payment.robokassa.merchant-login}") String merchantLogin,
            @Value("${payment.robokassa.merchant-password}") String merchantPassword,
            RestTemplate restTemplate) {
        return new RobokassaService(new RobokassaProperties(merchantLogin, merchantPassword, restTemplate));
    }

    @Bean
    public CryptocloudService cryptocloudService(
            @Value("${payment.cryptocloud.shop-id}") String shopId,
            @Value("${payment.cryptocloud.token}") String token,
            RestTemplate restTemplate
    ) {
        return new CryptocloudService(shopId, token, restTemplate);
    }

}
