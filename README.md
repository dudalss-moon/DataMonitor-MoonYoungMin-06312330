# DataMonitor

실시간으로 저장된 데이터 상태를 콘솔에서 조회할 수 있는 관리자용 모니터링 도구입니다.

## 화면 예시

```
╔══════════════════════════════════════════════════════════════════╗
║          DataMonitor  —  실시간 데이터 상태 조회          ║
║  시각: 2026-06-12 10:33:48                                       ║
╚══════════════════════════════════════════════════════════════════╝

  전체: 12    ● ACTIVE 8     ▲ WARNING 2     ✖ ERROR 2     ○ INACTIVE 0    이벤트: 14

  ────────────────────────────────────────────────────────────────
  ID      KEY                   VALUE               STATUS          갱신수  갱신시각
  ────────────────────────────────────────────────────────────────
  D001    CPU_USAGE             78%                 ● ACTIVE             2  10:33:47
  D002    MEMORY_USAGE          7%                  ✖ ERROR              0  10:33:46
  D003    DISK_USAGE            11%                 ● ACTIVE             0  10:33:46
  ...
  ────────────────────────────────────────────────────────────────

  새로고침 주기: 2초   종료: Ctrl+C
```

## 기능

| 기능 | 설명 |
|---|---|
| 실시간 갱신 | 2초마다 화면 전체 자동 새로고침 |
| 상태 표시 | `● ACTIVE` `▲ WARNING` `✖ ERROR` `○ INACTIVE` |
| 통계 헤더 | 전체 수 / 상태별 집계 / 누적 이벤트 수 |
| 갱신 추적 | 항목별 갱신 횟수 + 마지막 갱신 시각 |
| ANSI 컬러 | 상태별 색상 구분 (초록 / 노랑 / 빨강 / 흰색) |

## 프로젝트 구조

```
src/main/java/org/example/
├── DataMonitorApp.java          # 진입점
├── store/
│   ├── DataEntry.java           # 데이터 레코드 모델 (id, key, value, status)
│   └── DataStore.java           # 스레드 안전 인메모리 저장소 + 이벤트 리스너
├── monitor/
│   └── ConsoleMonitor.java      # ANSI 컬러 테이블 렌더러
└── generator/
    └── DataGenerator.java       # 샘플 데이터 자동 생성기 (시뮬레이터)
```

## 실행 환경

- Java 17 이상
- Gradle 9.x

## 실행 방법

### Windows — 원클릭 실행
```bat
run.bat
```

### 직접 실행
```bat
chcp 65001
set JAVA_HOME=C:\Users\User\.jdks\temurin-17.0.19
set PATH=%JAVA_HOME%\bin;%PATH%
gradlew compileJava -q
java -Dfile.encoding=UTF-8 -cp build\classes\java\main org.example.DataMonitorApp
```

### Gradle application 플러그인
```bat
gradlew run
```

종료는 `Ctrl+C`

## 모니터링 항목

| ID | KEY | 설명 |
|---|---|---|
| D001 | CPU_USAGE | 서버 CPU 사용률 |
| D002 | MEMORY_USAGE | 서버 메모리 사용률 |
| D003 | DISK_USAGE | 디스크 사용률 |
| D004 | NETWORK_IN | 네트워크 수신량 |
| D005 | NETWORK_OUT | 네트워크 송신량 |
| D006 | DB_CONN_POOL | DB 커넥션 풀 |
| D007 | ACTIVE_SESSIONS | 활성 세션 수 |
| D008 | QUEUE_SIZE | 메시지 큐 크기 |
| D009 | CACHE_HIT_RATE | 캐시 적중률 |
| D010 | ERROR_RATE | 오류율 |
| D011 | RESPONSE_TIME | 평균 응답시간 |
| D012 | BATCH_STATUS | 배치 작업 상태 |
