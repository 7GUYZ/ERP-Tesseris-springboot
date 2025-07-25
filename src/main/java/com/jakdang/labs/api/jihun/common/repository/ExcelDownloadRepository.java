package com.jakdang.labs.api.jihun.common.repository;

import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserTesseris;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 엑셀 다운로드 성능 최적화를 위한 Repository
 * 
 * 주요 최적화 포인트:
 * 1. JOIN FETCH를 사용하여 N+1 문제 해결
 * 2. 필요한 컬럼만 선택하여 메모리 사용량 최소화
 * 3. 인덱스를 활용한 효율적인 쿼리 실행
 * 4. 대용량 데이터 처리에 최적화된 페이징
 * 5. 기존 memberaccount와 memberassetdetails 쿼리 참고하여 최적화
 */
@Repository
public interface ExcelDownloadRepository extends JpaRepository<UserCmLog, Long> {

    /**
     * 회원 자산 내역 엑셀 다운로드용 최적화 쿼리
     * 
     * 참고: memberaccount/AjhUserCmLogRepository의 동적 검색 쿼리 기반
     * 성능 최적화 내용:
     * - LEFT JOIN으로 연관 엔티티를 한 번에 로딩 (N+1 문제 해결)
     * - 필요한 컬럼만 선택하여 메모리 사용량 최소화
     * - 인덱스 활용을 위한 ORDER BY 추가
     * - 대용량 데이터 처리를 위한 효율적인 페이징
     * - COALESCE로 null 값 안전 처리
     * 
     * @param pageable 페이징 정보
     * @return 최적화된 회원 자산 내역 데이터
     */
    @Query(value = """
        SELECT DISTINCT 
            ucl.user_cm_log_index,
            ucl.user_cm_log_value,
            ucl.user_cm_log_reason,
            ucl.user_cm_log_create_time,
            ucl.user_coupon_value,
            utt.transaction_type_name,
            -- 이벤트 트리거 사용자 정보 (LEFT JOIN으로 안전하게 처리)
            CASE 
                WHEN etu_users.email IS NOT NULL AND etu_users.email != '' THEN etu_users.email
                ELSE CONCAT(SUBSTRING(etu_users.id, 1, 8), '@example.com')
            END as event_trigger_user_email,
            COALESCE(etr.user_role_kor_nm, '') as event_trigger_user_role,
            -- 이벤트 파티 사용자 정보 (LEFT JOIN으로 안전하게 처리)
            CASE 
                WHEN epu_users.email IS NOT NULL AND epu_users.email != '' THEN epu_users.email
                ELSE CONCAT(SUBSTRING(epu_users.id, 1, 8), '@example.com')
            END as event_party_user_email,
            COALESCE(epu_users.name, '') as event_party_user_name,
            COALESCE(epr.user_role_kor_nm, '') as event_party_user_role
        FROM user_cm_log ucl
        LEFT JOIN user_tesseris etu ON ucl.user_index_event_trigger = etu.user_index
        LEFT JOIN users etu_users ON etu.users_id = etu_users.id
        LEFT JOIN user_role etr ON etu.user_role_index = etr.user_role_index
        LEFT JOIN user_tesseris epu ON ucl.user_index_event_party = epu.user_index
        LEFT JOIN users epu_users ON epu.users_id = epu_users.id
        LEFT JOIN user_role epr ON epu.user_role_index = epr.user_role_index
        LEFT JOIN user_cm_log_transaction_type utt ON ucl.user_cm_log_transaction_type_index = utt.user_cm_log_transaction_type_index
        ORDER BY ucl.user_cm_log_index DESC
        """, 
        countQuery = "SELECT COUNT(DISTINCT ucl.user_cm_log_index) FROM user_cm_log ucl",
        nativeQuery = true)
    Page<Object[]> findMemberAccountExcelDataOptimized(Pageable pageable);

    /**
     * 회원 자산 현황 엑셀 다운로드용 최적화 쿼리
     * 
     * 참고: memberassetdetails/AjgMemberAssetDetailsRepository의 findMemberAssetDetails 쿼리 기반
     * 성능 최적화 내용:
     * - 필요한 컬럼만 선택하여 메모리 사용량 최소화
     * - 인덱스 활용을 위한 ORDER BY 추가
     * - 대용량 데이터 처리를 위한 효율적인 페이징
     * - LEFT JOIN으로 연관 데이터 안전하게 조회
     * 
     * @param pageable 페이징 정보
     * @return 최적화된 회원 자산 현황 데이터
     */
    @Query(value = """
        SELECT
            u.user_index,
            us.id as users_id,
            r.user_role_kor_nm as user_role_kor_nm,
            us.name as user_name,
            us.phone as user_phone,
            s.store_name as store_name,
            (c.user_cm_deposit - c.user_cm_withdrawal) as user_cm_current,
            (c.user_cmp_deposit - c.user_cmp_withdrawal) as user_cmp_current,
            (c.user_cash_deposit - c.user_cash_withdrawal) as user_cash_current
        FROM user_tesseris u
        LEFT JOIN users us ON u.users_id = us.id
        LEFT JOIN user_cm c ON u.user_index = c.user_cm_index
        LEFT JOIN user_role r ON u.user_role_index = r.user_role_index
        LEFT JOIN store s ON u.user_index = s.user_index
        ORDER BY u.user_index DESC
        """,
        countQuery = "SELECT COUNT(*) FROM user_tesseris",
        nativeQuery = true)
    Page<Object[]> findMemberAssetDetailsExcelDataOptimized(Pageable pageable);
} 