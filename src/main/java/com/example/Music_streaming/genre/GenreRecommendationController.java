package com.example.Music_streaming.genre;

import com.example.Music_streaming.genre.dto.GenreRecommendationResponse;
import com.example.Music_streaming.home.dto.RecommendedTrackDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@Tag(name = "Genre Recommendations", description = "Spotify 장르 Seed 기반 추천 API")
public class GenreRecommendationController {
    private final GenreRecommendationService genreRecommendationService;

    @GetMapping("/seeds")
    @Operation(
            summary = "Spotify 장르 Seed 조회",
            description = """
                    로그인 후 발급받은 JWT를 Authorization 헤더로 전달해야 하며,
                    먼저 /api/spotify/tokens 에 Spotify Access Token을 저장한 사용자만 호출할 수 있습니다.
                    내부적으로는 저장한 Spotify 사용자 토큰으로 /recommendations/available-genre-seeds 를 조회합니다."""
    )
    public List<String> getAvailableSeeds(
    ) {
        return genreRecommendationService.getAvailableGenres();
    }

    @GetMapping("/recommendations")
    @Operation(
            summary = "장르 기반 추천",
            description = """
                    ?genres=pop&genres=k-pop 과 같이 장르를 전달하면 Spotify Recommendations API로 인기/추천 곡을 가져옵니다.
                    인증된 사용자이며 /api/spotify/tokens 로 저장된 Spotify Access Token이 있어야 하며,
                    Spotify API에서 404가 발생하면 동일 장르 키워드 검색으로 대체 데이터를 반환합니다."""
    )
    public GenreRecommendationResponse getRecommendations(
            @Parameter(description = "Spotify 장르 코드. ?genres=pop&genres=k-pop 처럼 다중 전달 가능", required = true)
            @RequestParam("genres") List<String> genres,
            @Parameter(description = "추천 결과 개수 (1~50)", example = "12")
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        List<RecommendedTrackDto> tracks = genreRecommendationService.getRecommendations(genres, limit);
        return new GenreRecommendationResponse(tracks);
    }
}
