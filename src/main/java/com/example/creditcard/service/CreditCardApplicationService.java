package com.example.creditcard.service;


import com.example.creditcard.client.*;
import com.example.creditcard.client.request.ComplianceCheckRequest;
import com.example.creditcard.client.request.EmploymentVerificationRequest;
import com.example.creditcard.client.request.IdVerificationRequest;
import com.example.creditcard.entity.CreditCardApplication;
import com.example.creditcard.repo.CreditCardApplicationRepository;
import com.example.creditcard.requestdto.CreditCardRequestDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;


@Service
public class CreditCardApplicationService {

    @Autowired
    private IdVerificationFeignClient idVerificationFeignClient;

    @Autowired
    private ComplianceFeignClient complianceFeignClient;

    @Autowired
    private EmploymentVerificationFeignClient employmentVerificationFeignClient;

    @Autowired
    private CreditRiskFeignClient creditRiskFeignClient;

    @Autowired
    private BehavioralAnalysisFeignClient behavioralAnalysisFeignClient;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CreditCardApplicationRepository creditCardApplicationRepository;

    // Store futures for each document upload, keyed by some identifier
    //Todo We can use caching like redis as well for scalable option but here keeping it simple
    private final Map<String, CompletableFuture<Integer>> scoreFutures = new ConcurrentHashMap<>();

