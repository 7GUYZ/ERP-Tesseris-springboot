package com.jakdang.labs.api.deokkyu.modal_admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.deokkyu.modal_admin.dto.StoreDetailDto;
import com.jakdang.labs.api.deokkyu.modal_admin.dto.StoreTransactionHistoryDto;
import com.jakdang.labs.api.deokkyu.modal_admin.dto.BusinessManDetailDto;
import com.jakdang.labs.api.deokkyu.modal_admin.dto.BusinessManTransactionHistoryDto;
import com.jakdang.labs.api.deokkyu.modal_admin.dto.StoreImagesPresignedDto;
import com.jakdang.labs.api.deokkyu.modal_admin.service.ModalService;
import com.jakdang.labs.api.deokkyu.modal_admin.service.ModalImageService;

@RestController
@RequestMapping("/api/modal")
public class ModalController {
    
    private final ModalService modalService;
    private final ModalImageService modalImageService;

    public ModalController(ModalService modalService, ModalImageService modalImageService) {
        this.modalService = modalService;
        this.modalImageService = modalImageService;
    }
    
    @GetMapping("/store/transaction-history/{userId}") // 가맹점 거래내역 조회
    public ResponseEntity<List<StoreTransactionHistoryDto>> getStoreTransactionHistory(@PathVariable String userId) {
        List<StoreTransactionHistoryDto> transactionHistory = modalService.getStoreTransactionHistory(userId);
        return ResponseEntity.ok(transactionHistory);
    }

    @GetMapping("/store/detail/{storeId}") // 가맹점 상세 정보 조회
    public ResponseEntity<StoreDetailDto> getStoreDetail(@PathVariable String storeId) {
        StoreDetailDto detail = modalService.getStoreDetail(storeId);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    @GetMapping("/businessman/detail/{businessManId}") // 사업자 상세정보 조회
    public ResponseEntity<BusinessManDetailDto> getBusinessManDetail(@PathVariable String businessManId) {
        BusinessManDetailDto detail = modalService.getBusinessManDetail(businessManId);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    @GetMapping("/businessman/transaction-history/{businessManId}") // 사업자 거래내역 조회
    public ResponseEntity<List<BusinessManTransactionHistoryDto>> getBusinessManTransactionHistory(@PathVariable String businessManId) {
        List<BusinessManTransactionHistoryDto> transactionHistory = modalService.getBusinessManTransactionHistory(businessManId);
        return ResponseEntity.ok(transactionHistory);
    }

    @PutMapping("/store/update/{storeId}") // 가맹점 정보 수정
    public ResponseEntity<String> updateStore(@PathVariable String storeId, @RequestBody StoreDetailDto data) {
        boolean updated = modalService.updateStore(storeId, data);
        if (updated) {
            return ResponseEntity.ok("가맹점 정보가 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("가맹점 정보 수정에 실패했습니다.");
        }
    }

    @PutMapping("/businessman/update/{businessManId}") // 사업자 정보 수정
    public ResponseEntity<String> updateBusinessMan(@PathVariable String businessManId, @RequestBody BusinessManDetailDto data) {
        boolean updated = modalService.updateBusinessMan(businessManId, data);
        if (updated) {
            return ResponseEntity.ok("사업자 정보가 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("사업자 정보 수정에 실패했습니다.");
        }
    }

    @GetMapping("/store/register/detail/{storeId}") // 가맹점 신청 상세정보 조회
    public ResponseEntity<StoreDetailDto> getStoreRegisterDetail(@PathVariable String storeId) {
        StoreDetailDto detail = modalService.getStoreRegisterDetail(storeId);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    @PutMapping("/store/register/update/{storeId}") // 가맹점 신청 정보 수정
    public ResponseEntity<String> updateStoreRegister(@PathVariable String storeId, @RequestBody StoreDetailDto data) {
        boolean updated = modalService.updateStoreRegister(storeId, data);
        if (updated) {
            return ResponseEntity.ok("가맹점 신청 정보가 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("가맹점 신청 정보 수정에 실패했습니다.");
        }
    }

    @GetMapping("/store/images-with-presigned/{storeIndex}") // 가맹점 이미지 S3 Presigned URL 조회
    public ResponseEntity<StoreImagesPresignedDto> getStoreImagesWithPresignedUrls(@PathVariable Integer storeIndex) {
        StoreImagesPresignedDto storeImages = modalImageService.getStoreImagesWithPresignedUrls(storeIndex);
        return ResponseEntity.ok(storeImages);
    }

    
}
