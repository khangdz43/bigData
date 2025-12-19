13/12: Bắt đầu làm project JAVA SWING THU THẬP và quản lí THÔNG TIN CỦA BỆNH NHÂN TIỂU ĐƯỜNG 
14/12 : Xây dưng được database và làm giao diện cơ bản 
19/12 : Hoàn thành project

xóa dữ liệu theo năm
thêm bản ghi bằng file csv được
thống kê được

1 giao diện thêm của bác sĩ thôi thêm 1 phát 3 bảng luôn
gồm có thêm file , thêm tay ,


và trang sau có thống kê theo năm theo tháng , xóa dữ liệu theo năm 


#NOTE import file vào docker trước rồi mới import được vào app

C:\Users\ACER>docker cp "C:\Users\ACER\Desktop\etl.sql" hive-server:/opt/etl.sql
Successfully copied 4.1kB to hive-server:/opt/etl.sql

Hive chỉ hiểu filesystem bên trong runtime của nó (container hoặc HDFS), không hiểu tên Docker container. 
Vì vậy chỉ dùng path tuyệt đối /tmp/....

viết etl để xử lí đúng quy trình imporrt data từ file



