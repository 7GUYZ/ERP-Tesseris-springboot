package com.jakdang.labs.api.taekjun.adminmypage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.api.auth.entity.UserEntity;

@Repository
public interface UserEntityJtjRepo extends JpaRepository<UserEntity, String> {

    
}
