package com.jakdang.labs.api.taekjun.user_update.repository;

import com.jakdang.labs.api.taekjun.user_update.dto.UserInfoDto;
import com.jakdang.labs.api.taekjun.user_update.dto.UserUpdateRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserUpdateJtjRepo {

    private final JdbcTemplate jdbcTemplate;

    public UserUpdateJtjRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 사용자 인덱스로 사용자 정보를 조회합니다.
     */
    public UserInfoDto findUserInfoByUserIndex(Long userIndex) {
        String sql = """
            SELECT 
                COALESCE(ue.name, '') as user_name,
                COALESCE(ue.phone, '') as user_phone,
                COALESCE(ue.email, '') as user_email,
                COALESCE(ut.user_address, '') as user_address,
                COALESCE(ut.user_detail_address, '') as user_detail_address,
                COALESCE(ut.user_zone_code, '') as user_zip_code,
                ut.user_bank_index as user_bank_index,
                COALESCE(ut.user_bank_number, '') as user_bank_number,
                COALESCE(ut.user_bank_holder, '') as user_bank_holder
            FROM user_tesseris ut
            INNER JOIN users ue ON ut.users_id = ue.id
            WHERE ut.user_index = ?
        """;

        try {
            UserInfoDto result = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> 
                UserInfoDto.builder()
                    .userName(rs.getString("user_name"))
                    .userPhone(rs.getString("user_phone"))
                    .userEmail(rs.getString("user_email"))
                    .userAddress(rs.getString("user_address"))
                    .userDetailAddress(rs.getString("user_detail_address"))
                    .userZipCode(rs.getString("user_zip_code"))
                    .userBankIndex(rs.getLong("user_bank_index"))
                    .userBankNumber(rs.getString("user_bank_number"))
                    .userBankHolder(rs.getString("user_bank_holder"))
                    .build(), userIndex);
            
            // 이메일이 없으면 기본값 설정
            if (result != null && (result.getUserEmail() == null || result.getUserEmail().isEmpty())) {
                log.warn("사용자 {}의 이메일이 없습니다. 기본값을 설정합니다.", userIndex);
                result.setUserEmail("이메일을 입력해주세요");
            }
            
            // 은행 정보 로그
            if (result != null) {
                log.info("사용자 {} 은행 정보: bankIndex={}, bankNumber={}, bankHolder={}", 
                    userIndex, result.getUserBankIndex(), result.getUserBankNumber(), result.getUserBankHolder());
            }
            
            return result;
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
                    user_bank_index = ?,
                    user_bank_number = ?,
                    user_bank_holder = ?
                WHERE user_index = ?
            """;

            bankUpdated = jdbcTemplate.update(bankSql,
                requestDto.getUserBankIndex(),
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