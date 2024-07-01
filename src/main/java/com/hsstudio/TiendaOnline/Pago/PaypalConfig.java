package com.hsstudio.TiendaOnline.Pago;

import com.paypal.base.rest.APIContext;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfig {
    @Value("${paypal.client.id}")
    private String clientId;
    @Value("${paypal.client.secret}")
    private String clientSecret;
    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public APIContext apiContext() {
        APIContext context = new APIContext(clientId, clientSecret, mode);
        context.setConfigurationMap(Map.of(
            "mode", mode,
            "http.ConnectionTimeOut", "1000",
            "http.Retry", "1",
            "service.EndPoint", "https://api-m.sandbox.paypal.com"
        ));
        return context;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}