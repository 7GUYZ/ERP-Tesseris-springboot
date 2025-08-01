package com.jakdang.labs.api.chat.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
<<<<<<< HEAD

import com.jakdang.labs.api.chat.dto.ChatAdminListResponseDto;
=======
import com.jakdang.labs.api.chat.dto.AdminListDTO;
>>>>>>> jihun
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.Admin;
import com.jakdang.labs.entity.adminType;

@Repository
public interface AjgChatServiceRepository extends JpaRepository<UserTesseris, Integer> {
<<<<<<< HEAD
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
=======
    @Query(value = "SELECT uts.user_index, uts.users_id, atp.admin_type_name, atp.admin_type_order, us.name FROM user_tesseris uts LEFT JOIN users us ON uts.users_id = us.id LEFT JOIN `admin` ad ON ad.user_index = uts.user_index LEFT JOIN admin_type atp ON ad.admin_type_index = atp.admin_type_index WHERE us.activated = 1 AND uts.user_role_index = 4", nativeQuery = true)
    List<AdminListDTO> findAdminList();
>>>>>>> jihun
}
