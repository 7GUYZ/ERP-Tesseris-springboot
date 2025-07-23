package com.jakdang.labs.api.taekjun.user_list.controller;

import com.jakdang.labs.api.taekjun.user_list.Dto.UserListResponseDTO;
import com.jakdang.labs.api.taekjun.user_list.service.UserListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/user-list")
@RequiredArgsConstructor
public class UserListController {
    private final UserListService userListService;

    @GetMapping
    public ResponseEntity<List<UserListResponseDTO>> getUserList() {
        List<UserListResponseDTO> list = userListService.getUserList();
        return ResponseEntity.ok(list);
    }
} 