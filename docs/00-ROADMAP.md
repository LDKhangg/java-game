# 00 — Lộ trình học và build

Nguyên tắc: **học đến đâu làm đến đó**. Mỗi cấp có: đọc gì, chạy lab nào, và tiêu chí "xong".
Không cần thuộc hết mới bắt đầu, nhưng đừng nhảy cấp: mỗi cấp là nền cho cấp sau.

> Lệnh dưới đây dùng `./gradlew`. Trên Windows dùng `gradlew.bat`.

## Phần A — Kiến thức nền (làm trước khi viết tính năng game)

| Cấp | Đọc | Lab | Xong khi bạn... |
|---|---|---|---|
| 0. Concurrency | [02-java-concurrency](02-java-concurrency.md) | `:labs:lab00` | giải thích được vì sao số đếm sai, và vì sao `volatile` không sửa được |
| 1. Mạng cơ bản | [01-network-basics](01-network-basics.md) | `:labs:lab01`, `lab02`, `lab03` | nói được "TCP là dòng byte" nghĩa là gì, tự viết được length-prefix, nêu được vì sao 1 thread/1 kết nối không scale |
| 2. Netty + Protobuf | [03-tech-stack](03-tech-stack.md) | `:labs:lab04` (+ `lab04Client`) | vẽ được pipeline, chỉ ra ai tách khung, ai giải mã |
| 3. Game loop | [04-architecture](04-architecture.md) | `:labs:lab05`, đọc `GameLoop.java` + `GameLoopTest.java` | giải thích được vì sao chỉ một thread đụng vào `World`, và chọn "bỏ tick trễ" hay "chạy dồn" |
| 4. libGDX | [03-tech-stack](03-tech-stack.md) mục libGDX | `:client:run` | đổi được Cảnh Giới bằng phím 1/2/3 và hiểu vì sao đổi aura không cần asset mới |

Chạy các lab theo thứ tự, mỗi lab đọc kỹ comment đầu file: có "Câu hỏi" và "Thử" để bạn tự sửa code.

## Phần B — Cột mốc dự án

### Khởi tạo (Init) — XONG
- [x] Gradle multi-module (`common`, `protocol`, `server`, `client`, `bot`, `labs`), version catalog
- [x] `dao.proto`: Login, LoginOk, Input, Snapshot, Ping/Pong trong `Envelope`
- [x] Server: Netty + `FrameCodec` + `GameLoop` 20 tick/giây + `HeartbeatSystem`
- [x] Login → LoginOk, Ping → Pong, Join/Leave đi qua hộp thư MPSC
- [x] `SmokeBot` kiểm tra server sống
- [x] Client libGDX offline: vòng tròn + glow + đổi Cảnh Giới

### Phiên bản Vô Danh — di chuyển, hiện tên, thấy nhau (TIẾP THEO)
- [ ] Server: `MovementSystem` đọc `Input` (kiểm tra tốc độ tối đa, chuẩn hóa vector)
- [ ] Server: `InputCommand` + lưu `lastAckSeq` cho từng `Player`
- [ ] Server: phát `Snapshot` toàn bản đồ mỗi tick (chưa cần AOI)
- [ ] Client: `NettyClient` chạy thread riêng, đẩy gói nhận được vào queue; render thread rút queue mỗi frame
- [ ] Client: vẽ người khác bằng interpolation (lùi ~100ms)
- [ ] Client: prediction + reconciliation cho nhân vật của mình
- [ ] Mở 2–3 client + `SmokeBot` mở rộng thành bot di chuyển ngẫu nhiên để kiểm tra

### Phiên bản Tụ Khí — Linh Mạch, Tọa Thiền, EXP
- [ ] `SpiritVein` entity + `SpiritVeinSystem` (`tick % 20 == 0`, chia đều 100 điểm cho người đang `MEDITATING`)
- [ ] State machine người chơi: `IDLE → MOVING → MEDITATING`
- [ ] Gói `ExpUpdate`, `LevelUpEvent` (chỉ gửi `realm_id`)
- [ ] Spatial hash grid + AOI (thay snapshot toàn bản đồ)
- [ ] Bot: 200 bot cùng ngồi quanh một Linh Mạch

### Phiên bản Huyết Chiến — Phi Kiếm, Linh Tán
- [ ] `ProjectileSystem` (một mảng, một vòng lặp mỗi tick; KHÔNG thread/goroutine cho mỗi kiếm)
- [ ] `ShieldSystem` (trừ Mana mỗi tick, vỡ khi hết)
- [ ] Chết → hồi sinh tại tọa độ mặc định

### Phiên bản Nghịch Thiên — Lôi Kiếp
- [ ] `TribulationSystem`: state machine theo tick, 60 giây = 1200 tick
- [ ] `LightningWarn{id, x, y, radius, explode_at_tick}`; cảnh báo 20 tick (1 giây)
- [ ] Dung sai 1–2 tick (50–100ms) khi kiểm tra va chạm để bù ping
- [ ] Client: làm tối màn hình chỉ ở máy người đang độ kiếp

### Sau đó
- [ ] Thêm UDP (xem mục "TCP trước, UDP sau" trong 01-network-basics)
- [ ] Lưu trữ: SQLite hoặc PostgreSQL qua JDBC, ghi bất đồng bộ (không chặn game thread)
