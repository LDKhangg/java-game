# Đạo Tâm (Core of Dao) — `java-game`

Game 2D top-down multiplayer RPG chủ đề tu tiên. Server authoritative viết bằng Java (Netty),
client libGDX vẽ hình khối có hiệu ứng glow. Mục tiêu kép: làm ra game, và luyện kỹ năng lập trình mạng / game server.

| | |
|---|---|
| Ngôn ngữ | Java 25 (LTS) |
| Build | Gradle 9 (Kotlin DSL, multi-module) |
| Server | Netty 4.2 + Protobuf + JCTools |
| Client | libGDX (LWJGL3) |

## Bắt đầu nhanh

Cần: Git và JDK 17 trở lên để khởi động Gradle (Gradle sẽ tự tải JDK 25 để biên dịch qua toolchain). Trên Windows dùng `gradlew.bat`.

```bash
./gradlew build            # biên dịch + test toàn bộ
./gradlew :server:run      # terminal 1: chạy server (cổng 7777)
./gradlew :bot:run         # terminal 2: smoke test, kỳ vọng: LoginOk rồi Pong
./gradlew :client:run      # cửa sổ libGDX offline: WASD di chuyển, phím 1/2/3 đổi Cảnh Giới
```

Server đúng khi log in `tick=0 players=0`, `tick=20 ...` mỗi giây, và `bot` in `Pong: RTT ~ N ms -> server OK`.

## Học trước, build sau

Đọc [docs/00-ROADMAP.md](docs/00-ROADMAP.md) trước. Các lab nằm ở `labs/`: `./gradlew :labs:lab00` ... `:labs:lab05`.

| Tài liệu | Nội dung |
|---|---|
| [00-ROADMAP](docs/00-ROADMAP.md) | Lộ trình học + cột mốc dự án (checklist) |
| [01-network-basics](docs/01-network-basics.md) | Socket, TCP/UDP, framing, blocking vs NIO, netcode game |
| [02-java-concurrency](docs/02-java-concurrency.md) | Race condition, volatile/synchronized/atomic, mô hình một chủ sở hữu, GC |
| [03-tech-stack](docs/03-tech-stack.md) | Gradle, Netty, Protobuf, libGDX, JCTools... là gì và vì sao chọn; bảng phiên bản |
| [04-architecture](docs/04-architecture.md) | Quyết định kiến trúc, mô hình luồng, cấu trúc code, ánh xạ tính năng |

## Cấu trúc

```
java-game/
├─ common/     hằng số + bảng Cảnh Giới (Realm)
├─ protocol/   dao.proto + FrameCodec (khung gói + Protobuf)
├─ server/     Netty + GameLoop 20 tick/giây
├─ client/     libGDX
├─ bot/        client headless để kiểm tra / load-test
├─ labs/       bài tập nền tảng
└─ docs/       tài liệu
```

## Trạng thái kiểm chứng khi tạo project

Đã chạy thật:
- Các lab chỉ cần JDK (lab00, 01, 02, 05) biên dịch và cho kết quả đúng.
- Logic lõi của `GameLoop` + `HeartbeatSystem` + `JoinCommand`/`LeaveCommand` (biên dịch với stub JCTools/SLF4J, chạy 41 tick đúng).
- `dao.proto` hợp lệ với `protoc`, round-trip một `Envelope` đúng.

**Chưa chạy được** (môi trường tạo project không tải được Maven Central/Gradle): `./gradlew build` và mọi thứ cần Netty, Protobuf-Java, libGDX, JCTools thật
(`NettyServer`, `PacketHandler`, `SmokeBot`, `Lab03/04`, client libGDX, các JUnit test). Lần chạy đầu tiên của bạn là lần kiểm chứng đầu tiên.
Nếu `./gradlew build` báo lỗi, gửi nguyên đoạn lỗi để sửa. Những chỗ dễ lệch nhất: số phiên bản trong `gradle/libs.versions.toml`
(đặc biệt các dòng "chưa tra" trong docs/03: protobuf-java, JCTools, Logback, JUnit, foojay) và API Netty 4.2.
