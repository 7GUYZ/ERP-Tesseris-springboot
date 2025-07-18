package com.jakdang.labs.api.taekjun.adminmypage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserTesseris;

@Repository
public interface AdminMyPageJtjRepo extends JpaRepository<UserTesseris, Integer> {

    @Query("SELECT ut FROM UserTesseris ut " +
           "LEFT JOIN FETCH ut.usersId " +
           "LEFT JOIN FETCH ut.userGender " +
           "WHERE ut.userIndex = :userIndex AND ut.userRoleIndex = :userRoleIndex")
    UserTesseris findUserTesserisWithDetails(@Param("userIndex") Integer userIndex, @Param("userRoleIndex") Integer userRoleIndex);
    

} 