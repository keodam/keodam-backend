package com.keodam.keodam_backend.app.user.service;

import com.keodam.keodam_backend.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import static com.keodam.keodam_backend.global.code.status.ErrorStatus.BAD_REQUEST;
import static com.keodam.keodam_backend.global.code.status.ErrorStatus.EMPTY_RESPONSE;

@Service
@Slf4j
public class BannedWordsService {

    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 2099;

    @Value("${banned.word.url}")
    private String apiUrl;

    @Value("${banned.word.key}")
    private String apikey;

    private final WebClient webClient;

    public BannedWordsService() {
        this.webClient = WebClient.builder().build();
    }

    public boolean isBannedWord(String inputNicknameWord) {
        try {
            String jsonResponse = fetchApiResponse();
            Set<String> bannedWords = parseBannedWords(jsonResponse);

            return bannedWords.contains(inputNicknameWord);
        } catch (Exception e) {
            throw new GeneralException(BAD_REQUEST);
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

    private Set<String> parseBannedWords(String jsonResponse) throws JSONException {
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
