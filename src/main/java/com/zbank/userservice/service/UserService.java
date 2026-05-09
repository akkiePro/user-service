package com.zbank.userservice.service;

import com.zbank.userservice.client.CardServiceClient;
import com.zbank.userservice.dto.request.ChangePinRequest;
import com.zbank.userservice.dto.request.CreditApplicationRequest;
import com.zbank.userservice.dto.response.CustomerResponse;
import com.zbank.userservice.exception.BusinessException;
import com.zbank.userservice.exception.ResourceNotFoundException;
import com.zbank.userservice.model.Customer;
import com.zbank.userservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CustomerRepository customerRepository;
    private final CardServiceClient cardServiceClient;

    @Transactional
    public Map<String, Object> applyForCreditCard(CreditApplicationRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("A customer with email " + request.getEmail() + " already exists");
        }
        if (customerRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new BusinessException("A customer with document number " + request.getDocumentNumber() + " already exists");
        }

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .employmentType(request.getEmploymentType())
                .employerName(request.getEmployerName())
                .annualSalary(request.getAnnualSalary())
                .documentType(request.getDocumentType())
                .documentNumber(request.getDocumentNumber())
                .existingCreditCards(request.getExistingCreditCards())
                .build();

        Customer saved = customerRepository.save(customer);

        // Calculate credit score via card-service
        Map<String, Object> scoreRequest = new HashMap<>();
        scoreRequest.put("customerId", saved.getId());
        scoreRequest.put("annualSalary", saved.getAnnualSalary());
        scoreRequest.put("existingCreditCards", saved.getExistingCreditCards());

        var scoreResponse = cardServiceClient.calculateCreditScore(scoreRequest);
        int creditScore = 0;
        if (scoreResponse != null && scoreResponse.getData() != null) {
            Object scoreValue = scoreResponse.getData().get("creditScore");
            creditScore = scoreValue != null ? ((Number) scoreValue).intValue() : 0;
        }

        saved.setCreditScore(creditScore);
        customerRepository.save(saved);

        // Activate card via card-service
        Map<String, Object> activationRequest = new HashMap<>();
        activationRequest.put("customerId", saved.getId());
        activationRequest.put("creditScore", creditScore);

        var cardResponse = cardServiceClient.activateCard(activationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("customer", toResponse(saved));
        result.put("card", cardResponse != null ? cardResponse.getData() : null);
        result.put("creditScore", creditScore);
        return result;
    }

    @Transactional
    public Map<String, Object> changePin(ChangePinRequest request) {
        if (!request.getNewPin().equals(request.getConfirmNewPin())) {
            throw new BusinessException("New PIN and confirm PIN do not match");
        }

        customerRepository.findByDocumentNumber(request.getDocumentId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with document ID: " + request.getDocumentId()));

        Map<String, Object> changePinRequest = new HashMap<>();
        changePinRequest.put("cardNumber", request.getCardNumber());
        changePinRequest.put("firstTimePin", request.getFirstTimePin());
        changePinRequest.put("documentId", request.getDocumentId());
        changePinRequest.put("newPin", request.getNewPin());
        changePinRequest.put("confirmNewPin", request.getConfirmNewPin());

        var response = cardServiceClient.changePin(changePinRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("message", response != null ? response.getMessage() : "PIN changed successfully");
        result.put("card", response != null ? response.getData() : null);
        return result;
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return toResponse(customer);
    }

    private CustomerResponse toResponse(Customer c) {
        return CustomerResponse.builder()
                .id(c.getId())
                .firstName(c.getFirstName())
                .lastName(c.getLastName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .dateOfBirth(c.getDateOfBirth())
                .address(c.getAddress())
                .employmentType(c.getEmploymentType())
                .employerName(c.getEmployerName())
                .annualSalary(c.getAnnualSalary())
                .documentType(c.getDocumentType())
                .documentNumber(c.getDocumentNumber())
                .creditScore(c.getCreditScore())
                .existingCreditCards(c.getExistingCreditCards())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
