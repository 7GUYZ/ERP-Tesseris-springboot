package com.jakdang.labs.api.jihun.memberaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * UserCmLog 응답용 DTO (Data Transfer Object)
 * 
 * 목적: 
 * 1. 프론트엔드로 전송할 데이터 구조 정의
 * 2. Entity의 복잡한 연관 관계를 평면화
 * 3. 순환 참조 문제 해결
 * 4. API 응답 형식 표준화
 * 
 * 설계 원칙:
 * - 불변성 보장 (final 필드 사용 권장)
 * - 명확한 네이밍
 * - null 안전 처리
 * - 프론트엔드 요구사항 반영
 */
@Data // Lombok: getter, setter, equals, hashCode, toString 자동 생성
@NoArgsConstructor // Lombok: 기본 생성자 자동 생성
@AllArgsConstructor // Lombok: 모든 필드를 매개변수로 받는 생성자 자동 생성
@Builder // Lombok: 빌더 패턴 자동 생성 (체이닝 방식으로 객체 생성 가능)
public class UserCmLogResponseDto {

    // ========== 기본 UserCmLog 정보 ==========

    /**
     * UserCmLog의 고유 식별자
     * 
     * 목적: 프론트엔드에서 개별 로그를 식별하기 위한 고유 ID
     * 
     * 특징:
     * - 데이터베이스의 Primary Key 값
     * - 상세 보기, 수정, 삭제 등의 작업에서 사용
     * - null이 아닌 고유한 값
     */
    private Long userCmLogIndex;

    /**
     * CM 거래 금액
     * 
     * 목적: 실제 거래된 CM의 양을 표시
     * 
     * 특징:
     * - BigDecimal 사용: 정확한 금액 계산
     * - 양수: 적립, 음수: 사용
     * - 프론트엔드에서 금액 표시 및 계산에 사용
     * - null 허용 (금액이 없는 거래인 경우)
     */
    private BigDecimal userCmLogValue;

    /**
     * 쿠폰 금액
     * 
     * 목적: 쿠폰과 관련된 추가 금액 정보 표시
     * 
     * 특징:
     * - CM 거래와 별도로 쿠폰 정보 관리
     * - 프론트엔드에서 쿠폰 정보 표시에 사용
     * - null 허용 (쿠폰이 없는 거래인 경우)
     */
    private BigDecimal userCouponValue;

    /**
     * CM 거래 사유
     * 
     * 목적: 거래가 발생한 이유를 표시
     * 
     * 특징:
     * - 텍스트 형태로 거래 사유 저장
     * - 예: "선물", "구매", "환불", "보상" 등
     * - 프론트엔드에서 거래 내역 표시에 사용
     * - null 허용 (사유가 없는 경우)
     */
    private String userCmLogReason;

    /**
     * CM 로그 생성 시간
     * 
     * 목적: 거래가 발생한 정확한 시간을 표시
     * 
     * 특징:
     * - String 사용: JSON 직렬화 문제 해결
     * - 프론트엔드에서 시간순 정렬 및 필터링에 사용
     * - 로그 표시 시 "몇 분 전", "몇 시간 전" 등으로 변환 가능
     * - null 허용 (시간 정보가 없는 경우)
     */
    private String userCmLogCreateTime;

    // ========== 이벤트 발생 사용자 정보 ==========

    /**
     * 이벤트 발생 사용자 인덱스
     * 
     * 목적: CM 거래를 발생시킨 사용자의 고유 ID
     * 
     * 특징:
     * - 예: 선물을 보낸 사용자, 시스템 관리자 등
     * - 프론트엔드에서 사용자 프로필 링크 생성에 사용
     * - null 허용 (시스템에 의한 자동 거래인 경우)
     */
    private Long eventTriggerUserIndex;

    /**
     * 이벤트 발생 사용자 이메일
     * 
     * 목적: 이벤트 발생 사용자의 이메일 주소
     * 
     * 특징:
     * - 사용자의 이메일 주소
     * - 예: "user@example.com", "admin@company.com" 등
     * - 프론트엔드에서 사용자 식별에 사용
     * - null 허용 (사용자 정보가 없는 경우)
     */
    private String eventTriggerUserEmail;

    /**
     * 이벤트 발생 사용자 이름
     * 
     * 목적: 이벤트 발생 사용자의 표시 이름
     * 
     * 특징:
     * - 사용자에게 보여지는 이름
     * - 예: "홍길동", "관리자" 등
     * - 프론트엔드에서 사용자명 표시에 사용
     * - null 허용 (이름 정보가 없는 경우)
     */
    private String eventTriggerUserName;

