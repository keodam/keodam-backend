package com.keodam.keodam_backend.mypage.address.service;

import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.global.util.AddressDataProcessor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AddressService {

    private Map<String, Map<String, List<String>>> addressData;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("json/국토교통부_전국 법정동_20221215.json");
            try (InputStream inputStream = resource.getInputStream()) {
                AddressDataProcessor processor = new AddressDataProcessor();
                addressData = processor.loadAndProcessAddressData(inputStream);
            }
        } catch (IOException e) {
            log.error("Failed to load address data: {}", e.getMessage());
            throw new RuntimeException("Failed to load address data", e);
        }
    }

    @Transactional(readOnly = true)
    public List<String> getAllProvinces() {
        return new ArrayList<>(addressData.keySet());
    }

    @Transactional(readOnly = true)
    public List<String> getDistrictsByProvince(String province) {
        Map<String, List<String>> districtsMap = addressData.get(province);
        if (districtsMap == null) {
            throw new GeneralException(ErrorStatus.INVALID_PROVINCE);
        }
        return new ArrayList<>(districtsMap.keySet());
    }

    @Transactional(readOnly = true)
    public List<String> getNeighborhoodsByDistrict(String province, String district) {
        Map<String, List<String>> districtsMap = addressData.get(province);
        if (districtsMap == null) {
            throw new GeneralException(ErrorStatus.INVALID_PROVINCE);
        }
        List<String> neighborhoods = districtsMap.get(district);
        if (neighborhoods == null) {
            throw new GeneralException(ErrorStatus.INVALID_DISTRICT);
        }
        return new ArrayList<>(neighborhoods);
    }
}
