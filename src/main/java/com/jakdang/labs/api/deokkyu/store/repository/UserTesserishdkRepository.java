package com.jakdang.labs.api.deokkyu.store.repository;

import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.entity.UserTesseris;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserTesserishdkRepository extends JpaRepository<UserTesseris, Integer> {
    Optional<UserTesseris> findByUserIndex(Integer userIndex);
    List<UserTesseris> findByUsersId(UserEntity usersId);
} 