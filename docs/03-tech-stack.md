# 03 — Các công nghệ trong project: là gì, vì sao chọn

## Phiên bản (tra cứu ngày 14/10/2026)

| Thành phần | Phiên bản | Nguồn xác nhận |
|---|---|---|
| Java | **25 (LTS)** | Trang tải Oracle: 25 là LTS mới nhất; 27 là bản mới nhất nhưng chỉ hỗ trợ ngắn hạn |
| Gradle | 9.2.1 | Java 25 yêu cầu Gradle ≥ 9.1.0 |
| Netty | 4.2.17.Final | netty.io/downloads: "Stable, Recommended" (04/08/2026) |
| libGDX | 1.14.2 | libgdx.com (trang chủ) |
| protobuf-java / protoc | 4.31.1 | **Chưa tra được bản mới nhất**; đây là bản đã biết tồn tại (runtime phải ≥ protoc, hiện hai bên cùng số) |
| protobuf Gradle plugin | 0.10.0 | README google/protobuf-gradle-plugin: bản stable mới nhất, yêu cầu Gradle ≥ 7.6 và Java 11 |
| JCTools | 4.0.5 | như trên |
| SLF4J / Logback | 2.0.17 / 1.5.18 | như trên |
| JUnit | 5.13.4 | như trên |
| foojay toolchain resolver | 1.0.0 | như trên |

Dòng "chưa tra" nghĩa là: build xanh rồi hãy nâng dần (xem Gradle bên dưới). Mọi phiên bản nằm ở một chỗ: `gradle/libs.versions.toml`.

## Gradle

Công cụ build: tải thư viện, biên dịch, chạy test, đóng gói.

- **Multi-module**: một repo chứa nhiều dự án con. `server`, `client`, `bot` đều phụ thuộc `protocol` và `common`,
  nên sửa một gói tin hay bảng `Realm` là cả hai phía biên dịch lại, không thể lệch nhau.
- **Version catalog** (`gradle/libs.versions.toml`): khai báo phiên bản một lần, module dùng `libs.netty.all`...
- **Wrapper** (`gradlew`): đảm bảo ai cũng dùng đúng phiên bản Gradle, không cần cài Gradle riêng.
- **Toolchain**: `build.gradle.kts` gốc yêu cầu JDK 25; nếu máy chưa có, plugin foojay tự tải.
- `api` và `implementation`: `protocol` khai báo Netty/Protobuf bằng `api` nên module dùng `protocol` cũng thấy chúng khi biên dịch.

Lệnh hay dùng:

```
./gradlew build                 # biên dịch + test mọi module
./gradlew test                  # chỉ test
./gradlew :server:run           # chạy một module cụ thể
./gradlew :labs:lab00           # chạy lab
./gradlew dependencies          # xem cây thư viện
./gradlew wrapper --gradle-version=<phiên bản>   # nâng Gradle
```

## Netty

Thư viện Java xây trên NIO, lo phần "canh nhiều socket cùng lúc" vốn rất khó viết bằng `java.nio` thuần.

| Khái niệm | Ý nghĩa |
|---|---|
| `EventLoop` | Một thread chạy vòng lặp "có dữ liệu thì xử lý". Một EventLoop canh nhiều kết nối |
| `EventLoopGroup` | Nhóm EventLoop. `boss` nhận kết nối mới, `workers` đọc/ghi |
| `Channel` | Một kết nối (một người chơi) |
| `ChannelPipeline` | Dây chuyền xử lý, byte đi qua lần lượt từng "trạm" (handler) |
| `ChannelHandler` | Một trạm. Inbound (nhận) và outbound (gửi) |
| `ByteBuf` | Vùng nhớ byte của Netty |

Pipeline trong project (xem `FrameCodec.java`):

```
nhận:  byte thô → LengthFieldBasedFrameDecoder → ProtobufDecoder → PacketHandler
gửi :  PacketHandler → ProtobufEncoder → LengthFieldPrepender → byte thô
```

Quy tắc: handler chạy trên luồng Netty, **không được chặn lâu** (không gọi DB, không `sleep`) và **không sửa `World`**.

Lưu ý phiên bản: tutorial cũ (Netty 4.1) dùng `NioEventLoopGroup`. Project này dùng API của 4.2:
`new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())`. Nếu đọc tutorial thấy khác, đó là lý do.

`netty-all` gom mọi module cho tiện khi học. Khi đóng gói thật có thể chọn từng module để nhẹ hơn.

## Protocol Buffers (Protobuf)

