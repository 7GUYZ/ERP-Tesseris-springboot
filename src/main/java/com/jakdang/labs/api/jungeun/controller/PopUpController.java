package com.jakdang.labs.api.jungeun.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.jungeun.dto.PopUpDTO;
import com.jakdang.labs.api.jungeun.service.PopUpSvc;
import com.jakdang.labs.api.common.ResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user")
public class PopUpController {
    private final PopUpSvc popUpSvc;

    @GetMapping("/getPopup")
    public ResponseEntity<ResponseDTO<List<PopUpDTO>>> getPopup() {
        try {
            List<PopUpDTO> popupImages = popUpSvc.getPopupImages();
            return ResponseEntity.ok(ResponseDTO.<List<PopUpDTO>>builder()
                .resultCode(200)
                .resultMessage("팝업 이미지 조회 성공")
                .data(popupImages)
                .build());
        } catch (Exception e) {
            log.error("팝업 이미지 조회 실패: {}", e.getMessage());
            return ResponseEntity.ok(ResponseDTO.<List<PopUpDTO>>builder()
                .resultCode(500)
                .resultMessage("팝업 이미지 조회 실패")
                .data(null)
                .build());
        }
    }
}
