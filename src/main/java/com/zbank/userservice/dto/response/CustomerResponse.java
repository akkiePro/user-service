package com.zbank.userservice.dto.response;

import com.zbank.userservice.model.enums.DocumentType;
import com.zbank.userservice.model.enums.EmploymentType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CustomerResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String address;
    private EmploymentType employmentType;
    private String employerName;
    private BigDecimal annualSalary;
    private DocumentType documentType;
    private String documentNumber;
    private Integer creditScore;
    private Integer existingCreditCards;
    private LocalDateTime createdAt;
}
