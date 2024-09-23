package com.example.creditcard.client;

import com.example.creditcard.client.request.IdVerificationRequest;
import com.example.creditcard.config.FeignClientMockConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "ICAService", configuration = FeignClientMockConfig.class)
public interface IdVerificationFeignClient {
    @GetMapping(value = "/verifyEmiratesId", consumes = APPLICATION_JSON_VALUE)
    boolean verifyEmiratesId(@RequestBody IdVerificationRequest idVerificationRequest, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken);
    // Feign methods

}