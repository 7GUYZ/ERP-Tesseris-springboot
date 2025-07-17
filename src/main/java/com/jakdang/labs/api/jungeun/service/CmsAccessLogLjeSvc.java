package com.jakdang.labs.api.jungeun.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.jungeun.dto.LoginoutCmsAccessLogDTO;
import com.jakdang.labs.api.jungeun.repository.CmsAccessLogLjeRepo;
import com.jakdang.labs.entity.CmsAccessLog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CmsAccessLogLjeSvc {
    private final CmsAccessLogLjeRepo cmsAccessLogRepo;

    public void saveLog(LoginoutCmsAccessLogDTO dto){
        LocalDateTime now = LocalDateTime.now();

        CmsAccessLog entity = new CmsAccessLog(
            null, // PK는 auto_increment라면 null
            dto.getCmsAccessLogUserIndex(),
            dto.getCmsAccessUserValue(),
            dto.getCmsAccessUserIp(),
            now
        );
        cmsAccessLogRepo.save(entity);
    }
}