Biến object thành dãy byte để gửi qua mạng và dựng lại ở bên kia. Bạn mô tả gói tin trong `protocol/src/main/proto/dao.proto`,
plugin Gradle gọi `protoc` sinh code Java.

```proto
message Input { uint32 seq = 1; float move_x = 2; float move_y = 3; }
```

```java
byte[] data = Input.newBuilder().setSeq(5).setMoveX(1f).build().toByteArray();
Input in = Input.parseFrom(data);
```

- Số `1, 2, 3` là **mã định danh field**, không phải thứ tự. **Không bao giờ đổi hay tái dùng số đã phát hành**:
  thêm field mới với số mới thì client cũ vẫn đọc được gói mới.
- `Envelope` với `oneof payload`: mọi gói đều bọc trong một loại gói duy nhất, handler chỉ cần
  `switch (msg.getPayloadCase())`.
- Điểm yếu: mỗi gói tạo object mới nên gây áp lực GC ở đường nóng (Snapshot 20 lần/giây × số người).
  Kế hoạch: dùng Protobuf cho mọi thứ trước; khi đo thấy Snapshot là điểm nghẽn thì viết codec nhị phân tay riêng cho nó.
- Phương án thay thế đã cân nhắc: JSON (nặng, chậm), Java Serialization (chậm, kém an toàn, chỉ Java), FlatBuffers (zero-copy nhưng phức tạp hơn).

## libGDX

Framework game 2D/3D cho Java, tương đương vai trò của Ebitengine bên Go. Bạn viết một lớp có các hàm vòng đời:

| Hàm | Khi nào chạy |
|---|---|
| `create()` | Một lần lúc khởi động: nạp tài nguyên |
| `render()` | Mỗi khung hình: nhận input, cập nhật, vẽ |
| `dispose()` | Khi thoát: giải phóng GPU/bộ nhớ |

Những điều cần biết (xem `DaoGame.java`):

- **Hệ tọa độ**: gốc ở góc dưới-trái, trục Y hướng lên (khác nhiều thư viện 2D khác).
- **`Gdx.graphics.getDeltaTime()`**: thời gian giữa hai khung hình, luôn nhân vào tốc độ để chuyển động không phụ thuộc FPS.
- **Glow**: tạo một texture tròn mờ dần bằng `Pixmap`, vẽ chồng nhiều lớp với blend **cộng dồn** `(SRC_ALPHA, ONE)`.
  Rẻ hơn nhiều so với shader bloom. Aura của Cảnh Giới chỉ là đổi màu + bán kính của lớp glow, không cần asset mới.
- **Font tiếng Việt**: font mặc định của libGDX không có glyph tiếng Việt có dấu (ả, ơ, ệ...), nên demo hiện tại vẽ nhãn bằng ASCII.
  Để hiện tiếng Việt, dùng extension `gdx-freetype` nạp file `.ttf` kèm danh sách ký tự tiếng Việt.
- **macOS**: cần cờ JVM `-XstartOnFirstThread` (đã thêm tự động trong `client/build.gradle.kts`).
- Cờ `--enable-native-access=ALL-UNNAMED` tránh cảnh báo native access trên JDK mới.
  Cả cờ này lẫn việc libGDX 1.14.2 chạy tốt trên JDK 25 **chưa được chạy thử** khi tạo project: nếu có lỗi lúc `:client:run`, đây là chỗ đầu tiên cần nghi ngờ.

## JCTools

Thư viện hàng đợi lock-free cho giao tiếp giữa các thread. Project dùng `MpscArrayQueue` làm hộp thư của game loop (xem 02-java-concurrency).
Bắt đầu với `ConcurrentLinkedQueue` cũng được; JCTools nhanh hơn và không cấp phát node mỗi lần thêm.

## SLF4J + Logback

Ghi log. SLF4J là giao diện (`log.info("tick {}", tick)`), Logback là bên thực thi ghi ra console/file. Cấu hình ở `server/src/main/resources/logback.xml`.
Dùng `{}` thay vì nối chuỗi để tránh tốn công tạo chuỗi khi mức log đang tắt.

## JUnit 5

Test tự động. Vì game chạy theo tick và `GameLoop.step()` chạy đúng một tick, test mô phỏng 60 giây Lôi Kiếp (1200 tick) chỉ mất mili-giây
(xem `GameLoopTest.java`). Hãy viết test cho từng System mới.

## Lưu ý Windows

Nếu chữ tiếng Việt trong terminal hiện sai, chạy `chcp 65001` trước, hoặc dùng Windows Terminal. Các task `labs` và `bot` đã đặt `-Dstdout.encoding=UTF-8`.