    // Create an executor with virtual threads
    private final Executor virtualThreadExecutor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());

    public String processApplication(CreditCardRequestDTO creditCardRequestDTO) {

        CreditCardApplication creditCardApplication = mapToEntity(creditCardRequestDTO);
        creditCardApplication.setBankStatementPath(creditCardRequestDTO.getBankStatement().getOriginalFilename());
        //persist the credit card application details first
        CreditCardApplication creditCardApplicationSaved;
        Optional<CreditCardApplication> existingApplication = creditCardApplicationRepository.findByEmiratesIdNumber(creditCardRequestDTO.getEmiratesIdNumber());
        if (existingApplication.isPresent()) {
            return "Application Exist Already";
        } else {
            creditCardApplicationSaved = creditCardApplicationRepository.save(creditCardApplication);
        }

        // Call each service asynchronously
        IdVerificationRequest idVerificationRequest = IdVerificationRequest.builder()
                .emiratesId(creditCardRequestDTO.getEmiratesIdNumber())
                .name(creditCardRequestDTO.getName())
                .build();

        CompletableFuture<Boolean> idVerifiedResponse = CompletableFuture.supplyAsync(() ->
                idVerificationFeignClient.verifyEmiratesId(idVerificationRequest, ""), virtualThreadExecutor);

        // Wait for the CompletableFuture to complete and get the result
        boolean isIdVerified = idVerifiedResponse.join(); // or response1.get()

        if (!isIdVerified) {
            return "Application rejected due to invalid emiratesId";
        }

        // Continue processing if the ID verification passed
        ComplianceCheckRequest complianceCheckRequest = ComplianceCheckRequest.builder()
                .nationality(creditCardRequestDTO.getNationality())
                .name(creditCardRequestDTO.getName())
                .build();

        CompletableFuture<Boolean> complianceChecked = CompletableFuture.supplyAsync(() ->
                complianceFeignClient.checkCompliance(complianceCheckRequest, ""), virtualThreadExecutor);

        EmploymentVerificationRequest employmentVerificationRequest = EmploymentVerificationRequest.builder()
                .employerId(creditCardRequestDTO.getEmploymentDetails())
                .emiratesId(creditCardRequestDTO.getEmiratesIdNumber())
                .name(creditCardRequestDTO.getName())
                .build();

        CompletableFuture<Boolean> employmentVerified = CompletableFuture.supplyAsync(() ->
                employmentVerificationFeignClient.verifyEmployment(employmentVerificationRequest, ""), virtualThreadExecutor);


        CompletableFuture<Integer> creditScore = CompletableFuture.supplyAsync(() -> creditRiskFeignClient.getCreditScore(creditCardRequestDTO.getEmiratesIdNumber(), ""), virtualThreadExecutor);

        // Upload the document and get the score from the callback
        CompletableFuture<Integer> behavioralScore = uploadAndGetScore(creditCardRequestDTO.getBankStatement(), creditCardRequestDTO.getEmiratesIdNumber());

        // Combine all the results once all futures are complete, without blocking
        // Determine outcome based on the final score
        // Return outcome
        CompletableFuture<String> score = CompletableFuture.allOf(complianceChecked, employmentVerified, creditScore, behavioralScore)
                .thenApply(v -> {
                    int totalScore = 20;
                    try {
                        totalScore += complianceChecked.get().equals(true) ? 20 : 0; // We still need to check these values
                        totalScore += employmentVerified.get().equals(true) ? 20 : 0;
                        totalScore += (int) (0.2 * creditScore.get());
                        // Combine behavioral score asynchronously
                        return totalScore;
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException("Error calculating final score", e);
                    }
                })
                // Non-blocking call to combine behavioral score
                .thenCombine(behavioralScore, (currentScore, behaviorScore) ->  {
                    // Combine scores
                    int finalScore = currentScore + (int) (0.2 * behaviorScore);

                    // Store the final score
                    // Persist updated request details (including calculated score)
                    creditCardApplicationSaved.setScore(finalScore);
                    creditCardApplicationRepository.save(creditCardApplicationSaved);
                    //Todo we can call backend service by sending creditCardApplicationSaved entity with score
                    return finalScore;
                })
                .thenApply(this::determineOutcome);
       return score.join();

    }


    private String determineOutcome(int totalScore) {
        // Determine Outcome
        if (totalScore > 90) {
            return "STP — Card is issued automatically.";
        } else if (totalScore >= 75) {
            return "Near-STP — Card is issued automatically, credit limit set manually after review.";
        } else if (totalScore >= 50) {
            return "Manual Review — Application goes for further assessment.";
        } else {
            return "Application rejected due to low total score.";
        }
    }


    public CompletableFuture<Integer> uploadAndGetScore(MultipartFile bankStatement, String id) {
        // Create a CompletableFuture to hold the result
        CompletableFuture<Integer> scoreFuture = new CompletableFuture<>();

        // Store the future in the map, so it can be completed when the callback is received
        scoreFutures.put(id, scoreFuture);

        // Use virtual thread executor to handle the document upload asynchronously
        CompletableFuture.runAsync(() -> {
            behavioralAnalysisFeignClient.uploadDocument(bankStatement, id, "http://localhost:8080/api/v1/creditCardApplication/behavioralAnalysis/callback", "");
        }, virtualThreadExecutor);

        return scoreFuture;
    }

    public void handleCallback(String emiratesId, Integer score) {
        // Retrieve the future associated with this documentId
        CompletableFuture<Integer> scoreFuture = scoreFutures.get(emiratesId);
        if (scoreFuture != null) {
            // Complete the future with the score received from the callback
            scoreFuture.complete(score);
            // remove the completed future from the map
            scoreFutures.remove(emiratesId);
        }
    }

    private CreditCardApplication mapToEntity(CreditCardRequestDTO requestDTO) {
        CreditCardApplication application = new CreditCardApplication();
        application.setEmiratesIdNumber(requestDTO.getEmiratesIdNumber());
        application.setName(requestDTO.getName());
        application.setMobileNumber(requestDTO.getMobileNumber());
        application.setNationality(requestDTO.getNationality());
        application.setAddress(requestDTO.getAddress());
        application.setIncome(requestDTO.getIncome());
        application.setEmploymentDetails(requestDTO.getEmploymentDetails());
        application.setRequestedCreditLimit(requestDTO.getRequestedCreditLimit());

        // Handle file saving
        if (requestDTO.getBankStatement() != null) {

            application.setBankStatementPath(requestDTO.getBankStatement().getOriginalFilename());
        }
        return application;
    }

    //Without executor service

  /*  public CompletableFuture<Boolean> checkCompliance(ComplianceCheckRequest complianceCheckRequest) {
        // Create a CompletableFuture that will run on a virtual thread
        CompletableFuture<Boolean> complianceChecked = new CompletableFuture<>();

        // Start a virtual thread to execute the compliance check
        Thread.ofVirtual().start(() -> {
            try {
                // Call the compliance service and complete the future
                Boolean response = complianceFeignClient.checkCompliance(complianceCheckRequest, "");
                complianceChecked.complete(response);
            } catch (Exception e) {
                // Complete exceptionally if there's an error
                complianceChecked.completeExceptionally(e);
            }
        });

        return complianceChecked;
    }*/


   /* public CompletableFuture<Integer> uploadAndGetScore(MultipartFile bankStatement, String id) {
        CompletableFuture<Integer> scoreFuture = new CompletableFuture<>();
        // Store the future in the map,so it can be completed when the callback is received
        scoreFutures.put(id, scoreFuture);

        // Use virtual thread for document upload
        Thread.ofVirtual().start(() -> {
            behavioralAnalysisFeignClient.uploadDocument(bankStatement, id, "https://localhost:8080/api/documents/callback", "");
        });

        return scoreFuture;
    }*/

}

