package com.jakdang.labs.api.jiyun.notice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class NoticeDTO {
    @Data
    public static class CreateRequest {
        private Integer userIndex;
        private String noticeTitle;
        private String noticeDesc;
    }

    @Data
    public static class UpdateRequest {
        private Integer noticeIndex;
        private String noticeTitle;
        private String noticeDesc;
    }

    @Data
    public static class Response {
        private Integer noticeIndex;
        private String userId;
        private String noticeTitle;
        private String noticeDesc;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime noticeCreateTime;
    }
} 