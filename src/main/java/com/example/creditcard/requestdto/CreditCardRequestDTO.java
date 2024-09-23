package com.example.creditcard.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class CreditCardRequestDTO {


    private String emiratesIdNumber;

    private String name;

    private String mobileNumber;

    private String nationality;

    private String address;

    private BigDecimal income;

    private String employmentDetails;

    private BigDecimal requestedCreditLimit;

    private MultipartFile bankStatement;

}
