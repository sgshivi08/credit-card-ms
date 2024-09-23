package com.example.creditcard.client;

import com.example.creditcard.client.request.ComplianceCheckRequest;
import com.example.creditcard.client.request.EmploymentVerificationRequest;
import com.example.creditcard.config.FeignClientMockConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "LabourService", url = "http://labour-service.com/api", configuration = FeignClientMockConfig.class)
public interface EmploymentVerificationFeignClient {
    @GetMapping(value = "/verifyEmployment", consumes = APPLICATION_JSON_VALUE)
    boolean verifyEmployment(@RequestBody EmploymentVerificationRequest employmentVerificationRequest, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken);
    // Feign methods
}