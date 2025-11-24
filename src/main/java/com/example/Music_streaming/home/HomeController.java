package com.example.Music_streaming.home;

import com.example.Music_streaming.home.dto.HomeNewReleasesResponse;
import com.example.Music_streaming.home.dto.HomeRecommendationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/recommendations")
    public HomeRecommendationResponse recommendations(
            @RequestParam(value = "seedTrackIds", required = false) List<String> seedTrackIds
    ) {
        return homeService.getRecommendations(seedTrackIds);
    }

    @GetMapping("/new-releases")
    public HomeNewReleasesResponse newReleases() {
        return homeService.getNewReleases();
    }
}