    /**
     * 이벤트 발생 사용자 역할
     * 
     * 목적: 이벤트 발생 사용자의 권한/역할 정보
     * 
     * 특징:
     * - 예: "일반사용자", "관리자", "VIP" 등
     * - 프론트엔드에서 역할별 아이콘 또는 배지 표시에 사용
     * - 권한 확인 및 UI 분기에 사용
     * - null 허용 (역할 정보가 없는 경우)
     */
    private String eventTriggerUserRole;

    // ========== 이벤트 대상 사용자 정보 ==========

    /**
     * 이벤트 대상 사용자 인덱스
     * 
     * 목적: CM 거래의 대상이 되는 사용자의 고유 ID
     * 
     * 특징:
     * - 예: 선물을 받은 사용자, 거래 상대방 등
     * - 프론트엔드에서 사용자 프로필 링크 생성에 사용
     * - null 허용 (시스템에 의한 자동 거래인 경우)
     */
    private Long eventPartyUserIndex;

    /**
     * 이벤트 대상 사용자 이메일
     * 
     * 목적: 이벤트 대상 사용자의 이메일 주소
     * 
     * 특징:
     * - 사용자의 이메일 주소
     * - 예: "user@example.com", "admin@company.com" 등
     * - 프론트엔드에서 사용자 식별에 사용
     * - null 허용 (사용자 정보가 없는 경우)
     */
    private String eventPartyUserEmail;

    /**
     * 이벤트 대상 사용자 이름
     * 
     * 목적: 이벤트 대상 사용자의 표시 이름
     * 
     * 특징:
     * - 사용자에게 보여지는 이름
     * - 프론트엔드에서 사용자명 표시에 사용
     * - null 허용 (이름 정보가 없는 경우)
     */
    private String eventPartyUserName;

    /**
     * 이벤트 대상 사용자 역할
     * 
     * 목적: 이벤트 대상 사용자의 권한/역할 정보
     * 
     * 특징:
     * - 프론트엔드에서 역할별 아이콘 또는 배지 표시에 사용
     * - 권한 확인 및 UI 분기에 사용
     * - null 허용 (역할 정보가 없는 경우)
     */
    private String eventPartyUserRole;

    // ========== CM 유형 정보 ==========

    /**
     * CM 유형 인덱스
     * 
     * 목적: CM의 종류를 구분하기 위한 고유 ID
     * 
     * 특징:
     * - 예: 캐시, 포인트, 쿠폰 등
     * - 프론트엔드에서 유형별 필터링에 사용
     * - null 허용 (유형이 정의되지 않은 경우)
     */
    private Long valueTypeIndex;

    /**
     * CM 유형 이름
     * 
     * 목적: CM의 종류를 사용자에게 표시
     * 
     * 특징:
     * - 예: "캐시", "포인트", "쿠폰" 등
     * - 프론트엔드에서 유형별 아이콘 또는 색상 표시에 사용
     * - null 허용 (유형 이름이 없는 경우)
     */
    private String valueTypeName;

    // ========== 결제 수단 정보 ==========

    /**
     * 결제 수단 인덱스
     * 
     * 목적: 결제 수단을 구분하기 위한 고유 ID
     * 
     * 특징:
     * - 예: 신용카드, 계좌이체, 포인트 등
     * - 프론트엔드에서 결제 수단별 필터링에 사용
     * - null 허용 (결제 수단이 없는 경우)
     */
    private Long paymentIndex;

    /**
     * 결제 수단 이름
     * 
     * 목적: 결제 수단을 사용자에게 표시
     * 
     * 특징:
     * - 예: "신용카드", "계좌이체", "포인트" 등
     * - 프론트엔드에서 결제 수단별 아이콘 표시에 사용
     * - null 허용 (결제 수단 이름이 없는 경우)
     */
    private String paymentName;

    // ========== 거래 유형 정보 ==========

    /**
     * 거래 유형 인덱스
     * 
     * 목적: 거래의 성격을 구분하기 위한 고유 ID
     * 
     * 특징:
     * - 예: 적립, 사용, 환불, 선물 등
     * - 프론트엔드에서 거래 유형별 필터링에 사용
     * - null 허용 (거래 유형이 정의되지 않은 경우)
     */
    private Long transactionTypeIndex;

    /**
     * 거래 유형 이름
     * 
     * 목적: 거래의 성격을 사용자에게 표시
     * 
     * 특징:
     * - 예: "적립", "사용", "환불", "선물" 등
     * - 프론트엔드에서 거래 유형별 아이콘 또는 색상 표시에 사용
     * - 거래 내역 표시 시 중요한 정보
     * - null 허용 (거래 유형 이름이 없는 경우)
     */
    private String transactionTypeName;
} 