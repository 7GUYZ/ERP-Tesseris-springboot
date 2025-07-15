package com.jakdang.labs.api.pinChange.dto;

import lombok.Data;

public class PinChangeDTO {

  @Data
  public static class Response{
    // private Integer userCmIndex;
    private String userCmPincode;
  }
}
