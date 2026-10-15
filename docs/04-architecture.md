# 04 — Kiến trúc

## Quyết định chính

1. **Single-loop authoritative server.** Một game thread giữ toàn bộ `World`; luồng Netty chỉ đẩy `Command` vào hộp thư MPSC. Không lock, không race.
   Khi một core không đủ, chia `World` theo zone (mỗi zone một loop). Vì vậy `World` không là singleton.
2. **Không thread/goroutine cho mỗi thực thể.** Projectile là một mảng duyệt trong một vòng lặp mỗi tick.
3. **"Spatial Timer 1 tick/giây" = `if (tick % 20 == 0)`** trong cùng game loop. Không cần timer riêng.
4. **Client gửi Input, server tự tính.** Chống gian lận từ ngày đầu.
5. **Server chỉ gửi `realm_id`**; client tra bảng `Realm` trong `common` để vẽ aura.
6. **TCP trước, UDP sau**, phía sau interface `Transport` (dự kiến, chưa viết).
7. **Một nơi định nghĩa khung gói**: `FrameCodec` dùng chung cho server, bot, client.

## Mô hình luồng

```
 Luồng Netty (nhiều)                 Game thread (một)                 Luồng nền
┌──────────────────────┐          ┌────────────────────────┐        ┌──────────────┐
│ decode → PacketHandler│ Command │ 1. drain hộp thư       │        │ ghi DB       │
│  (không đụng World)  │────────► │ 2. chạy các System     │──────► │ (sau này)    │
│                      │  MPSC    │ 3. tick++              │ task   └──────────────┘
│ ◄─ writeAndFlush ────│◄─────────│ 4. gửi snapshot/event  │
└──────────────────────┘          └────────────────────────┘
```

Thứ tự trong một tick (`GameLoop.step()`): rút hộp thư → chạy từng `GameSystem` theo thứ tự đăng ký → tăng tick.
Thứ tự đăng ký System **có ý nghĩa** (ví dụ Movement trước Projectile trước kiểm tra va chạm).

## Phụ thuộc giữa module

```
common ◄── protocol ◄── server
   ▲            ▲  ▲
   │            │  └──── bot
   └──── client (hiện chỉ dùng common; sẽ dùng protocol khi nối mạng)
labs  (độc lập, chỉ để học)
```

## Cấu trúc code hiện có và dự kiến

```
server/src/main/java/dao/server/
├─ Main.java                  khởi động GameLoop + NettyServer
├─ core/    GameLoop, World, Player, Command, GameSystem, JoinCommand, LeaveCommand
├─ net/     NettyServer, PacketHandler
├─ system/  HeartbeatSystem                     (đã có)
│           MovementSystem, SpiritVeinSystem,   (dự kiến)
│           ProjectileSystem, ShieldSystem, TribulationSystem
└─ spatial/ SpatialHashGrid, AOI                (dự kiến)
```

## Luồng gói tin hiện tại

```
Bot/Client                           Server
   │── Login{name} ───────────────►  PacketHandler: tạo id, submit(JoinCommand)
   │◄─ LoginOk{id, serverTick, 20} ─  
   │── Ping{t} ───────────────────►
   │◄─ Pong{t, serverTime} ─────────
   │── (đóng) ────────────────────►  channelInactive: submit(LeaveCommand)
```

`serverTick` trong LoginOk để client đồng bộ đồng hồ tick, cần cho Lôi Kiếp (client tự đếm ngược tới `explode_at_tick` để vẽ vùng đỏ).

## Ánh xạ tính năng thiết kế gốc sang code

| Tính năng | System / dữ liệu |
|---|---|
| Cảnh giới & Aura | `Realm` (đã có trong `common`), `LevelUpEvent{realm_id}` |
| Linh Mạch / Tọa Thiền | `SpiritVeinSystem`: mỗi 20 tick, truy vấn grid theo bán kính, lọc `MEDITATING`, chia đều 100 điểm, gửi `ExpUpdate` riêng cho từng người |
| Lôi Kiếp | `TribulationSystem`: SPAWN_WARN → EXPLODE → CHECK_HIT; 60 giây = 1200 tick; cảnh báo 20 tick; dung sai 1–2 tick; trúng quá 3 lần thì rớt cấp |
| Phi Kiếm / Linh Tán | `ProjectileSystem` (mảng, đường thẳng, va chạm vòng tròn), `ShieldSystem` (trừ Mana mỗi tick) |

Hằng số thời gian: 20 tick/giây nên **1 tick = 50ms**; "dung sai 50–100ms" = 1–2 tick.

## State machine người chơi (dự kiến)

`IDLE → MOVING → MEDITATING → TRIBULATION → DEAD`. Mỗi trạng thái quy định lệnh nào được nhận:
`MEDITATING` bỏ qua input di chuyển; `TRIBULATION` không được bắn kiếm; `DEAD` chỉ nhận yêu cầu hồi sinh.

## Những điều chỉnh so với bản thiết kế gốc (viết cho Go)

- "Hàng ngàn Projectile bằng Goroutine" → một vòng lặp, một mảng.
- "Spatial Timer" → `tick % 20`, phần "Spatial" thực sự nằm ở spatial hash grid cho AOI.
- Giữ nguyên toàn bộ tinh thần: server authoritative, client chỉ vẽ, hiệu ứng bằng hình khối + glow.
