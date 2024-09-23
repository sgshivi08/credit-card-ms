package com.example.creditcard.client;

import com.example.creditcard.config.FeignClientMockConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "AECBService", url = "http://aecb-service.com/api", configuration = FeignClientMockConfig.class)
public interface CreditRiskFeignClient {
    @GetMapping(value = "/getCreditScore", consumes = APPLICATION_JSON_VALUE)
    Integer getCreditScore(@RequestParam(value = "emiratesId") String emiratesId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken);
    // Feign methods
}