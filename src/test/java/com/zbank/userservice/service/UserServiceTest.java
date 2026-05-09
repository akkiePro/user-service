package com.zbank.userservice.service;

import com.zbank.userservice.client.CardServiceClient;
import com.zbank.userservice.dto.request.CreditApplicationRequest;
import com.zbank.userservice.dto.response.ApiResponse;
import com.zbank.userservice.exception.BusinessException;
import com.zbank.userservice.model.Customer;
import com.zbank.userservice.model.enums.DocumentType;
import com.zbank.userservice.model.enums.EmploymentType;
import com.zbank.userservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CardServiceClient cardServiceClient;

    @InjectMocks
    private UserService userService;

    private CreditApplicationRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CreditApplicationRequest();
        validRequest.setFirstName("John");
        validRequest.setLastName("Doe");
        validRequest.setEmail("john.doe@example.com");
        validRequest.setPhone("9876543210");
        validRequest.setDateOfBirth(LocalDate.of(1990, 1, 15));
        validRequest.setAddress("123 Main St");
        validRequest.setEmploymentType(EmploymentType.SALARIED);
        validRequest.setEmployerName("Tech Corp");
        validRequest.setAnnualSalary(new BigDecimal("250000"));
        validRequest.setDocumentType(DocumentType.PAN);
        validRequest.setDocumentNumber("ABCDE1234F");
        validRequest.setExistingCreditCards(0);
    }

    @Test
    void applyForCreditCard_success_newCustomer() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsByDocumentNumber(anyString())).thenReturn(false);

        Customer savedCustomer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("9876543210")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .address("123 Main St")
                .employmentType(EmploymentType.SALARIED)
                .employerName("Tech Corp")
                .annualSalary(new BigDecimal("250000"))
                .documentType(DocumentType.PAN)
                .documentNumber("ABCDE1234F")
                .existingCreditCards(0)
                .build();
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Map<String, Object> scoreData = new HashMap<>();
        scoreData.put("creditScore", 500);
        ApiResponse<Map<String, Object>> scoreResponse = ApiResponse.success("ok", scoreData);
        when(cardServiceClient.calculateCreditScore(any())).thenReturn(scoreResponse);

        Map<String, Object> cardData = new HashMap<>();
        cardData.put("cardNumber", "1234567890123456");
        cardData.put("cardType", "PLATINUM");
        ApiResponse<Map<String, Object>> cardResponse = ApiResponse.success("Card created", cardData);
        when(cardServiceClient.activateCard(any())).thenReturn(cardResponse);

        Map<String, Object> result = userService.applyForCreditCard(validRequest);

        assertThat(result).isNotNull();
        assertThat(result.get("creditScore")).isEqualTo(500);
        verify(customerRepository, times(2)).save(any(Customer.class));
    }

    @Test
    void applyForCreditCard_throwsException_whenEmailExists() {
        when(customerRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.applyForCreditCard(validRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void applyForCreditCard_throwsException_whenDocumentExists() {
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsByDocumentNumber("ABCDE1234F")).thenReturn(true);

        assertThatThrownBy(() -> userService.applyForCreditCard(validRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already exists");
    }
}
