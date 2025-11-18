package com.example.Music_streaming.youtube;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class YouTubeController {

    private final YouTubeService youTubeService;

    @GetMapping("/search")
    public ResponseEntity<?> searchVideo(@RequestParam String query) {
        String videoId = youTubeService.searchVideoId(query);

        if (videoId == null) {
            return ResponseEntity.notFound().build();
        }

        // 프론트에서 바로 embed URL 쓸 수 있게 반환해도 되고,
        // videoId만 보내도 됨. 여기선 둘 다 보내줄게.
        return ResponseEntity.ok(new YouTubeVideoResponse(videoId,
                "https://www.youtube.com/embed/" + videoId));
    }

    public record YouTubeVideoResponse(String videoId, String embedUrl) {}
}
