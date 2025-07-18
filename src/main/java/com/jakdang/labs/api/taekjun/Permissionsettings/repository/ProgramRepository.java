package com.jakdang.labs.api.taekjun.Permissionsettings.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.Program;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Integer> {

    List<Program> findByMenuIndex(Integer menuIndex);
    
    @Query("SELECT new com.jakdang.labs.api.taekjun.Permissionsettings.dto.ProgramDTO(p.programIndex, p.programName) FROM Program p WHERE p.menuIndex = :menuIndex")
    List<com.jakdang.labs.api.taekjun.Permissionsettings.dto.ProgramDTO> findProgramDTOsByMenuIndex(Integer menuIndex);
  
} 