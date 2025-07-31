package com.jakdang.labs.api.taekjun.user_update.repository;

import com.jakdang.labs.api.taekjun.user_update.dto.UserInfoDto;
import com.jakdang.labs.api.taekjun.user_update.dto.UserUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserUpdateJtjRepo {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 사용자 인덱스로 사용자 정보를 조회합니다.
     */
    public UserInfoDto findUserInfoByUserIndex(Long userIndex) {
        String sql = """
            SELECT 
                ue.name as user_name,
                ue.phone as user_phone,
                ue.email as user_email,
                ut.user_address,
                ut.user_detail_address,
                ut.user_zone_code as user_zip_code,
                ub.user_bank_name as user_bank_name,
                ut.user_bank_number as user_bank_number,
                ut.user_bank_holder as user_bank_holder
            FROM user_tesseris ut
            INNER JOIN users ue ON ut.users_id = ue.id
            LEFT JOIN user_bank ub ON ut.user_bank_index = ub.user_bank_index
            WHERE ut.user_index = ?
        """;

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> 
                UserInfoDto.builder()
                    .userName(rs.getString("user_name"))
                    .userPhone(rs.getString("user_phone"))
                    .userEmail(rs.getString("user_email"))
                    .userAddress(rs.getString("user_address"))
                    .userDetailAddress(rs.getString("user_detail_address"))
                    .userZipCode(rs.getString("user_zip_code"))
                    .userBankName(rs.getString("user_bank_name"))
                    .userBankNumber(rs.getString("user_bank_number"))
                    .userBankHolder(rs.getString("user_bank_holder"))
                    .build(), userIndex);
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 사용자 정보를 수정합니다.
     */
    public int updateUserInfo(Long userIndex, UserUpdateRequestDto requestDto) {
        // 1. users 테이블 업데이트
        String usersSql = """
            UPDATE users ue
            INNER JOIN user_tesseris ut ON ue.id = ut.users_id
            SET 
                ue.name = ?,
                ue.phone = ?,
                ue.email = ?
            WHERE ut.user_index = ?
        """;

        int usersUpdated = jdbcTemplate.update(usersSql,
            requestDto.getUserName(),
            requestDto.getUserPhone(),
            requestDto.getUserEmail(),
            userIndex);

        // 2. user_tesseris 테이블 업데이트 (주소 정보)
        String tesserisSql = """
            UPDATE user_tesseris 
            SET 
                user_address = ?,
                user_detail_address = ?,
                user_zone_code = ?
            WHERE user_index = ?
        """;

        int tesserisUpdated = jdbcTemplate.update(tesserisSql,
            requestDto.getUserAddress(),
            requestDto.getUserDetailAddress(),
            requestDto.getUserZipCode(),
            userIndex);

        // 3. 계좌번호 정보 업데이트
        int bankUpdated = 0;
        if (requestDto.getUserBankNumber() != null && !requestDto.getUserBankNumber().trim().isEmpty()) {
            String bankSql = """
                UPDATE user_tesseris 
                SET 
                    user_bank_number = ?,
                    user_bank_holder = ?
                WHERE user_index = ?
            """;

            bankUpdated = jdbcTemplate.update(bankSql,
                requestDto.getUserBankNumber(),
                requestDto.getUserBankHolder(),
                userIndex);
        }

        log.info("사용자 정보 수정 결과 - usersUpdated: {}, tesserisUpdated: {}, bankUpdated: {}", 
            usersUpdated, tesserisUpdated, bankUpdated);

        return usersUpdated + tesserisUpdated + bankUpdated;
    }

    /**
     * 은행 목록을 조회합니다.
     */
    public List<Map<String, Object>> getBankList() {
        String sql = """
            SELECT 
                user_bank_index,
                user_bank_name
            FROM user_bank
            ORDER BY user_bank_index
        """;

        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("은행 목록 조회 중 오류: {}", e.getMessage());
            return List.of();
        }
    }
} 