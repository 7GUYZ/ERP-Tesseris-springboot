package com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.jakdang.labs.entity.StoreImage;
import java.util.List;
import java.util.Optional;

// StoreImage 관련 Repository 통합본 (기존 FrontMyPageStoreImageJdbRepo, StoreBasicInfoImageJdbRepo, StoreRepresentativeImageJdbRepo)
public interface StoreImageJdbRepo extends JpaRepository<StoreImage, Integer> {
    @Query("SELECT si FROM StoreImage si WHERE si.storeUserIndex.storeIndex = :storeIndex ORDER BY si.storeImageIndex ASC")
    List<StoreImage> findAllByStoreIndex(@Param("storeIndex") Integer storeIndex);

    @Query("SELECT si FROM StoreImage si WHERE si.storeUserIndex.storeIndex = :storeIndex AND si.storeMainImageStatus = :status ORDER BY si.storeImageIndex ASC")
    List<StoreImage> findByStoreIndexAndStatus(@Param("storeIndex") Integer storeIndex, @Param("status") String status);

    @Query("SELECT si FROM StoreImage si WHERE si.storeUserIndex.storeIndex = :storeIndex AND si.storeMainImageStatus = 'T'")
    List<StoreImage> findMainImageByStoreIndex(@Param("storeIndex") Integer storeIndex);

    @Query("SELECT si FROM StoreImage si WHERE si.storeUserIndex.storeIndex = :storeIndex AND si.storeMainImageStatus = 'N' ORDER BY si.storeImageIndex ASC")
    List<StoreImage> findDetailImagesByStoreIndex(@Param("storeIndex") Integer storeIndex);

    void deleteByStoreUserIndex_StoreIndex(Integer storeIndex);

    // 추가 메서드 (StoreBasicInfoImageJdbRepo에서 가져옴)
    @Query("SELECT COUNT(si) FROM StoreImage si WHERE si.storeUserIndex.storeIndex = :userIndex")
    Long countByUserIndex(@Param("userIndex") Integer userIndex);

    @Query("SELECT si FROM StoreImage si WHERE si.storeUserIndex.storeIndex = :userIndex AND si.storeMainImageStatus = 'T'")
    Optional<StoreImage> findMainImageByUserIndex(@Param("userIndex") Integer userIndex);
} 