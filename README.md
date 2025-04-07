Đầu tiên truy cập https://github.com/abetdt/fastapi-chat-with-gemini, 
thực hiện theo hướng dẫn nhưng trước khi kích hoạt môi trường ảo thì chạy

    pip insatll tzdata 
 
và ở bước cuối chạy

    uvicorn main:app --host 0.0.0.0 --port 8000

tiếp theo tải app về 

Nếu chạy trên máy android thật thì sửa IP thành:

sau đó vào ChatApp\app\src\main\java\com\example\chatapp\ApiClient.java và sửa IP thành IP máy bạn (ví dụ: http://192.168.1.154:8000)
tiếp ChatApp\app\src\main\res\xml\network_security_config.xml và sửa IP thành IP máy bạn (ví dụ: 192.168.1.154)

Còn trên máy android ảo của Android Studio thì sửa thành http://10.0.2.2:8000 và 10.0.2.2

Trong trường hợp máy thật không thể ping tới Api thì có thể do tường lửa chặn, bạn có thể tắt tường lửa hoặc
truy cập Windows Defender Firewall with Advanced Security và vào Inbound Rules -> New Rule, tạo rule cho phép truy cậo vào Port: 8000
