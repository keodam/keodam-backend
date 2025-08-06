package com.keodam.keodam_backend.mypage.address.controller;

import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.global.code.status.SuccessStatus;
import com.keodam.keodam_backend.mypage.address.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Address Lookup", description = "주소 조회 API")
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "모든 시/도 목록 조회", description = "대한민국 모든 시/도 목록을 조회합니다.")
    @GetMapping("/provinces")
    public ResponseEntity<ApiResponse<List<String>>> getAllProvinces() {
        List<String> provinces = addressService.getAllProvinces();
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, provinces));
    }

    @Operation(summary = "특정 시/도의 시/군/구 목록 조회", description = "선택된 시/도에 해당하는 시/군/구 목록을 조회합니다.")
    @GetMapping("/districts")
    public ResponseEntity<ApiResponse<List<String>>> getDistrictsByProvince(@RequestParam String province) {
        List<String> districts = addressService.getDistrictsByProvince(province);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, districts));
    }

    @Operation(summary = "특정 시/군/구의 읍/면/동/리 목록 조회", description = "선택된 시/도와 시/군/구에 해당하는 읍/면/동/리 목록을 조회합니다.")
    @GetMapping("/neighborhoods")
    public ResponseEntity<ApiResponse<List<String>>> getNeighborhoodsByDistrict(
            @RequestParam String province,
            @RequestParam String district) {
        List<String> neighborhoods = addressService.getNeighborhoodsByDistrict(province, district);
        return ResponseEntity.ok(ApiResponse.of(SuccessStatus._OK, neighborhoods));
    }
}
