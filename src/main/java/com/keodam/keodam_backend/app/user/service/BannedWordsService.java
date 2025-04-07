package com.keodam.keodam_backend.app.user.service;

import com.keodam.keodam_backend.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static com.keodam.keodam_backend.global.code.status.ErrorStatus.*;

@Service
public class BannedWordsService {

    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 2099;
    private static final String CACHE_KEY = "bannedWords";

    @Value("${banned.word.url}")
    private String apiUrl;

    @Value("${banned.word.key}")
    private String apikey;

    private final WebClient webClient;
    private final StringRedisTemplate redisTemplate;

    public BannedWordsService(WebClient.Builder webClientBuilder, StringRedisTemplate redisTemplate) {
        this.webClient = webClientBuilder.build();
        this.redisTemplate = redisTemplate;
    }

    public boolean isBannedWord(String inputNicknameWord) {
        try {
            Set<String> bannedWords = getBannedWordsFromCache();
            return bannedWords.contains(inputNicknameWord);
        } catch (Exception e) {
            throw new GeneralException(BAD_REQUEST);
        }
    }

    private Set<String> getBannedWordsFromCache() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String cached = ops.get(CACHE_KEY);

        if (cached != null) {
            String[] split = cached.split(",");
            validateBannedWords(split);
            return new HashSet<>(Arrays.asList(split));
        }

        return fetchFromApiAndCache();
    }

    private void validateBannedWords(String[] split) {
        for (String word : split) {
            if (word == null || word.isBlank()) {
                throw new GeneralException(INVALID_JSON_RESPONSE);
            }
        }
    }

    private String fetchApiResponse() {
        try {
            String fullUrl = apiUrl + "?page=" + MIN_VALUE +
                    "&perPage=" + MAX_VALUE +
                    "&serviceKey=" + apikey;

            return webClient
                    .get()
                    .uri(URI.create(fullUrl))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e) {
            throw new GeneralException(BAD_REQUEST);
        }
    }

    private Set<String> fetchFromApiAndCache() {
        String jsonResponse = fetchApiResponse();
        Set<String> bannedWords = parseBannedWords(jsonResponse);
        redisTemplate.opsForValue().set(CACHE_KEY, String.join(",", bannedWords));
        return bannedWords;
    }

    private Set<String> parseBannedWords(String jsonResponse) {
        Set<String> bannedWords = new HashSet<>();

        if (jsonResponse == null || jsonResponse.isEmpty()) {
            throw new GeneralException(EMPTY_RESPONSE);
        }

        JSONObject json = new JSONObject(jsonResponse);
        JSONArray dataArray = json.getJSONArray("data");

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject item = dataArray.getJSONObject(i);
            String bannedWord = item.getString("단어");
            bannedWords.add(bannedWord);
        }
        return bannedWords;
    }
}
