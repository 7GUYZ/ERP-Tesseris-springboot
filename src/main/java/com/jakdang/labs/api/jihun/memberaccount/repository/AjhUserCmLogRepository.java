package com.jakdang.labs.api.jihun.memberaccount.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.jakdang.labs.entity.UserCmLog;

import java.util.List;

/**
 * UserCmLog 엔티티에 대한 데이터베이스 접근을 담당하는 Repository
 * 
 * 주요 기능:
 * 1. 무한 스크롤을 위한 페이징 처리
 * 2. N+1 문제 해결을 위한 JOIN FETCH 사용
 * 3. 사용자별 필터링
 * 4. 성능 최적화를 위한 DISTINCT 사용
 */
@Repository
public interface AjhUserCmLogRepository extends JpaRepository<UserCmLog, Long> {

    /**
     * 상위 100개 조회 (기존 유지)
     * 
     * 목적: 초기 페이지 로드 시 빠른 응답을 위해 제한된 데이터만 조회
     * 
     * 특징:
     * - LEFT JOIN FETCH: N+1 문제 방지 (연관 엔티티를 한 번에 조회)
     * - DISTINCT: 중복 데이터 제거 (JOIN으로 인한 중복 방지)
     * - ORDER BY DESC: 최신 데이터부터 정렬
     * - Pageable: 페이징 정보 (size=100으로 제한)
     * 
     * 사용 시나리오: 앱 초기 로드, 빠른 데이터 표시가 필요한 경우
     */
    @Query("""
            SELECT DISTINCT ucl FROM UserCmLog ucl
            LEFT JOIN FETCH ucl.userIndexEventTrigger etu
            LEFT JOIN FETCH ucl.userIndexEventParty epu
            ORDER BY ucl.userCmLogCreateTime DESC
            """)
    List<UserCmLog> findTop100WithJoins(Pageable pageable);

    /**
     * 🆕 페이징 처리된 전체 데이터 조회 (무한 스크롤용)
     * 
     * 목적: 무한 스크롤 구현을 위한 메인 쿼리
     * 
     * 특징:
     * - Page<UserCmLog> 반환: 총 개수, 페이지 정보 등 메타데이터 포함
     * - 무한 스크롤에서 "더 보기" 버튼 클릭 시 호출
     * - 메모리 효율적: 한 번에 제한된 개수만 로드
     * - 성능 최적화: 필요한 데이터만 조회
     * 
     * 사용 시나리오: 무한 스크롤, 대용량 데이터 처리
     */
    @Query("""
            SELECT DISTINCT ucl FROM UserCmLog ucl
            LEFT JOIN FETCH ucl.userIndexEventTrigger etu
            LEFT JOIN FETCH ucl.userIndexEventParty epu
            ORDER BY ucl.userCmLogCreateTime DESC
            """)
    Page<UserCmLog> findAllWithJoinsPaged(Pageable pageable);

    /**
     * 전체 데이터 조회 (기존 - 위험할 수 있음)
     * 
     * ⚠️ 주의: 대용량 데이터가 있을 경우 메모리 부족 위험
     * 
     * 목적: 모든 데이터를 한 번에 조회 (테스트, 관리자용)
     * 
     * 특징:
     * - List<UserCmLog> 반환: 모든 데이터를 메모리에 로드
     * - 성능 이슈 가능성: 데이터가 많을 경우
     * - JOIN FETCH로 N+1 문제는 해결
     * 
     * 사용 시나리오: 데이터 분석, 관리자 페이지, 소량 데이터
     */
    @Query("""
            SELECT DISTINCT ucl FROM UserCmLog ucl
            LEFT JOIN FETCH ucl.userIndexEventTrigger etu
            LEFT JOIN FETCH ucl.userIndexEventParty epu
            ORDER BY ucl.userCmLogCreateTime DESC
            """)
    List<UserCmLog> findAllWithJoins();

