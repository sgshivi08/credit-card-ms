package com.example.creditcard;

import com.example.creditcard.client.*;
import com.example.creditcard.entity.CreditCardApplication;
import com.example.creditcard.repo.CreditCardApplicationRepository;
import com.example.creditcard.requestdto.CreditCardRequestDTO;
import com.example.creditcard.service.CreditCardApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("mock")
class CreditCardMsApplicationTests {

	@Test
	void contextLoads() {
	}

	@InjectMocks
	private CreditCardApplicationService creditCardApplicationService;

	@MockBean
	private IdVerificationFeignClient idVerificationFeignClient;

	@MockBean
	private ComplianceFeignClient complianceFeignClient;

	@MockBean
	private EmploymentVerificationFeignClient employmentVerificationFeignClient;

	@MockBean
	private CreditRiskFeignClient creditRiskFeignClient;

	@MockBean
	private BehavioralAnalysisFeignClient behavioralAnalysisFeignClient;

	@MockBean
	private ModelMapper modelMapper;

	@MockBean
	private CreditCardApplicationRepository creditCardApplicationRepository;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testCalculateFinalScore_SuccessSTP() throws IOException {
		// Prepare mock data
		CreditCardRequestDTO requestDTO = new CreditCardRequestDTO();
		requestDTO.setEmiratesIdNumber("123");
		requestDTO.setName("John Doe");
		requestDTO.setNationality("UAE");
		requestDTO.setEmploymentDetails("Some Employer");

		// Load the bank statement file from the resources
		ClassPathResource resource = new ClassPathResource("bank_statements/john_bank_statement.pdf");
		byte[] fileContent = Files.readAllBytes(resource.getFile().toPath());
		MockMultipartFile bankStatement = new MockMultipartFile("file", "john_bank_statement.pdf", "application/pdf", fileContent);

		requestDTO.setBankStatement(bankStatement);

		when(idVerificationFeignClient.verifyEmiratesId(any(), any())).thenReturn(true);
		when(complianceFeignClient.checkCompliance(any(), any())).thenReturn(true);
		when(employmentVerificationFeignClient.verifyEmployment	(any(), any())).thenReturn(true);
		when(creditRiskFeignClient.getCreditScore(any(), any())).thenReturn(100);
		when(creditCardApplicationRepository.save(any())).thenReturn(new CreditCardApplication());

		// Mock the behavioral analysis client to return a score
		doAnswer(invocation -> {
			String id = invocation.getArgument(1);
			creditCardApplicationService.handleCallback(id, 100); // Simulate callback
			return null; // No return value needed as the method is void
		}).when(behavioralAnalysisFeignClient).uploadDocument(any(), any(), any(), any());

		// Call the method
		String result = creditCardApplicationService.processApplication(requestDTO);

		// Validate the result
		assertEquals("STP — Card is issued automatically.", result);  // 20 + 20 + 20 + 20 + 20
	}

	@Test
	public void testCalculateFinalScore_SuccessManualReview() throws IOException {
		// Prepare mock data
		CreditCardRequestDTO requestDTO = new CreditCardRequestDTO();
		requestDTO.setEmiratesIdNumber("123");
		requestDTO.setName("John Doe");
		requestDTO.setNationality("UAE");
		requestDTO.setEmploymentDetails("Some Employer");

		// Load the bank statement file from the resources
		ClassPathResource resource = new ClassPathResource("bank_statements/john_bank_statement.pdf");
		byte[] fileContent = Files.readAllBytes(resource.getFile().toPath());
		MockMultipartFile bankStatement = new MockMultipartFile("file", "john_bank_statement.pdf", "application/pdf", fileContent);

		requestDTO.setBankStatement(bankStatement);

		when(idVerificationFeignClient.verifyEmiratesId(any(), any())).thenReturn(true);
		when(complianceFeignClient.checkCompliance(any(), any())).thenReturn(true);
		when(employmentVerificationFeignClient.verifyEmployment	(any(), any())).thenReturn(true);
		when(creditRiskFeignClient.getCreditScore(any(), any())).thenReturn(25);
		when(creditCardApplicationRepository.save(any())).thenReturn(new CreditCardApplication());

		// Mock the behavioral analysis client to return a score
		doAnswer(invocation -> {
			String id = invocation.getArgument(1);
			creditCardApplicationService.handleCallback(id, 25); // Simulate callback
			return null; // No return value needed as the method is void
		}).when(behavioralAnalysisFeignClient).uploadDocument(any(), any(), any(), any());

		// Call the method
		String result = creditCardApplicationService.processApplication(requestDTO);

		// Validate the result
		assertEquals("Manual Review — Application goes for further assessment.", result);  // 20 + 20 + 20 + 5 + 5
	}


	@Test
	public void testCalculateFinalScore_SuccessNearSTP() throws IOException {
		// Prepare mock data
		CreditCardRequestDTO requestDTO = new CreditCardRequestDTO();
		requestDTO.setEmiratesIdNumber("123");
		requestDTO.setName("John Doe");
		requestDTO.setNationality("UAE");
		requestDTO.setEmploymentDetails("Some Employer");

		// Load the bank statement file from the resources
		ClassPathResource resource = new ClassPathResource("bank_statements/john_bank_statement.pdf");
		byte[] fileContent = Files.readAllBytes(resource.getFile().toPath());
		MockMultipartFile bankStatement = new MockMultipartFile("file", "john_bank_statement.pdf", "application/pdf", fileContent);

		requestDTO.setBankStatement(bankStatement);

		when(idVerificationFeignClient.verifyEmiratesId(any(), any())).thenReturn(true);
		when(complianceFeignClient.checkCompliance(any(), any())).thenReturn(true);
		when(employmentVerificationFeignClient.verifyEmployment	(any(), any())).thenReturn(true);
		when(creditRiskFeignClient.getCreditScore(any(), any())).thenReturn(100);
		when(creditCardApplicationRepository.save(any())).thenReturn(new CreditCardApplication());

		// Mock the behavioral analysis client to return a score
		doAnswer(invocation -> {
			String id = invocation.getArgument(1);
			creditCardApplicationService.handleCallback(id, 50); // Simulate callback
			return null; // No return value needed as the method is void
		}).when(behavioralAnalysisFeignClient).uploadDocument(any(), any(), any(), any());

		// Call the method
		String result = creditCardApplicationService.processApplication(requestDTO);

		// Validate the result
		assertEquals("Near-STP — Card is issued automatically, credit limit set manually after review.", result);  // 20 + 20 + 20 + 20 + 10
	}



	@Test
	public void testCalculateFinalScore_IdVerificationFailed() throws IOException {
		// Prepare mock data
		CreditCardRequestDTO requestDTO = new CreditCardRequestDTO();
		requestDTO.setEmiratesIdNumber("invalid-id");
		requestDTO.setName("John Doe");

		// Load the bank statement file from the resources
		ClassPathResource resource = new ClassPathResource("bank_statements/john_bank_statement.pdf");
		byte[] fileContent = Files.readAllBytes(resource.getFile().toPath());
		MockMultipartFile bankStatement = new MockMultipartFile("file", "john_bank_statement.pdf", "application/pdf", fileContent);
		requestDTO.setBankStatement(bankStatement);
		// Mock behaviors
		when(idVerificationFeignClient.verifyEmiratesId(any(), any())).thenReturn(false);

		// Call the method
		String result = creditCardApplicationService.processApplication(requestDTO);

		// Validate the result
		assertEquals("Application rejected due to invalid emiratesId", result);
	}

}
