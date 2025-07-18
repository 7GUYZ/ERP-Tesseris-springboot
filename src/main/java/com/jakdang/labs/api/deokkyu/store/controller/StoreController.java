package com.jakdang.labs.api.deokkyu.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.deokkyu.store.dto.CusStoreListDto;
import com.jakdang.labs.api.deokkyu.store.dto.StoreListDto;
import com.jakdang.labs.api.deokkyu.store.dto.StoreListSearchDto;
import com.jakdang.labs.api.deokkyu.store.dto.StoreRegisterdListDto;
import com.jakdang.labs.api.deokkyu.store.dto.CustomerDto;
import com.jakdang.labs.api.deokkyu.store.service.StoreService;

import java.util.List;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/list") // 가맹점 신청 현황 페이지에서 가맹점 리스트 불러오기 (검색 포함)
    public ResponseEntity<List<StoreListDto>> getFilteredStores(StoreListSearchDto filter) {
        List<StoreListDto> stores = storeService.getStoreDtos(filter);
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/customerlist") // 가맹점 고객관리 페이지에서 가맹점 리스트 불러오기 (검색 포함)
    public ResponseEntity<List<CusStoreListDto>> getStoresInCustomerList(StoreListSearchDto filter) {
        List<CusStoreListDto> stores = storeService.getStoresInCustomerList(filter);
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/customerlist/{storeUsersId}")
    public ResponseEntity<List<CustomerDto>> getStoreCustomerListByStoreUserId(@PathVariable String storeUsersId) {
        List<CustomerDto> customers = storeService.getStoreCustomerListByStoreId(storeUsersId);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/registerlist") // 가맹점 신청 현황 페이지에서 가맹점 리스트 불러오기 (검색 포함)
    public ResponseEntity<List<StoreRegisterdListDto>> getFilteredRegisterdStores(StoreListSearchDto filter) {
        List<StoreRegisterdListDto> stores = storeService.getFilteredRegisterdStores(filter);
        return ResponseEntity.ok(stores);
    }

}
