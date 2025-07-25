package com.jakdang.labs.api.jihun.usercurrentpoint.service;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.jihun.usercurrentpoint.repository.AjgUserCurrentPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCurrentPointService {
    private final AjgUserCurrentPointRepository ajgUserCurrentPointRepository;

    public String GetCurrentPoint(String userid) {
        return ajgUserCurrentPointRepository.findByUserCmId(userid);
    }
}
