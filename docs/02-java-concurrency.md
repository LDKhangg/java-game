# 02 — Java concurrency cho game server

Game server có nhiều luồng: luồng Netty nhận gói, luồng game chạy logic, luồng nền ghi DB. Phần này giải thích tại sao thiết kế của project này
ép **World chỉ do một thread sở hữu**.

## 1. Race condition

`counter++` trông là một bước nhưng thực ra là ba bước: *đọc → cộng 1 → ghi*. Hai thread chen vào giữa thì một lần cộng bị mất.

**Chạy `:labs:lab00`.** Ví dụ một lần chạy thử khi tạo project: kỳ vọng 100.000.000, `int` thường chỉ đạt khoảng 98,8–99,2 triệu
(mất cả triệu lần cập nhật), còn `AtomicInteger` đạt đúng 100.000.000. Số cụ thể thay đổi mỗi lần chạy, và đó chính là điểm đáng sợ:
lỗi này không lặp lại ổn định nên rất khó debug.

## 2. Ba công cụ cơ bản

| Công cụ | Đảm bảo | Không đảm bảo |
|---|---|---|
| `volatile` | Thread khác *thấy* giá trị mới nhất (visibility) | `x++` vẫn không nguyên tử |
| `synchronized` / `Lock` | Chỉ một thread vào vùng găng tại một thời điểm + visibility | Không chống được deadlock nếu khóa sai thứ tự |
| `Atomic*` (`AtomicInteger`...) | Thao tác đọc-sửa-ghi đơn lẻ nguyên tử, không khóa | Không bảo vệ nhóm nhiều biến cùng lúc |

Trong code project: `GameLoop.publishedTick` là `volatile` (một thread ghi, nhiều thread đọc, chỉ cần visibility);
`NettyServer.playerIds` là `AtomicInteger` (nhiều luồng Netty cùng xin id).

## 3. Thread, executor, virtual thread

- `Thread`: luồng của hệ điều hành, mỗi luồng tốn bộ nhớ stack, nên không tạo hàng nghìn.
- `ExecutorService`: pool luồng tái sử dụng; đưa việc vào, pool tự chạy. Dùng cho việc nền như ghi DB (không chặn game thread).
- **Virtual thread (Java 21+)**: rất rẻ, tạo hàng chục nghìn được; hợp với việc chờ I/O. Không phù hợp cho game loop vì game loop cần một thread chạy liên tục có thời gian chính xác.

## 4. Quy tắc vàng của server này: một chủ sở hữu

Thay vì khóa mọi thứ, ta cho **đúng một thread** (game thread) đụng vào `World`. Các thread khác giao tiếp bằng **truyền thông điệp**:

```
Luồng Netty ──(Command)──► [hộp thư MPSC] ──► Game thread: drain → update systems → tick++
```

- Luồng Netty chỉ *tạo `Command`* (`JoinCommand`, `LeaveCommand`...) rồi `loop.submit(...)`. Không sửa `World` (xem `PacketHandler.java`).
- **MPSC** = Multi-Producer, Single-Consumer: nhiều thread bỏ vào, một thread lấy ra. `MpscArrayQueue` của JCTools lock-free, có giới hạn kích thước, không cấp phát node mới.
  Hộp thư đầy thì `submit` trả `false`: server từ chối thêm thay vì phình bộ nhớ.
- Lợi ích: không lock, không race, debug dễ; nhược điểm: một core gánh toàn bộ logic. Khi cần mở rộng, chia `World` theo zone (mỗi zone một game loop riêng).

Hệ quả cho bản thiết kế gốc: *không* tạo thread/goroutine cho mỗi Projectile. Một mảng projectile duyệt trong một vòng lặp mỗi tick là đủ,
nhanh hơn và không có race.

## 5. Hiệu năng: GC và cấp phát mỗi tick

Tick có ngân sách 50ms. Nếu mỗi tick tạo hàng trăm nghìn object tạm, bộ gom rác (GC) sẽ tạm dừng game loop và gây giật cho mọi người cùng lúc.

- Đo trước khi tối ưu: chạy server với `-Xlog:gc` để xem tần suất và độ dài các lần dừng.
- Hạn chế tạo object trong vòng lặp nóng (snapshot, projectile): tái sử dụng buffer, dùng mảng nguyên thủy.
- Mới bắt đầu: bộ GC mặc định là đủ. Chỉ chuyển sang GC độ trễ thấp (ZGC) khi đo thấy các lần dừng làm trễ tick.

## 6. Thời gian

- Dùng `System.nanoTime()` để đo khoảng thời gian (đơn điệu). Không dùng `currentTimeMillis()` để đo, vì đồng hồ hệ thống có thể bị chỉnh.
- `LockSupport.parkNanos` ngủ chính xác ở mức mili-giây trên Linux/macOS; trên Windows độ phân giải có thể thô hơn. Nếu thấy tick rung lắc trên Windows, đo bằng `:labs:lab05`.
- Khi một tick vượt 50ms: có hai chiến lược, *bỏ phần trễ* (code hiện tại) hoặc *chạy dồn để đuổi kịp*. Bỏ phần trễ thì thế giới chậm đi một chút nhưng ổn định;
  chạy dồn thì giữ đúng thời gian nhưng có thể tạo cơn "xoáy tử thần" khi máy quá tải. `:labs:lab05 --args=overload` cho bạn thấy tình huống.

## Tự kiểm tra

1. Thêm `volatile` vào biến đếm trong lab00 có hết lỗi không? Vì sao?
2. Vì sao `HashMap` trong `World` không cần `ConcurrentHashMap`?
3. Nếu `PacketHandler` gọi thẳng `world.addPlayer(...)` thì điều gì sai, và khi nào lỗi mới lộ ra?
4. Hộp thư MPSC đầy thì nên làm gì với kết nối đang gửi quá nhiều?
