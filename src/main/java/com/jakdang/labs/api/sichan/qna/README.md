# QnA 시스템

## 개요
사용자와 관리자 간의 문의 및 답변을 처리하는 QnA 시스템입니다.

## 기능

### 사용자 페이지
- **문의 등록**: 사용자가 문의 제목과 내용을 입력하여 새로운 QnA를 등록
- **문의 내역 조회**: 사용자가 자신이 등록한 문의 목록을 조회
- **문의 상세 조회**: 특정 문의의 상세 내용과 답변을 조회

### 관리자 페이지
- **QnA 목록 조회**: 모든 QnA 목록을 조회 (검색 기능 포함)
- **QnA 상세 조회**: 특정 QnA의 상세 내용을 조회
- **답변 등록**: 관리자가 문의에 대한 답변을 등록
- **답변 대기/완료 분류**: 답변 상태에 따른 QnA 분류

## API 엔드포인트

### 사용자 API
- `POST /api/sichan/qna/inquiry` - 문의 등록
- `GET /api/sichan/qna/inquiry/list` - 문의 내역 조회
- `GET /api/sichan/qna/inquiry/{qnaIndex}` - 문의 상세 조회

### 관리자 API
- `GET /api/sichan/qna/admin/list` - QnA 목록 조회 (검색 가능)
- `GET /api/sichan/qna/admin/{qnaIndex}` - QnA 상세 조회
- `POST /api/sichan/qna/admin/{qnaIndex}/answer` - 답변 등록
- `GET /api/sichan/qna/admin/waiting` - 답변 대기 중인 QnA 목록
- `GET /api/sichan/qna/admin/completed` - 답변 완료된 QnA 목록

## 데이터베이스 구조

### Qna 엔티티
- `qnaIndex`: QnA 고유 번호 (Primary Key)
- `questionUser`: 문의자 (UserTesseris 엔티티와 연결)
- `questionTitle`: 문의 제목
- `questionDesc`: 문의 내용
- `answerUser`: 답변자 (UserTesseris 엔티티와 연결)
- `answerTitle`: 답변 제목
- `answerDesc`: 답변 내용
- `qnaCreateTime`: 문의 등록 시간
- `answerCreateTime`: 답변 등록 시간

## 프론트엔드 페이지

### 사용자 페이지
- `/sichan/qna/inquiry` - 문의 등록 페이지
- `/sichan/qna/list` - 문의 내역 페이지
- `/sichan/qna/detail/:qnaIndex` - 문의 상세 페이지

### 관리자 페이지
- `/admin/sichan/qna/list` - QnA 관리 목록 페이지
- `/admin/sichan/qna/detail/:qnaIndex` - QnA 상세 및 답변 페이지

## 사용 방법

### 사용자
1. 문의 등록: `/sichan/qna/inquiry` 페이지에서 제목과 내용을 입력하여 문의 등록
2. 내역 조회: `/sichan/qna/list` 페이지에서 자신의 문의 내역 확인
3. 상세 조회: 문의 목록에서 특정 문의를 클릭하여 상세 내용과 답변 확인

### 관리자
1. QnA 목록 조회: `/admin/sichan/qna/list` 페이지에서 모든 QnA 목록 확인
2. 검색 기능: 제목 또는 사용자명으로 QnA 검색 가능
3. 답변 등록: QnA 상세 페이지에서 답변 제목과 내용을 입력하여 답변 등록
4. 상태 확인: 답변 대기/완료 상태를 시각적으로 확인 가능

## 보안
- 모든 API는 JWT 토큰 인증이 필요합니다
- 사용자는 자신의 문의만 조회할 수 있습니다
- 관리자는 모든 QnA에 접근할 수 있습니다 