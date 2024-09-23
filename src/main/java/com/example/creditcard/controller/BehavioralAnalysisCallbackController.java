package com.example.creditcard.controller;

import com.example.creditcard.client.response.DocumentUploadResponse;
import com.example.creditcard.service.CreditCardApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping(value = "/api/v1/creditCardApplication/behavioralAnalysis", produces = {MediaType.APPLICATION_JSON_VALUE})
public class BehavioralAnalysisCallbackController {

    @Autowired
    private CreditCardApplicationService creditCardService;

    @PostMapping("callback")
    public ResponseEntity<Void> handleDocumentUploadCallback(@RequestBody DocumentUploadResponse response, HttpServletRequest httpServletRequest) {

        // Notify the document upload service to complete the future
        creditCardService.handleCallback(response.getEmiratesId(), response.getScore());
        return ResponseEntity.ok().build();
    }


   /* @PostMapping("/callback")
    public ResponseEntity<String> handleDocumentUploadCallback(@RequestBody DocumentUploadResponse response) {
        Optional<CreditCardApplication> creditCardApplicationOptional = repo.findById(response.getId());
        if(creditCardApplicationOptional.isPresent()){
            CreditCardApplication creditCardApplication = creditCardApplicationOptional.get();
            creditCardApplication.setScore(response.getScore());
            repo.save(creditCardApplication);
        }

        return ResponseEntity.ok("Callback received successfully");
    }*/
}
