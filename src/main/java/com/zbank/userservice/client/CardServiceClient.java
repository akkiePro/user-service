package com.zbank.userservice.client;

import com.zbank.userservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "card-service", url = "${card-service.url:http://localhost:8082}")
public interface CardServiceClient {

    @PostMapping("/api/cards/credit-score")
    ApiResponse<Map<String, Object>> calculateCreditScore(@RequestBody Map<String, Object> request);

    @PostMapping("/api/cards/activate")
    ApiResponse<Map<String, Object>> activateCard(@RequestBody Map<String, Object> request);

    @PostMapping("/api/cards/change-pin")
    ApiResponse<Map<String, Object>> changePin(@RequestBody Map<String, Object> request);
}
