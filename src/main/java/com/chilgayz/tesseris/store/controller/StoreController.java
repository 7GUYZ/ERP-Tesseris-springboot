package com.chilgayz.tesseris.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chilgayz.tesseris.store.dto.StoreDto;
import com.chilgayz.tesseris.store.service.StoreService;

import java.util.List;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<StoreDto>> getFilteredStores(
            @RequestParam(name = "userId", required = false) String userId,
            @RequestParam(name = "userName", required = false) String userName,
            @RequestParam(name = "storeName", required = false) String storeName
    ) {
        List<StoreDto> stores = storeService.getStoreDtos(userId, userName, storeName);
        return ResponseEntity.ok(stores);
    }
}
