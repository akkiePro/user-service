package com.zbank.userservice.controller;

import com.zbank.userservice.dto.request.ChangePinRequest;
import com.zbank.userservice.dto.request.CreditApplicationRequest;
import com.zbank.userservice.dto.response.ApiResponse;
import com.zbank.userservice.dto.response.CustomerResponse;
import com.zbank.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<Map<String, Object>>> applyForCreditCard(
            @Valid @RequestBody CreditApplicationRequest request) {
        Map<String, Object> result = userService.applyForCreditCard(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Credit card application submitted successfully", result));
    }

    @PutMapping("/change-pin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> changePin(
            @Valid @RequestBody ChangePinRequest request) {
        Map<String, Object> result = userService.changePin(request);
        return ResponseEntity.ok(ApiResponse.success("PIN changed successfully", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomer(@PathVariable Long id) {
        CustomerResponse customer = userService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success("Customer retrieved successfully", customer));
    }
}
