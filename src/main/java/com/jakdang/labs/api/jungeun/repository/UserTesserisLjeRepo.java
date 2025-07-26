package com.jakdang.labs.api.jungeun.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserTesseris;

@Repository
public interface UserTesserisLjeRepo extends JpaRepository<UserTesseris, Integer>{
    @Query("SELECT ut FROM UserTesseris ut WHERE ut.usersId.id = :usersId")
    Optional<UserTesseris> findByUsersId(@Param("usersId") String usersId);

    @Query("SELECT CAST(ut.userIndex AS string) FROM UserTesseris ut WHERE ut.userRoleIndex = :roleIndex")
    List<String> findUserIndexesByRole(@Param("roleIndex") int roleIndex);

    @Query("SELECT CAST(a.userIndex.userIndex AS string) FROM Admin a WHERE a.adminTypeIndex.adminTypeIndex = :adminTypeIndex")
    List<String> findAdminIndexesByType(@Param("adminTypeIndex") int adminTypeIndex);
}