    /**
     * 특정 ID의 UserCmLog 조회 (상세 정보용)
     * 
     * 목적: 개별 로그의 상세 정보 조회
     * 
     * 특징:
     * - WHERE 절로 특정 ID 필터링
     * - JOIN FETCH로 연관 데이터도 함께 조회
     * - 단일 결과 반환
     * 
     * 사용 시나리오: 로그 상세 보기, 모달 창 표시
     */
    @Query("""
            SELECT ucl FROM UserCmLog ucl
            LEFT JOIN FETCH ucl.userIndexEventTrigger etu
            LEFT JOIN FETCH ucl.userIndexEventParty epu
            WHERE ucl.userCmLogIndex = :id
            """)
    UserCmLog findByIdWithJoins(@Param("id") Long id);

    /**
     * 상위 100개 사용자별 조회 (기존 유지)
     * 
     * 목적: 특정 사용자와 관련된 로그만 조회 (초기 로드용)
     * 
     * 특징:
     * - WHERE 절: eventTriggerUser OR eventPartyUser 조건
     * - 사용자가 주체이거나 상대방인 모든 로그 조회
     * - 페이징으로 성능 제어
     * 
     * 사용 시나리오: 사용자별 로그 페이지 초기 로드
     */
    @Query("""
            SELECT DISTINCT ucl FROM UserCmLog ucl
            LEFT JOIN FETCH ucl.userIndexEventTrigger etu
            LEFT JOIN FETCH ucl.userIndexEventParty epu
            WHERE ucl.userIndexEventTrigger = :userId OR ucl.userIndexEventParty = :userId
            ORDER BY ucl.userCmLogCreateTime DESC
            """)
    List<UserCmLog> findTop100ByUserIndexWithJoins(@Param("userId") Long userId, Pageable pageable);

    /**
     * 🆕 페이징 처리된 사용자별 데이터 조회
     * 
     * 목적: 사용자별 무한 스크롤 구현
     * 
     * 특징:
     * - Page<UserCmLog> 반환: 페이징 메타데이터 포함
     * - 사용자 필터링 + 페이징 조합
     * - 무한 스크롤에서 사용자별 데이터 로드
     * 
     * 사용 시나리오: 사용자별 로그 페이지의 무한 스크롤
     */
    @Query("""
            SELECT DISTINCT ucl FROM UserCmLog ucl
            LEFT JOIN FETCH ucl.userIndexEventTrigger etu
            LEFT JOIN FETCH ucl.userIndexEventParty epu
            WHERE ucl.userIndexEventTrigger = :userId OR ucl.userIndexEventParty = :userId
            ORDER BY ucl.userCmLogCreateTime DESC
            """)
    Page<UserCmLog> findByUserIndexWithJoinsPaged(@Param("userId") Long userId, Pageable pageable);

    /**
     * 전체 사용자별 조회 (기존)
     * 
     * ⚠️ 주의: 사용자별 데이터가 많을 경우 메모리 부족 위험
     * 
     * 목적: 특정 사용자의 모든 로그 조회
     * 
     * 특징:
     * - List<UserCmLog> 반환: 모든 데이터를 메모리에 로드
     * - 사용자 필터링만 적용
     * - 페이징 없음
     * 
     * 사용 시나리오: 사용자별 전체 로그 분석, 데이터 내보내기
     */
    @Query("""
            SELECT DISTINCT ucl FROM UserCmLog ucl
            LEFT JOIN FETCH ucl.userIndexEventTrigger etu
            LEFT JOIN FETCH ucl.userIndexEventParty epu
            WHERE ucl.userIndexEventTrigger = :userId OR ucl.userIndexEventParty = :userId
            ORDER BY ucl.userCmLogCreateTime DESC
            """)
    List<UserCmLog> findAllByUserIndexWithJoins(@Param("userId") Long userId);

