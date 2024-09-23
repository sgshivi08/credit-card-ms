package com.example.creditcard.client;

import com.example.creditcard.client.request.ComplianceCheckRequest;
import com.example.creditcard.client.request.IdVerificationRequest;
import com.example.creditcard.config.FeignClientMockConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "ComplianceService", url = "http://compliance-service.com/api", configuration = FeignClientMockConfig.class)
public interface ComplianceFeignClient {
    @GetMapping(value = "/checkCompliance", consumes = APPLICATION_JSON_VALUE)
    boolean checkCompliance(@RequestBody ComplianceCheckRequest complianceCheckRequest, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken);
    // Feign methods
}