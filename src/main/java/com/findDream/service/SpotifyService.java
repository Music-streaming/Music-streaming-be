package com.findDream.service;

import com.findDream.dto.TrackResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SpotifyService {

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    // 🔑 캐싱용 변수
    private String cachedAccessToken;
    private long tokenExpirationTime = 0; // 유효시간 추적

    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String SEARCH_URL = "https://api.spotify.com/v1/search";

    public SpotifyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * ✅ Access Token 발급 및 캐싱 (Client Credentials Flow)
     */
    private String getAccessToken() {
        long currentTime = System.currentTimeMillis();

        // 캐시된 토큰이 유효하면 재사용
        if (cachedAccessToken != null && currentTime < tokenExpirationTime) {
            return cachedAccessToken;
        }

        // 새 토큰 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);

        cachedAccessToken = (String) response.getBody().get("access_token");
        int expiresIn = (int) response.getBody().get("expires_in"); // 보통 3600초 (1시간)
        tokenExpirationTime = currentTime + (expiresIn * 1000L);

        return cachedAccessToken;
    }

    /**
     * 🎵 Spotify 트랙 검색
     */
    public List<TrackResponse> searchTracks(String query) {
        String token = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = SEARCH_URL + "?q=" + query + "&type=track&limit=5";

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        JSONObject root = new JSONObject(response.getBody());
        JSONArray items = root.getJSONObject("tracks").getJSONArray("items");

        List<TrackResponse> results = new ArrayList<>();

        for (int i = 0; i < items.length(); i++) {
            JSONObject track = items.getJSONObject(i);

            String title = track.getString("name");
            String artist = track.getJSONArray("artists").getJSONObject(0).getString("name");

            // 이미지가 없을 수도 있으므로 null-safe 처리
            String albumImage = null;
            JSONArray images = track.getJSONObject("album").getJSONArray("images");
            if (images.length() > 0) {
                albumImage = images.getJSONObject(0).getString("url");
            }

            String previewUrl = track.optString("preview_url", null);
            String spotifyUrl = track.getJSONObject("external_urls").getString("spotify");

            results.add(TrackResponse.builder()
                    .title(title)
                    .artist(artist)
                    .albumImageUrl(albumImage)
                    .previewUrl(previewUrl)
                    .spotifyUrl(spotifyUrl)
                    .build());
        }

        return results;
    }
}
