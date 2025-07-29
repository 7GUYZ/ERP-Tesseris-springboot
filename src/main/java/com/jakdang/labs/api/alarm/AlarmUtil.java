package com.jakdang.labs.api.alarm;

import org.springframework.stereotype.Component;

import com.jakdang.labs.api.auth.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlarmUtil {
    private final UserService userService;
}
