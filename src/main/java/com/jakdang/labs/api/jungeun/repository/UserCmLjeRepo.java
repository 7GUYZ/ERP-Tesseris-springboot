package com.jakdang.labs.api.jungeun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserCm;

@Repository
public interface UserCmLjeRepo extends JpaRepository<UserCm, Integer> {
    UserCm findByUserCmIndex(Integer userCmIndex);
    
    @Query(value = """
        SELECT 
            ut.user_index,
            u.name,
            u.email,
            ut.user_role_index,
            u.phone
        FROM user_tesseris ut
        INNER JOIN users u ON ut.users_id = u.id
        WHERE u.email = :email
        """, nativeQuery = true)
    Object findUserByEmail(@Param("email") String email);
}
