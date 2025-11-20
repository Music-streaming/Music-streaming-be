package com.example.Music_streaming.track;

import com.example.Music_streaming.track.dto.TrackSatisfactionDtos;
import com.example.Music_streaming.user.User;
import com.example.Music_streaming.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrackSatisfactionService {

    private final TrackRepository trackRepository;
    private final TrackSatisfactionRepository satisfactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void setSatisfaction(Long trackId, String userEmail, TrackSatisfactionDtos.SetRequest req) {

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var exist = satisfactionRepository.findByTrackAndUser(track, user);

        if (exist.isPresent()) {
            TrackSatisfaction s = exist.get();
            s.setType(req.getType());
        } else {
            TrackSatisfaction s = TrackSatisfaction.builder()
                    .track(track)
                    .user(user)
                    .type(req.getType())
                    .build();
            satisfactionRepository.save(s);
        }
    }

    @Transactional(readOnly = true)
    public TrackSatisfactionDtos.MyStatusResponse getMyStatus(Long trackId, String userEmail) {

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var exist = satisfactionRepository.findByTrackAndUser(track, user);

        return new TrackSatisfactionDtos.MyStatusResponse(
                exist.map(TrackSatisfaction::getType).orElse(null)
        );
    }

    @Transactional(readOnly = true)
    public TrackSatisfactionDtos.SummaryResponse getSummary(Long trackId) {

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        long satisfied = satisfactionRepository.countByTrackAndType(track, SatisfactionType.SATISFIED);
        long dissatisfied = satisfactionRepository.countByTrackAndType(track, SatisfactionType.DISSATISFIED);

        return new TrackSatisfactionDtos.SummaryResponse(satisfied, dissatisfied);
    }
}
