# 01 — Mạng cơ bản cho game

## 1. Socket

Socket là "ổ cắm" để hai chương trình nói chuyện qua mạng. Mỗi kết nối được xác định bởi
(IP client, port client, IP server, port server). Server "lắng nghe" một port, mỗi client kết nối tạo ra một socket riêng.

## 2. TCP và UDP

| | TCP | UDP |
|---|---|---|
| Đến nơi | Đảm bảo (mất thì tự gửi lại) | Không đảm bảo |
| Thứ tự | Đúng thứ tự | Có thể đảo |
| Ranh giới gói | **Không có** (dòng byte) | Có (mỗi datagram là một gói) |
| Độ trễ khi mạng xấu | Có thể tăng mạnh | Thấp, nhưng mất gói |
| Hợp với | Login, chat, sự kiện quan trọng | Vị trí, snapshot liên tục |

Hai khái niệm hay gặp khi làm game qua TCP:

- **Nagle và `TCP_NODELAY`**: Nagle gom các gói nhỏ lại để gửi một lần, tiết kiệm băng thông nhưng thêm độ trễ.
  Game real-time bật `TCP_NODELAY` để tắt gom (xem `NettyServer.java`).
- **Head-of-line blocking**: TCP giao dữ liệu đúng thứ tự, nên một gói bị mất sẽ chặn *mọi gói phía sau* cho tới khi gói đó được gửi lại.
  Snapshot vị trí cũ vô giá trị (50ms sau đã có snapshot mới) nhưng TCP vẫn bắt chờ. Đây là lý do thực sự phải chuyển sang UDP sau này.

## 3. TCP là dòng byte, nên cần "framing"

Bạn gửi 2 gói 13 byte, bên kia có thể nhận 1 lần 26 byte, hoặc 7+7+7+5. **Chạy `:labs:lab01` để tự thấy.**

Cách chuẩn: **length-prefix**. Mỗi gói là `[4 byte độ dài][payload]`.

```
┌────────────┬──────────────────────┐
│ len (4 B)  │ payload (len byte)   │  → gói tiếp theo ...
└────────────┴──────────────────────┘
```

Bên nhận: đọc đủ 4 byte → biết `len` → đọc đủ `len` byte → có một gói trọn vẹn. **`:labs:lab02`** làm thủ công, **`:labs:lab04`** để Netty làm.

Hai điểm bảo mật quan trọng: luôn đặt **giới hạn độ dài tối đa** (`FrameCodec.MAX_FRAME_BYTES`), vì client có thể gửi
`len = 2 tỷ` để làm server cấp phát bộ nhớ vô tội vạ; và không bao giờ tin dữ liệu từ client.

## 4. Blocking và non-blocking

- **Blocking**: một thread chờ một socket. Đọc mà chưa có dữ liệu thì thread đứng đợi. 1000 người = 1000 thread (**`:labs:lab03`**).
- **Non-blocking (NIO)**: một thread canh nhiều socket, socket nào sẵn sàng thì xử lý. Đây là mô hình của Netty (**`:labs:lab04`**).
- **Virtual threads (Java 21+)**: giữ code kiểu blocking dễ đọc nhưng thread rất rẻ. Với quy mô vài trăm người chơi đây là lựa chọn hợp lệ.
  Project này dùng Netty vì mục tiêu học netcode và vì đổi sang UDP sau này vẫn dùng cùng bộ khái niệm.

## 5. Khái niệm netcode game

**Tick**: server chia thời gian thành các bước cố định (ở đây 20 tick/giây = 50ms/tick). Mọi logic chạy theo tick.

**Authoritative server**: server là nguồn sự thật duy nhất. Client chỉ gửi *ý định* (Input), server tự tính kết quả. Nhờ vậy khó gian lận,
và các cơ chế như Lôi Kiếp (server tự sinh tọa độ sét, tự kiểm tra va chạm) hoạt động công bằng.

**Input, không phải trạng thái**: client gửi `Input{seq, move_x, move_y}`. Server tự cộng vị trí và giới hạn tốc độ.

**Snapshot**: server định kỳ gửi trạng thái các entity (`Snapshot{tick, ack_seq, entities[]}`).

**Interpolation (cho người khác)**: snapshot đến rời rạc (50ms/lần). Client không vẽ ngay vị trí mới nhất mà vẽ *lùi về quá khứ khoảng 100ms*
(tức nằm giữa 2 snapshot đã có) để chuyển động mượt dù mạng giật.

**Prediction + Reconciliation (cho nhân vật của mình)**: nếu chờ server trả lời rồi mới di chuyển thì thấy trễ bằng ping.
Client tự di chuyển ngay (prediction), vẫn lưu các input chưa được xác nhận. Khi snapshot về có `ack_seq`, client lấy vị trí server rồi
*chạy lại* các input chưa ack (reconciliation). Nếu giống nhau thì không thấy gì, lệch thì sửa nhẹ.

**Latency, jitter, packet loss**: độ trễ một chiều/khứ hồi (RTT), độ dao động của độ trễ, và tỉ lệ mất gói. Hãy thiết kế để chịu được cả ba.

**AOI (Area Of Interest)**: chỉ gửi cho mỗi người những entity ở gần họ. Dùng **spatial hash grid**: chia bản đồ thành ô vuông, entity nằm trong ô nào thì đăng ký ở ô đó.
Tìm "ai ở gần" chỉ cần xét vài ô lân cận, không duyệt toàn bộ.

**Ước lượng băng thông (thô, chưa đo)**: một `EntityState` Protobuf khoảng 15 byte. 50 entity × 15 B × 20 snapshot/s ≈ 15 KB/s cho mỗi người.
200 người cùng xem 50 entity thì server gửi ra khoảng 3 MB/s. Đó là lý do cần AOI, và về sau cần delta/lượng tử hóa.
(Để tham chiếu về độ gọn của Protobuf: gói `LoginOk` 3 trường bọc trong `Envelope` chỉ khoảng 8 byte.)

## 6. TCP trước, UDP sau

- Bắt đầu bằng TCP: không phải tự làm "tin cậy" nên tập trung học game loop, snapshot, prediction.
- Đặt tầng mạng phía sau một interface (dự kiến `Transport`) để game logic không biết đang dùng TCP hay UDP.
- Khi thêm UDP, phải tự giải quyết: gói nào cần tin cậy (Login, LevelUpEvent, kết quả Lôi Kiếp → gửi lại cho tới khi có ack)
  và gói nào không (Snapshot, Input → mất thì thôi, gói sau đè lên). Chia làm hai kênh là cách phổ biến:
  kênh tin cậy (TCP, hoặc UDP tự thêm seq/ack) và kênh không tin cậy (UDP).
- Netty hỗ trợ UDP (datagram channel) trong cùng khái niệm Bootstrap/Pipeline/Handler đã học ở lab04.

## Tự kiểm tra

1. Vì sao `read()` có thể trả về nửa gói? Ai chịu trách nhiệm tách gói?
2. Nếu bỏ giới hạn `MAX_FRAME_BYTES`, kẻ tấn công làm gì được?
3. Vì sao snapshot hợp với UDP hơn TCP, còn `LoginOk` thì ngược lại?
4. Interpolation lùi 100ms đổi lấy điều gì, và phải trả giá gì?
