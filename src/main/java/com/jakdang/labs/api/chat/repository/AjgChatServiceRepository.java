package com.jakdang.labs.api.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserTesseris;

@Repository
public interface AjgChatServiceRepository extends JpaRepository<UserTesseris, Integer> {
    @Query(value = "SELECT * FROM user_tesseris u left join users us on u.users_id = us.id where u.user_role_index = 4", nativeQuery = true)
    List<UserTesseris> findAllAdmin();
}
