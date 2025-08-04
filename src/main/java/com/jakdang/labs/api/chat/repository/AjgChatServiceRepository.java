package com.jakdang.labs.api.chat.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.jakdang.labs.api.chat.dto.AdminListDTO;
import com.jakdang.labs.entity.UserTesseris;

@Repository
public interface AjgChatServiceRepository extends JpaRepository<UserTesseris, Integer> {
    @Query(value = "SELECT uts.user_index, uts.users_id, atp.admin_type_name, atp.admin_type_order, us.name FROM user_tesseris uts LEFT JOIN users us ON uts.users_id = us.id LEFT JOIN `admin` ad ON ad.user_index = uts.user_index LEFT JOIN admin_type atp ON ad.admin_type_index = atp.admin_type_index WHERE us.activated = 1 AND uts.user_role_index = 4", nativeQuery = true)
    List<AdminListDTO> findAdminList();
}