    /**
     * 🆕 동적 검색 쿼리 (PHP 검색 기능 재현)
     * 
     * 목적: 복합 조건을 이용한 동적 검색 기능
     * 
     * 특징:
     * - 다중 조건 검색 (사용자, 역할, 유형, 날짜 등)
     * - LIKE 검색으로 부분 일치 지원
     * - NULL/빈 값 처리
     * - 페이징 지원
     * - nativeQuery = true: MySQL 전용 함수 사용을 위해 네이티브 쿼리 사용
     * 
     * 사용 시나리오: 관리자 검색, 상세 필터링
     */
    @Query(value = """
            SELECT DISTINCT ucl.* FROM user_cm_log ucl
            INNER JOIN user_tesseris etu ON ucl.user_index_event_trigger = etu.user_index
            LEFT JOIN user_role etuRole ON etu.user_role_index = etuRole.user_role_index
            INNER JOIN user_tesseris epu ON ucl.user_index_event_party = epu.user_index
            LEFT JOIN user_role epuRole ON epu.user_role_index = epuRole.user_role_index
            LEFT JOIN user_cm_log_value_type vt ON ucl.user_cm_log_value_type_index = vt.user_cm_log_value_type_index
            LEFT JOIN user_cm_log_payment p ON ucl.user_cm_log_payment_index = p.user_cm_log_payment_index
            LEFT JOIN user_cm_log_transaction_type tt ON ucl.user_cm_log_transaction_type_index = tt.user_cm_log_transaction_type_index
            LEFT JOIN users etu_users ON etu.users_id = etu_users.id
            LEFT JOIN users epu_users ON epu.users_id = epu_users.id
            WHERE (:triggerUserId IS NULL OR :triggerUserId = '' OR etu_users.id LIKE CONCAT('%', :triggerUserId, '%'))
              AND (:partyUserId IS NULL OR :partyUserId = '' OR epu_users.id LIKE CONCAT('%', :partyUserId, '%'))
              AND (:partyRoleIndex IS NULL OR :partyRoleIndex = 0 OR epu.user_role_index = :partyRoleIndex)
              AND (:triggerRoleIndex IS NULL OR :triggerRoleIndex = 0 OR etu.user_role_index = :triggerRoleIndex)
              AND (:valueTypeIndex IS NULL OR :valueTypeIndex = 0 OR ucl.user_cm_log_value_type_index = :valueTypeIndex)
              AND (:startDate IS NULL OR :startDate = '' OR ucl.user_cm_log_create_time >= STR_TO_DATE(CONCAT(:startDate, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'))
              AND (:endDate IS NULL OR :endDate = '' OR ucl.user_cm_log_create_time <= STR_TO_DATE(CONCAT(:endDate, ' 23:59:59'), '%Y-%m-%d %H:%i:%s'))
              AND (:paymentIndex IS NULL OR :paymentIndex = 0 OR ucl.user_cm_log_payment_index = :paymentIndex)
              AND (:transactionTypeIndex IS NULL OR :transactionTypeIndex = 0 OR ucl.user_cm_log_transaction_type_index = :transactionTypeIndex)
            ORDER BY ucl.user_cm_log_create_time DESC
            """, countQuery = """
            SELECT COUNT(DISTINCT ucl.user_cm_log_index) FROM user_cm_log ucl
            INNER JOIN user_tesseris etu ON ucl.user_index_event_trigger = etu.user_index
            INNER JOIN user_tesseris epu ON ucl.user_index_event_party = epu.user_index
            LEFT JOIN users etu_users ON etu.users_id = etu_users.id
            LEFT JOIN users epu_users ON epu.users_id = epu_users.id
            WHERE (:triggerUserId IS NULL OR :triggerUserId = '' OR etu_users.id LIKE CONCAT('%', :triggerUserId, '%'))
              AND (:partyUserId IS NULL OR :partyUserId = '' OR epu_users.id LIKE CONCAT('%', :partyUserId, '%'))
              AND (:partyRoleIndex IS NULL OR :partyRoleIndex = 0 OR epu.user_role_index = :partyRoleIndex)
              AND (:triggerRoleIndex IS NULL OR :triggerRoleIndex = 0 OR etu.user_role_index = :triggerRoleIndex)
              AND (:valueTypeIndex IS NULL OR :valueTypeIndex = 0 OR ucl.user_cm_log_value_type_index = :valueTypeIndex)
              AND (:startDate IS NULL OR :startDate = '' OR ucl.user_cm_log_create_time >= STR_TO_DATE(CONCAT(:startDate, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'))
              AND (:endDate IS NULL OR :endDate = '' OR ucl.user_cm_log_create_time <= STR_TO_DATE(CONCAT(:endDate, ' 23:59:59'), '%Y-%m-%d %H:%i:%s'))
              AND (:paymentIndex IS NULL OR :paymentIndex = 0 OR ucl.user_cm_log_payment_index = :paymentIndex)
              AND (:transactionTypeIndex IS NULL OR :transactionTypeIndex = 0 OR ucl.user_cm_log_transaction_type_index = :transactionTypeIndex)
            """, nativeQuery = true)
    Page<UserCmLog> findBySearchCriteria(
            @Param("triggerUserId") String triggerUserId,
            @Param("partyUserId") String partyUserId,
            @Param("partyRoleIndex") Long partyRoleIndex,
            @Param("triggerRoleIndex") Long triggerRoleIndex,
            @Param("valueTypeIndex") Long valueTypeIndex,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("paymentIndex") Long paymentIndex,
            @Param("transactionTypeIndex") Long transactionTypeIndex,
            Pageable pageable);

