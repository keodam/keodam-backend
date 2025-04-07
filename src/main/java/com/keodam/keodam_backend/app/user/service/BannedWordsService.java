package com.keodam.keodam_backend.app.user.service;

import com.keodam.keodam_backend.exception.GeneralException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URI;
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
        if (Boolean.FALSE.equals(redisTemplate.hasKey(CACHE_KEY))) {
            Set<String> bannedWords = fetchFromApi();
            if (!bannedWords.isEmpty()) {
                redisTemplate.opsForSet().add(CACHE_KEY, bannedWords.toArray(new String[0]));
            }
        }

        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(CACHE_KEY, inputNicknameWord));
    }

    private Set<String> fetchFromApi() {
        String jsonResponse = fetchApiResponse();
        return parseBannedWords(jsonResponse);
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
            if (bannedWord != null && !bannedWord.isBlank()) {
                bannedWords.add(bannedWord);
            }
        }

        return bannedWords;
    }
}

