package com.jakdang.labs.api.taekjun.checkpermission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.AuthorityType;

@Repository
public interface PermissionCheckRepository extends JpaRepository<AuthorityType, Long> {
    
    @Query(value = """
        SELECT 
            insert_authority,
            delete_authority,
            update_authority
        FROM authority_type 
        WHERE admin_type_index = :adminTypeIndex 
        AND program_index = :programIndex
    """, nativeQuery = true)
    Object[] checkAllPermissions(@Param("adminTypeIndex") Integer adminTypeIndex, 
                                @Param("programIndex") Integer programIndex);
} 