    /**
     * 🆕 강화된 LIKE 검색 지원 동적 쿼리
     * 
     * 목적: 프론트엔드에서 전달받은 파라미터로 LIKE 검색 수행
     * 
     * 특징:
     * - 이메일과 이름 모두에서 LIKE 검색 지원
     * - OR 조건으로 이메일 또는 이름 중 하나라도 매치되면 결과에 포함
     * - 프론트엔드 파라미터와 완전히 호환
     * - 동적 조건 처리 (null/빈 값 무시)
     * 
     * 사용 시나리오: 프론트엔드에서 사용자 ID 또는 이름으로 검색
     */
    @Query(value = """
            SELECT DISTINCT ucl.* FROM user_cm_log ucl
            INNER JOIN user_tesseris etu ON ucl.user_index_event_trigger = etu.user_index
            LEFT JOIN user_role etuRole ON etu.user_role_index = etuRole.user_role_index
            INNER JOIN user_tesseris epu ON ucl.user_index_event_party = epu.user_index
            LEFT JOIN user_role epuRole ON epu.user_role_index = epuRole.user_role_index
            LEFT JOIN user_cm_log_value_type vt ON ucl.user_cm_log_value_type_index = vt.user_cm_log_value_type_index
            LEFT JOIN user_cm_log_payment p ON ucl.user_cm_log_payment_index = p.user_cm_log_payment_index
            LEFT JOIN user_cm_log_transaction_type tt ON ucl.user_cm_log_transaction_type_index = tt.user_cm_log_transaction_type_index
            LEFT JOIN users etu_users ON etu.users_id = etu_users.id
            LEFT JOIN users epu_users ON epu.users_id = epu_users.id
            WHERE (:triggerUserEmail IS NULL OR :triggerUserEmail = '' OR 
                   etu_users.email LIKE CONCAT('%', :triggerUserEmail, '%') OR 
                   etu_users.id LIKE CONCAT('%', :triggerUserEmail, '%'))
              AND (:partyUserEmail IS NULL OR :partyUserEmail = '' OR 
                   epu_users.email LIKE CONCAT('%', :partyUserEmail, '%') OR 
                   epu_users.id LIKE CONCAT('%', :partyUserEmail, '%'))
              AND (:partyUserName IS NULL OR :partyUserName = '' OR 
                   epu_users.name LIKE CONCAT('%', :partyUserName, '%'))
              AND (:triggerRoleIndex IS NULL OR :triggerRoleIndex = 0 OR etu.user_role_index = :triggerRoleIndex)
              AND (:partyRoleIndex IS NULL OR :partyRoleIndex = 0 OR epu.user_role_index = :partyRoleIndex)
              AND (:valueTypeIndex IS NULL OR :valueTypeIndex = 0 OR ucl.user_cm_log_value_type_index = :valueTypeIndex)
              AND (:startDate IS NULL OR :startDate = '' OR ucl.user_cm_log_create_time >= STR_TO_DATE(CONCAT(:startDate, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'))
              AND (:endDate IS NULL OR :endDate = '' OR ucl.user_cm_log_create_time <= STR_TO_DATE(CONCAT(:endDate, ' 23:59:59'), '%Y-%m-%d %H:%i:%s'))
              AND (:paymentIndex IS NULL OR :paymentIndex = 0 OR ucl.user_cm_log_payment_index = :paymentIndex)
              AND (:transactionTypeIndex IS NULL OR :transactionTypeIndex = 0 OR ucl.user_cm_log_transaction_type_index = :transactionTypeIndex)
            ORDER BY ucl.user_cm_log_create_time DESC
            """, countQuery = """
            SELECT COUNT(DISTINCT ucl.user_cm_log_index) FROM user_cm_log ucl
            INNER JOIN user_tesseris etu ON ucl.user_index_event_trigger = etu.user_index
            INNER JOIN user_tesseris epu ON ucl.user_index_event_party = epu.user_index
            LEFT JOIN users etu_users ON etu.users_id = etu_users.id
            LEFT JOIN users epu_users ON epu.users_id = epu_users.id
            WHERE (:triggerUserEmail IS NULL OR :triggerUserEmail = '' OR 
                   etu_users.email LIKE CONCAT('%', :triggerUserEmail, '%') OR 
                   etu_users.id LIKE CONCAT('%', :triggerUserEmail, '%'))
              AND (:partyUserEmail IS NULL OR :partyUserEmail = '' OR 
                   epu_users.email LIKE CONCAT('%', :partyUserEmail, '%') OR 
                   epu_users.id LIKE CONCAT('%', :partyUserEmail, '%'))
              AND (:partyUserName IS NULL OR :partyUserName = '' OR 
                   epu_users.name LIKE CONCAT('%', :partyUserName, '%'))
              AND (:triggerRoleIndex IS NULL OR :triggerRoleIndex = 0 OR etu.user_role_index = :triggerRoleIndex)
              AND (:partyRoleIndex IS NULL OR :partyRoleIndex = 0 OR epu.user_role_index = :partyRoleIndex)
              AND (:valueTypeIndex IS NULL OR :valueTypeIndex = 0 OR ucl.user_cm_log_value_type_index = :valueTypeIndex)
              AND (:startDate IS NULL OR :startDate = '' OR ucl.user_cm_log_create_time >= STR_TO_DATE(CONCAT(:startDate, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'))
              AND (:endDate IS NULL OR :endDate = '' OR ucl.user_cm_log_create_time <= STR_TO_DATE(CONCAT(:endDate, ' 23:59:59'), '%Y-%m-%d %H:%i:%s'))
              AND (:paymentIndex IS NULL OR :paymentIndex = 0 OR ucl.user_cm_log_payment_index = :paymentIndex)
              AND (:transactionTypeIndex IS NULL OR :transactionTypeIndex = 0 OR ucl.user_cm_log_transaction_type_index = :transactionTypeIndex)
            """, nativeQuery = true)
    Page<UserCmLog> findBySearchCriteriaWithLike(
            @Param("triggerUserEmail") String triggerUserEmail,
            @Param("partyUserEmail") String partyUserEmail,
            @Param("partyUserName") String partyUserName,
            @Param("triggerRoleIndex") Long triggerRoleIndex,
            @Param("partyRoleIndex") Long partyRoleIndex,
            @Param("valueTypeIndex") Long valueTypeIndex,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("paymentIndex") Long paymentIndex,
            @Param("transactionTypeIndex") Long transactionTypeIndex,
            Pageable pageable);

