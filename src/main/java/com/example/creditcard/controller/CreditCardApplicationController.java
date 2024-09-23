package com.example.creditcard.controller;


import com.example.creditcard.requestdto.CreditCardRequestDTO;
import com.example.creditcard.service.CreditCardApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/v1/creditCardApplication", produces = {MediaType.APPLICATION_JSON_VALUE})
public class CreditCardApplicationController {


    @Autowired
    private CreditCardApplicationService creditCardService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitApplication(@ModelAttribute CreditCardRequestDTO creditCardRequestDTO,
                                                    HttpServletRequest httpServletRequest) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json");
        return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders)
                .body(creditCardService.processApplication(creditCardRequestDTO));
    }

}
