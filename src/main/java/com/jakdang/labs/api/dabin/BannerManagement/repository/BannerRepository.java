package com.jakdang.labs.api.dabin.BannerManagement.repository;

import com.jakdang.labs.api.dabin.BannerManagement.dto.BannerResponseDto;
import com.jakdang.labs.entity.Banner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Integer> {
    
    @Query("""
        SELECT new com.jakdang.labs.api.dabin.BannerManagement.dto.BannerResponseDto(
            b.bannerIndex,
            u.usersId.name,
            b.bannerPhoto,
            b.bannerCreateTime,
            b.bannerIsvisible
        )
        FROM Banner b
        JOIN b.userIndex u
        ORDER BY b.bannerCreateTime DESC
    """)
    List<BannerResponseDto> findAllBannersWithUserInfo();
    
    @Query("""
        SELECT new com.jakdang.labs.api.dabin.BannerManagement.dto.BannerResponseDto(
            b.bannerIndex,
            u.usersId.name,
            b.bannerPhoto,
            b.bannerCreateTime,
            b.bannerIsvisible
        )
        FROM Banner b
        JOIN b.userIndex u
        WHERE b.bannerIndex = :bannerIndex
    """)
    Optional<BannerResponseDto> findBannerWithUserInfo(@Param("bannerIndex") Integer bannerIndex);
    
    @Modifying
    @Query("UPDATE Banner b SET b.bannerPhoto = :bannerPhoto, b.bannerCreateTime = :bannerCreateTime WHERE b.bannerIndex = :bannerIndex")
    int updateBannerPhotoAndCreateTime(@Param("bannerIndex") Integer bannerIndex, 
                                      @Param("bannerPhoto") String bannerPhoto, 
                                      @Param("bannerCreateTime") LocalDateTime bannerCreateTime);
} 