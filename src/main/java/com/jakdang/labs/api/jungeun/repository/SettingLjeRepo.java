package com.jakdang.labs.api.jungeun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.Setting;

@Repository
public interface SettingLjeRepo extends JpaRepository<Setting, Integer> {
    Setting findBySettingIndex(Integer settingIndex);
}
