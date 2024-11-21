<img width="1680" alt="Ảnh màn hình 2024-11-21 lúc 19 06 28" src="https://github.com/user-attachments/assets/1fe3371d-8806-4dfd-aa88-98e94797ebe8">
<img width="1680" alt="Ảnh màn hình 2024-11-21 lúc 19 06 15" src="https://github.com/user-attachments/assets/10a0d0ec-232e-48cd-a5a3-b35894d815e3">
Hệ thống Nhà Thông Minh Điều Khiển Qua Ứng Dụng Di Động
Giới thiệu
Dự án Hệ thống Nhà Thông Minh là một giải pháp IoT (Internet of Things) giúp quản lý và điều khiển các thiết bị trong gia đình thông qua ứng dụng di động. Với mục tiêu mang lại sự tiện nghi và an toàn, hệ thống cho phép người dùng điều khiển ánh sáng, quạt, bình nóng lạnh, cửa cuốn và các thiết bị cảnh báo từ xa qua giao diện thân thiện.

Tính năng chính
Điều khiển ánh sáng:
Bật/tắt đèn từ xa.
Điều khiển quạt:
Bật/tắt quạt qua ứng dụng.
Giám sát an ninh:
Quản lý trạng thái đóng/mở cửa cuốn ra vào.
Điều khiển bình nóng lạnh:
Điều khiển bật/tắt thiết bị.
Thiết bị cảnh báo:
Kích hoạt còi cảnh báo khi phát hiện trạng thái bất thường.
Kiến trúc hệ thống
Hệ thống được xây dựng dựa trên các thành phần chính:

Thiết bị IoT:
ESP8266 và các cảm biến, relay, module điều khiển.
Trung tâm điều khiển (Hub):
Kết nối các thiết bị IoT và giao tiếp với máy chủ qua MQTT.
Backend:
API được xây dựng bằng .NET Core, triển khai trên Azure với cơ sở dữ liệu MSSQL.
Ứng dụng di động Android:
Giao diện thân thiện, hỗ trợ điều khiển thiết bị và giám sát thời gian thực.

Công nghệ sử dụng
Ngôn ngữ: C#, Java.
Giao thức truyền thông: MQTT.
Backend: .NET Core API, MSSQL.
Frontend (ứng dụng Android): Retrofit, WebSocket, MQTT.
Phần cứng: ESP8266, module L298N, mạch relay.

