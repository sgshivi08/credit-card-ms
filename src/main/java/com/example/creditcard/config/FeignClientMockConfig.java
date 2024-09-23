package com.example.creditcard.config;

import com.example.creditcard.client.*;
import com.example.creditcard.client.request.ComplianceCheckRequest;
import com.example.creditcard.client.request.EmploymentVerificationRequest;
import com.example.creditcard.client.request.IdVerificationRequest;
import com.example.creditcard.client.response.DocumentUploadResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;



@Configuration
@Profile("mock")
public class FeignClientMockConfig {

    @Bean
    public IdVerificationFeignClient idVerificationFeignClient() {
        return new IdVerificationFeignClient() {
            @Override
            public boolean verifyEmiratesId(IdVerificationRequest idVerificationRequest, String token) {
                return idVerificationRequest.getEmiratesId().equals("123");

            }
        };
    }

    @Bean
    public ComplianceFeignClient complianceFeignClient() {
        return new ComplianceFeignClient() {
            @Override
            public boolean checkCompliance(ComplianceCheckRequest complianceCheckRequest, String authToken) {
                return complianceCheckRequest.getName().equals("John Doe");
            }
        };
    }

    @Bean
    public EmploymentVerificationFeignClient employmentVerificationFeignClient() {
        return new EmploymentVerificationFeignClient() {
            @Override
            public boolean verifyEmployment(EmploymentVerificationRequest employmentVerificationRequest, String authToken) {
                // Provide mock implementation
                return true;
            }
        };
    }


    @Bean
    public CreditRiskFeignClient creditRiskFeignClient() {
        return new CreditRiskFeignClient() {
            @Override
            public Integer getCreditScore(String emiratesId, String authToken) {
                // Provide mock implementation
                return 100;
            }
        };
    }

    @Bean
    public BehavioralAnalysisFeignClient behavioralAnalysisFeignClient() {
        return new BehavioralAnalysisFeignClient() {
            @Override
            public void uploadDocument(MultipartFile file, String id, String url, String authToken) {
                // Provide mock implementation
                simulateCallback(id, url);
            }

            private void simulateCallback(String id, String callbackUrl) {
                // Directly trigger the callback endpoint in your application without delay
                RestTemplate restTemplate = new RestTemplate();
                // Prepare the mock callback data
                DocumentUploadResponse mockResponse = new DocumentUploadResponse();
                mockResponse.setEmiratesId(id);
                mockResponse.setScore(50); // Mocked score of 85
                // Create headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // Create an HttpEntity with the request body and headers
                HttpEntity<DocumentUploadResponse> requestEntity = new HttpEntity<>(mockResponse, headers);

                // Send the callback request
                restTemplate.postForObject(callbackUrl, requestEntity, Void.class);
            }
        };
    }
}
