package com.example.creditcard.repo;


import com.example.creditcard.entity.CreditCardApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CreditCardApplicationRepository extends JpaRepository<CreditCardApplication, Integer> {


    Optional<CreditCardApplication> findByEmiratesIdNumber(String emiratesIdNumber);
}
