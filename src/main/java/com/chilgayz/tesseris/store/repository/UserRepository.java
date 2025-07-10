package com.chilgayz.tesseris.store.repository;

import com.chilgayz.tesseris.store.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}

