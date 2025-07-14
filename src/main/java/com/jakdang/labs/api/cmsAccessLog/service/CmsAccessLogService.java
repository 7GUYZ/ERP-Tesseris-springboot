package com.jakdang.labs.api.cmsAccessLog.service;

import com.jakdang.labs.api.cmsAccessLog.dto.AdminTypeDTO;
import com.jakdang.labs.api.cmsAccessLog.repository.CmsAccessLogRepository;
import com.jakdang.labs.api.cmsAccessLog.repository.AdminTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CmsAccessLogService {
    
    private final CmsAccessLogRepository cmsAccessLogRepository;
    private final AdminTypeRepository adminTypeRepository;
    
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
            cmsAccessUserTimeStart != null ? cmsAccessUserTimeStart + " 00:00:00" : "",
            cmsAccessUserTimeEnd != null ? cmsAccessUserTimeEnd + " 23:59:59" : ""
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