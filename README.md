# 🚀 Tesseris ERP System

## 📋 프로젝트 소개

**Tesseris ERP System**은 인사관리 및 전자결제 시스템(ERP)을 제공하는 비즈니스 플랫폼입니다. 이 시스템은 기업의 효율적인 인사 관리와 전자 결제 업무를 원활하게 처리할 수 있도록 돕습니다. 사용자는 각종 인사 정보 관리와 전자 결제를 통해 비즈니스 운영을 더욱 자동화하고 효율적으로 관리할 수 있습니다.

## 🏗️ 프로젝트 구조

```
tesseris/
├── ERP-Tesseris-react-admin/     # 관리자용 React 애플리케이션
├── ERD-tesseris-springboot/      # Spring Boot 백엔드 서버
├── tesseris-react/               # 사용자용 React 애플리케이션
├── chat-server/                  # 실시간 채팅 서버
└── .vscode/                      # VS Code 설정
```

## 🛠️ 사용된 기술

### Front-End

#### React 애플리케이션 (ERP-Tesseris-react-admin, tesseris-react)
- **React 19.1.1**: 최신 React 버전으로 구축된 사용자 인터페이스
- **Material-UI (MUI)**: 구글의 Material Design 기반 UI 컴포넌트 라이브러리
  - `@mui/material`: 핵심 UI 컴포넌트
  - `@mui/icons-material`: 아이콘 라이브러리
  - `@mui/x-data-grid`: 데이터 그리드 컴포넌트
  - `@mui/x-date-pickers`: 날짜 선택 컴포넌트
  - `@mui/lab`: 실험적 컴포넌트들
- **Emotion**: CSS-in-JS 스타일링 솔루션
- **React Router DOM**: 클라이언트 사이드 라우팅
- **Axios**: HTTP 클라이언트 라이브러리
- **Zustand**: 상태 관리 라이브러리
- **React Hot Toast**: 토스트 알림 컴포넌트
- **Recharts**: 차트 및 데이터 시각화 라이브러리
- **GoJS**: 다이어그램 및 조직도 라이브러리
- **XLSX**: Excel 파일 처리 라이브러리
- **Day.js**: 날짜 처리 라이브러리
- **Date-fns**: 날짜 유틸리티 라이브러리
- **Lucide React**: 아이콘 라이브러리
- **Socket.io Client**: 실시간 통신 클라이언트
- **SockJS Client**: WebSocket 폴백 클라이언트
- **STOMP.js**: 메시징 프로토콜 클라이언트

#### 스타일링 & CSS
- **Tailwind CSS**: 유틸리티 퍼스트 CSS 프레임워크
- **PostCSS**: CSS 후처리기
- **Autoprefixer**: CSS 벤더 프리픽스 자동 추가

#### 개발 도구
- **ESLint**: JavaScript 코드 품질 관리
- **React Scripts**: Create React App 스크립트
- **Testing Library**: React 컴포넌트 테스팅

### Back-End

#### Spring Boot 서버 (ERD-tesseris-springboot)
- **Spring Boot 3.4.3**: Java 기반의 웹 애플리케이션 프레임워크
- **Java 17**: 최신 LTS Java 버전
- **Spring Security**: 인증 및 권한 관리, 비밀번호 암호화
- **Spring Data JPA**: 데이터 접근 계층
- **Spring WebSocket**: 실시간 통신 지원
- **Spring Kafka**: 메시징 시스템
- **Spring Cloud OpenFeign**: 마이크로서비스 간 통신
- **Spring Boot Mail**: 이메일 발송 기능
- **SpringDoc OpenAPI**: API 문서화 (Swagger)

#### 데이터베이스 & ORM
- **MySQL**: 관계형 데이터베이스 관리 시스템
- **MariaDB**: MySQL 호환 데이터베이스
- **MyBatis**: SQL 매퍼 프레임워크
- **QueryDSL**: 타입 안전한 쿼리 생성
- **JPA**: Java Persistence API

#### 보안 & 인증
- **JWT (JSON Web Token)**: 토큰 기반 인증
- **Argon2**: 비밀번호 해싱 알고리즘
- **Jasypt**: 설정 파일 암호화

#### 클라우드 & 외부 서비스
- **AWS S3**: 파일 저장소
- **Firebase Admin**: 푸시 알림 및 인증
- **Google Firebase**: 클라우드 서비스 플랫폼

#### 개발 도구
- **Lombok**: Java 보일러플레이트 코드 제거
- **Gradle**: 빌드 도구
- **Docker**: 컨테이너화

### 채팅 서버 (chat-server)
- **Node.js**: JavaScript 런타임 환경
- **Express.js**: 웹 애플리케이션 프레임워크
- **Socket.io**: 실시간 양방향 통신
- **CORS**: 크로스 오리진 리소스 공유
- **Dotenv**: 환경 변수 관리
- **Nodemon**: 개발 서버 자동 재시작

### 배포 & 인프라
- **Docker**: 컨테이너화
- **Kubernetes**: 컨테이너 오케스트레이션
- **Nginx**: 웹 서버 및 리버스 프록시
- **GitHub Actions**: CI/CD 파이프라인

