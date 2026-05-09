package com.zbank.userservice.dto.request;

import com.zbank.userservice.model.enums.DocumentType;
import com.zbank.userservice.model.enums.EmploymentType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreditApplicationRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    private String employerName;

    @NotNull(message = "Annual salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Annual salary must be positive")
    private BigDecimal annualSalary;

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @NotBlank(message = "Document number is required")
    @Size(min = 4, max = 20, message = "Document number must be between 4 and 20 characters")
    private String documentNumber;

    @NotNull(message = "Existing credit cards count is required")
    @Min(value = 0, message = "Existing credit cards count cannot be negative")
    private Integer existingCreditCards;
}
