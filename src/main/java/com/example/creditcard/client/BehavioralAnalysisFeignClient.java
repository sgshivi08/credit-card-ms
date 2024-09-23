package com.example.creditcard.client;

import com.example.creditcard.config.FeignClientMockConfig;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "LLMService", url = "http://llm-service.com/api", configuration = FeignClientMockConfig.class)
public interface BehavioralAnalysisFeignClient {

    @PostMapping(value = "/uploadDocument", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Headers("Content-Type: multipart/form-data")
    void uploadDocument(
            @RequestPart("file") MultipartFile file,
            @RequestPart("id") String id,
            @RequestPart("callbackUrl") String callbackUrl,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken
    );
}