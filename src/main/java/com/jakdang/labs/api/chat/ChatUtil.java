package com.jakdang.labs.api.chat;

import org.springframework.stereotype.Component;

import com.jakdang.labs.api.auth.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatUtil {
    private final UserService userService;
}
