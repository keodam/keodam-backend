package com.keodam.keodam_backend.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddressDataProcessor {

    public static class AddressEntry {
        public String 순위;
        public String 생성일자;
        public String 법정동코드;
        public String 읍면동명;
        public String 시군구명;
        public String 시도명;
        public String 삭제일자;
        public String 리명;
        public String 과거법정동코드;

        public String get시도명() { return 시도명; }
        public String get시군구명() { return 시군구명; }
        public String get읍면동명() { return 읍면동명; }
        public String get리명() { return 리명; }
        public String get삭제일자() { return 삭제일자; }
    }

    public Map<String, Map<String, List<String>>> loadAndProcessAddressData(InputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<AddressEntry> allEntries = objectMapper.readValue(inputStream,
                objectMapper.getTypeFactory().constructCollectionType(List.class, AddressEntry.class));

        List<AddressEntry> validEntries = allEntries.stream()
                .filter(entry -> entry.get삭제일자() == null || entry.get삭제일자().isEmpty())
                .toList();

        Map<String, Map<String, List<String>>> processedData = new HashMap<>();

        for (AddressEntry entry : validEntries) {
            String sido = entry.get시도명();
            String sigungu = entry.get시군구명();
            String eupmyeondong = entry.get읍면동명();
            String ri = entry.get리명();

            processedData.putIfAbsent(sido, new HashMap<>());
            Map<String, List<String>> sigunguMap = processedData.get(sido);

            sigunguMap.putIfAbsent(sigungu, new ArrayList<>());
            List<String> eupmyeondongList = sigunguMap.get(sigungu);

            String fullAddressPart = eupmyeondong;
            if (ri != null && !ri.isEmpty()) {
                fullAddressPart += " " + ri;
            }

            if (!eupmyeondongList.contains(fullAddressPart)) {
                eupmyeondongList.add(fullAddressPart);
            }
        }

        processedData.values().forEach(sigunguMap -> {
            sigunguMap.values().forEach(list -> list.sort(String::compareTo));
        });

        return processedData;
    }
}
