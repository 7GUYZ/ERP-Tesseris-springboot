package com.jakdang.labs.api.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.api.chat.dto.ChatAdminListResponseDto;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.Admin;
import com.jakdang.labs.entity.adminType;

@Repository
public interface AjgChatServiceRepository extends JpaRepository<UserTesseris, Integer> {
    @Query(value = "SELECT * FROM user_tesseris u left join users us on u.users_id = us.id where u.user_role_index = 4", nativeQuery = true)
    List<UserTesseris> findAllAdmin();


        /**
     * 채팅용 관리자 리스트 조회
     * adminName: user_index -> user_tesseris -> users -> name
     * adminUserIndex: admin.user_index
     * adminTypeName: admin_type_index -> admin_type.admin_type_name
     * adminRankName: admin.admin_rank_name
     */
    @Query("SELECT new com.jakdang.labs.api.chat.dto.ChatAdminListResponseDto(" +
           "COALESCE(u.name, ''), " +
           "ut.userIndex, " +
           "COALESCE(at.adminTypeName, ''), " +
           "COALESCE(a.adminRankName, '')) " +
           "FROM Admin a " +
           "LEFT JOIN a.userIndex ut " +
           "LEFT JOIN ut.usersId u " +
           "LEFT JOIN a.adminTypeIndex at " +
           "ORDER BY a.adminRegistrationDate DESC")
    List<ChatAdminListResponseDto> findAllChatAdminList();
}