    /**
     * 🆕 페이징 없는 동적 검색 쿼리 (클라이언트 사이드 pagination용)
     * 
     * 목적: 클라이언트 사이드 pagination을 위해 모든 데이터를 한 번에 조회
     * 
     * 특징:
     * - 페이징 없이 모든 데이터 조회
     * - 클라이언트에서 pagination 처리
     * - DataGrid의 내부 상태 관리 안정화
     * 
     * 사용 시나리오: 클라이언트 사이드 pagination이 필요한 경우
     */
    @Query(value = """
            SELECT DISTINCT ucl.* FROM user_cm_log ucl
            INNER JOIN user_tesseris etu ON ucl.user_index_event_trigger = etu.user_index
            LEFT JOIN user_role etuRole ON etu.user_role_index = etuRole.user_role_index
            INNER JOIN user_tesseris epu ON ucl.user_index_event_party = epu.user_index
            LEFT JOIN user_role epuRole ON epu.user_role_index = epuRole.user_role_index
            LEFT JOIN user_cm_log_value_type vt ON ucl.user_cm_log_value_type_index = vt.user_cm_log_value_type_index
            LEFT JOIN user_cm_log_payment p ON ucl.user_cm_log_payment_index = p.user_cm_log_payment_index
            LEFT JOIN user_cm_log_transaction_type tt ON ucl.user_cm_log_transaction_type_index = tt.user_cm_log_transaction_type_index
            LEFT JOIN users etu_users ON etu.users_id = etu_users.id
            LEFT JOIN users epu_users ON epu.users_id = epu_users.id
            WHERE (:triggerUserEmail IS NULL OR :triggerUserEmail = '' OR 
                   etu_users.email LIKE CONCAT('%', :triggerUserEmail, '%') OR 
                   etu_users.id LIKE CONCAT('%', :triggerUserEmail, '%'))
              AND (:partyUserEmail IS NULL OR :partyUserEmail = '' OR 
                   epu_users.email LIKE CONCAT('%', :partyUserEmail, '%') OR 
                   epu_users.id LIKE CONCAT('%', :partyUserEmail, '%'))
              AND (:partyUserName IS NULL OR :partyUserName = '' OR 
                   epu_users.name LIKE CONCAT('%', :partyUserName, '%'))
              AND (:triggerRoleIndex IS NULL OR :triggerRoleIndex = 0 OR etu.user_role_index = :triggerRoleIndex)
              AND (:partyRoleIndex IS NULL OR :partyRoleIndex = 0 OR epu.user_role_index = :partyRoleIndex)
              AND (:valueTypeIndex IS NULL OR :valueTypeIndex = 0 OR ucl.user_cm_log_value_type_index = :valueTypeIndex)
              AND (:startDate IS NULL OR :startDate = '' OR ucl.user_cm_log_create_time >= STR_TO_DATE(CONCAT(:startDate, ' 00:00:00'), '%Y-%m-%d %H:%i:%s'))
              AND (:endDate IS NULL OR :endDate = '' OR ucl.user_cm_log_create_time <= STR_TO_DATE(CONCAT(:endDate, ' 23:59:59'), '%Y-%m-%d %H:%i:%s'))
              AND (:paymentIndex IS NULL OR :paymentIndex = 0 OR ucl.user_cm_log_payment_index = :paymentIndex)
              AND (:transactionTypeIndex IS NULL OR :transactionTypeIndex = 0 OR ucl.user_cm_log_transaction_type_index = :transactionTypeIndex)
            ORDER BY ucl.user_cm_log_create_time DESC
            """, nativeQuery = true)
    List<UserCmLog> findBySearchCriteriaWithLikeNoPaging(
            @Param("triggerUserEmail") String triggerUserEmail,
            @Param("partyUserEmail") String partyUserEmail,
            @Param("partyUserName") String partyUserName,
            @Param("triggerRoleIndex") Long triggerRoleIndex,
            @Param("partyRoleIndex") Long partyRoleIndex,
            @Param("valueTypeIndex") Long valueTypeIndex,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("paymentIndex") Long paymentIndex,
            @Param("transactionTypeIndex") Long transactionTypeIndex);
}