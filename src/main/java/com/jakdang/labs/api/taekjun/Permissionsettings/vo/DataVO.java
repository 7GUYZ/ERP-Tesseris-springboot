package com.jakdang.labs.api.taekjun.Permissionsettings.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataVO {
    // 클라이언트에게 결과를 보낼때 사용하는 VO
    private boolean success ;
    private String message;
    private Object data ;

}
