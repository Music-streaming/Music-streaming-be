package com.example.Music_streaming.track.dto;

import com.example.Music_streaming.track.SatisfactionType;
import lombok.Getter;
import lombok.Setter;

public class TrackSatisfactionDtos {

    @Getter
    @Setter
    public static class SetRequest {
        private SatisfactionType type;   // SATISFIED / DISSATISFIED
    }

    @Getter
    @Setter
    public static class MyStatusResponse {
        private SatisfactionType type;   // null 이면 아직 선택 안 함

        public MyStatusResponse(SatisfactionType type) {
            this.type = type;
        }
    }

    @Getter
    @Setter
    public static class SummaryResponse {
        private long satisfiedCount;
        private long dissatisfiedCount;

        public SummaryResponse(long satisfiedCount, long dissatisfiedCount) {
            this.satisfiedCount = satisfiedCount;
            this.dissatisfiedCount = dissatisfiedCount;
        }
    }
}
