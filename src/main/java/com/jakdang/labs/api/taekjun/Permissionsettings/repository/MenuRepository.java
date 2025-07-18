package com.jakdang.labs.api.taekjun.Permissionsettings.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.Menu;



@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    @Query("SELECT new com.jakdang.labs.api.taekjun.Permissionsettings.dto.MenuDTO(m.manuIndex, m.manuName) FROM Menu m")
    List<com.jakdang.labs.api.taekjun.Permissionsettings.dto.MenuDTO> findAllMenuDTOs();
    
}
