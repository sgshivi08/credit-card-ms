package com.example.creditcard.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_card_application")
public class CreditCardApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(name = "emirates_id_number")
    private String emiratesIdNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "address")
    private String address;

    @Column(name = "income")
    private BigDecimal income;

    @Column(name = "employment_details")
    private String employmentDetails;

    @Column(name = "requested_credit_limit")
    private BigDecimal requestedCreditLimit;

    @Column(name = "bank_statement_path")
    private String bankStatementPath; // Store the file path or URL

    @Column(name = "score")
    private Integer score;

}