## 🔌 API

### 외부 API 서비스

#### 결제 시스템
- **토스페이먼츠 (Toss Payments)**: 한국 대표 결제 시스템
  - 결제 요청 생성 및 승인
  - 결제 정보 조회
  - 결제 이력 관리
- **아임포트 (I'mport)**: 결제 모듈 연동
  - 결제 처리 및 검증
  - 결제 상태 관리

#### 지도 & 위치 서비스
- **카카오 맵 API**: 한국 지도 서비스
  - 주소 검색 및 지오코딩
  - 위치 기반 서비스
  - 지도 임베딩
- **구글 지도 API**: 글로벌 지도 서비스
  - 지도 임베딩
  - 위치 검색

#### 주소 검색
- **행정안전부 주소지 API**: 공공데이터 포털 주소 검색
  - 정확한 주소 정보 제공
  - 우편번호 검색

#### 알림 & 메시징
- **Firebase Cloud Messaging (FCM)**: 푸시 알림 서비스
  - 실시간 알림 전송
  - 사용자별 알림 관리

#### 파일 관리
- **AWS S3**: 클라우드 파일 저장소
  - 파일 업로드/다운로드
  - 이미지 리사이징
  - Presigned URL 생성

#### 이메일
- **Spring Boot Mail**: 이메일 발송 서비스
  - 인증 메일 발송
  - 알림 메일 전송

### 내부 API 서비스

#### 사용자 관리
- **인증 API**: 로그인, 회원가입, 비밀번호 찾기
- **사용자 정보 API**: 프로필 관리, 권한 관리
- **추천인 시스템**: 추천인 등록 및 보상 지급

#### 결제 & 포인트
- **포인트 충전 API**: CM 충전 시스템
- **결제 처리 API**: 상점별 결제 처리
- **쿠폰 관리 API**: 쿠폰 발급 및 사용

#### 상점 관리
- **가맹점 신청 API**: 상점 등록 및 승인
- **상점 정보 API**: 상점 상세 정보 관리
- **이미지 관리 API**: 상점 이미지 업로드

#### 게시물 & 커뮤니티
- **게시물 API**: 글쓰기, 수정, 삭제, 조회
- **댓글 API**: 댓글 작성 및 관리
- **북마크 API**: 게시물 북마크 기능

#### 알림 시스템
- **알림 API**: 실시간 알림 전송 및 관리
- **알림 타입 API**: 알림 종류별 관리
- **알림 히스토리 API**: 알림 이력 조회

#### 채팅 시스템
- **채팅방 API**: 채팅방 생성 및 관리
- **메시지 API**: 실시간 메시지 전송
- **초대 API**: 사용자 초대 기능
- **읽음 처리 API**: 메시지 읽음 상태 관리

#### 파일 서비스
- **파일 업로드 API**: 다중 파일 업로드
- **파일 다운로드 API**: 파일 다운로드
- **이미지 처리 API**: 이미지 리사이징 및 최적화

## 🚀 시작하기

### 필수 요구사항
- Node.js 14.0.0 이상
- Java 17 이상
- MySQL 8.0 이상
- Docker (선택사항)

### 설치 및 실행

#### 1. React 애플리케이션 (사용자용)
```bash
cd tesseris-react
npm install
npm start
```

#### 2. React 애플리케이션 (관리자용)
```bash
cd ERP-Tesseris-react-admin
npm install
npm start
```

#### 3. Spring Boot 서버
```bash
cd ERD-tesseris-springboot
./gradlew bootRun
```

#### 4. 채팅 서버
```bash
cd chat-server/chat-server
npm install
npm start
```

### 환경 변수 설정
프로젝트 실행을 위해 다음 환경 변수들을 설정해야 합니다:

- `AWS_ACCESS_KEY`: AWS S3 접근 키
- `AWS_SECRET_KEY`: AWS S3 시크릿 키
- `AWS_S3_BUCKET`: S3 버킷 이름
- `KAKAO_API_KEY`: 카카오 API 키
- `TOSS_SECRET_KEY`: 토스페이먼츠 시크릿 키
- `FIREBASE_CREDENTIALS`: Firebase 인증 정보

## 📱 주요 기능

- **사용자 관리**: 회원가입, 로그인, 권한 관리
- **상점 관리**: 가맹점 신청 및 승인, 상점 정보 관리
- **결제 시스템**: 포인트 충전, 결제 처리, 쿠폰 관리
- **커뮤니티**: 게시물 작성, 댓글, 북마크
- **실시간 채팅**: 사용자 간 실시간 소통
- **알림 시스템**: 푸시 알림 및 이메일 알림
- **파일 관리**: 이미지 업로드, 파일 공유
- **관리자 기능**: 사용자 관리, 시스템 모니터링


---

**Tesseris ERP System** - 효율적인 비즈니스 관리를 위한 최고의 선택 🎯
---
** 포트폴리오**
https://www.tokkitokki.kr/7guyz
