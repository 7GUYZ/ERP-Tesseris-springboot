package com.jakdang.labs.api.jiyun.pinChange.dto;

import lombok.Data;

public class PinChangeDTO {

  @Data
  public static class Response{
    private Integer userCmIndex;
    private String userCmPincode;
  }

    @Data
    public static class PasswordVerifyRequest {
        private String password;
    }
} 