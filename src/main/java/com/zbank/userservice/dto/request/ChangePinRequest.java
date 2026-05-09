package com.zbank.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePinRequest {

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
    private String cardNumber;

    @NotBlank(message = "First time PIN is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "PIN must be 6 digits")
    private String firstTimePin;

    @NotBlank(message = "Document ID is required")
    private String documentId;

    @NotBlank(message = "New PIN is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "New PIN must be 6 digits")
    private String newPin;

    @NotBlank(message = "Confirm PIN is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Confirm PIN must be 6 digits")
    private String confirmNewPin;
}
