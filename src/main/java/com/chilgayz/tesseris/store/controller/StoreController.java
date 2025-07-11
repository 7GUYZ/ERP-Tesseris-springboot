package com.chilgayz.tesseris.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chilgayz.tesseris.store.dto.StoreListDto;
import com.chilgayz.tesseris.store.dto.StoreListSearchDto;
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
    public ResponseEntity<List<StoreListDto>> getFilteredStores(StoreListSearchDto filter) {
        List<StoreListDto> stores = storeService.getStoreDtos(filter);
        return ResponseEntity.ok(stores);
    }
}
