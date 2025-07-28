package com.jakdang.labs.api.jihun.usercurrentpoint.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jakdang.labs.api.jihun.usercurrentpoint.service.UserCurrentPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/usercurrentpoint")
@RequiredArgsConstructor
@Slf4j
public class UserCurrentPoint {

    private final UserCurrentPointService userCurrentPointService;

    @GetMapping("/{userid}")
    public ResponseEntity<String> GetCurrnetPoint(@PathVariable("userid") String userid) {
        return ResponseEntity.ok(userCurrentPointService.GetCurrentPoint(userid));
    }

}
