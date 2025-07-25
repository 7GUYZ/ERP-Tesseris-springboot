package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.jakdang.labs.entity.StoreImage;
import java.util.List;
import java.util.Optional;

public interface StoreBasicInfoImageJdbRepo extends JpaRepository<StoreImage, Integer> {
    @Query("SELECT si FROM StoreImage si WHERE si.storeUserIndex.storeIndex = :storeIndex ORDER BY si.storeImageIndex ASC")
    List<StoreImage> findAllByStoreIndex(@Param("storeIndex") Integer storeIndex);

    @Query("SELECT si FROM StoreImage si WHERE si.storeUserIndex.storeIndex = :storeIndex AND si.storeMainImageStatus = :status ORDER BY si.storeImageIndex ASC")
    List<StoreImage> findByStoreIndexAndStatus(@Param("storeIndex") Integer storeIndex, @Param("status") String status);

    @Query("SELECT si FROM StoreImage si WHERE si.storeUserIndex.storeIndex = :storeIndex AND si.storeMainImageStatus = 'T'")
    List<StoreImage> findMainImageByStoreIndex(@Param("storeIndex") Integer storeIndex);

    @Query("SELECT si FROM StoreImage si WHERE si.storeUserIndex.storeIndex = :storeIndex AND si.storeMainImageStatus = 'N' ORDER BY si.storeImageIndex ASC")
    List<StoreImage> findDetailImagesByStoreIndex(@Param("storeIndex") Integer storeIndex);

    void deleteByStoreUserIndex_StoreIndex(Integer storeIndex);

    @Query("SELECT COUNT(si) FROM StoreImage si WHERE si.storeUserIndex.storeIndex = :userIndex")
    Long countByUserIndex(@Param("userIndex") Integer userIndex);

    @Query("SELECT si FROM StoreImage si WHERE si.storeUserIndex.storeIndex = :userIndex AND si.storeMainImageStatus = 'T'")
    Optional<StoreImage> findMainImageByUserIndex(@Param("userIndex") Integer userIndex);
} 