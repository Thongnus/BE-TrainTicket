package com.example.betickettrain.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Data
public class VnPayProperties {
    @Value("${vnPay.tmnCode}")
    private String tmnCode;
    @Value("${vnPay.hashSecret}")
    private String hashSecret;
    @Value("${vnPay.url}")
    private String url;
    @Value("${vnPay.returnUrl}")
    private String returnUrl;
}
