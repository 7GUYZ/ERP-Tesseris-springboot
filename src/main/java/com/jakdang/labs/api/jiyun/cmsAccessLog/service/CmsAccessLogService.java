package com.jakdang.labs.api.jiyun.cmsAccessLog.service;

import com.jakdang.labs.api.jiyun.cmsAccessLog.dto.AdminTypeDTO;
import com.jakdang.labs.api.jiyun.cmsAccessLog.repository.CmsAccessLogkjyRepository;
import com.jakdang.labs.api.jiyun.cmsAccessLog.repository.AdminTypekjyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CmsAccessLogService {
    
    private final CmsAccessLogkjyRepository cmsAccessLogRepository;
    private final AdminTypekjyRepository adminTypeRepository;
    
    public List<Object[]> searchCmsAccessLogs(
        String userId,
        String userName,
        String cmsAccessUserIp,
        String adminTypeIndex,
        String cmsAccessUserTimeStart,
        String cmsAccessUserTimeEnd
    ) {
        List<Object[]> result = cmsAccessLogRepository.searchCmsAccessLogs(
            userId != null ? userId : "",
            userName != null ? userName : "",
            cmsAccessUserIp != null ? cmsAccessUserIp : "",
            adminTypeIndex != null ? adminTypeIndex : "0",
            cmsAccessUserTimeStart != null && !cmsAccessUserTimeStart.isEmpty() ? cmsAccessUserTimeStart + " 00:00:00" : null,
            cmsAccessUserTimeEnd != null && !cmsAccessUserTimeEnd.isEmpty() ? cmsAccessUserTimeEnd + " 23:59:59" : null
        );
        return result;
    }
    
    public List<AdminTypeDTO> getAllAdminTypes() {
        return adminTypeRepository.findAll().stream()
            .map(adminType -> {
                AdminTypeDTO dto = new AdminTypeDTO();
                dto.setAdminTypeIndex(adminType.getAdminTypeIndex());
                dto.setAdminTypeName(adminType.getAdminTypeName());
                return dto;
            })
            .collect(Collectors.toList());
    }
} 