package com.jakdang.labs.api.taekjun.Permissionsettings.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.AuthorityType;
import com.jakdang.labs.api.taekjun.Permissionsettings.dto.AuthorityProgramDTO;

@Repository
public interface AdminPermissionsettingsrepository extends JpaRepository<AuthorityType, Long>{
    
    @Query("""
    SELECT new com.jakdang.labs.api.taekjun.Permissionsettings.dto.AuthorityProgramDTO(
        a.authorityTypeIndex,
        a.adminTypeIndex.adminTypeIndex,
        a.programIndex.programIndex,
        a.programIndex.programName,
        a.insertAuthority,
        a.deleteAuthority,
        a.updateAuthority
    )
    FROM AuthorityType a
    WHERE a.adminTypeIndex.adminTypeIndex = :adminTypeIndex
    ORDER BY a.adminTypeIndex.adminTypeIndex
    """)
    List<AuthorityProgramDTO> findAuthorityProgramDTOByAdminTypeIndex(@Param("adminTypeIndex") Integer adminTypeIndex);
    
    // adminTypeIndex와 programIndex로 권한 조회
    Optional<AuthorityType> findByAdminTypeIndexAdminTypeIndexAndProgramIndexProgramIndex(
        Integer adminTypeIndex, Integer programIndex);
}
