package com.example.Music_streaming.track;

import com.example.Music_streaming.track.dto.TrackSatisfactionDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tracks/{trackId}/satisfaction")
public class TrackSatisfactionController {

    private final TrackSatisfactionService satisfactionService;

    @PutMapping
    public ResponseEntity<Void> setSatisfaction(
            @PathVariable Long trackId,
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody TrackSatisfactionDtos.SetRequest request
    ) {
        String email = requireUser(principal);
        satisfactionService.setSatisfaction(trackId, email, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<TrackSatisfactionDtos.MyStatusResponse> myStatus(
            @PathVariable Long trackId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String email = requireUser(principal);
        var res = satisfactionService.getMyStatus(trackId, email);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/summary")
    public ResponseEntity<TrackSatisfactionDtos.SummaryResponse> summary(
            @PathVariable Long trackId
    ) {
        var summary = satisfactionService.getSummary(trackId);
        return ResponseEntity.ok(summary);
    }

    private String requireUser(UserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return principal.getUsername();
    }
}
