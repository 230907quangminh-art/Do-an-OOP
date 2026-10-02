# Quản lý quán coffee số _ Đồ án OOP cuối kỳ 

###### Project Java 17, Maven, chạy console trên IntelliJ IDEA. Sử dụng cơ sở dữ liệu MySQL 8.x Community để lưu trữ và quản lý dữ liệu của quán Coffee.
## Mở và chạy trong IntelliJ
######   
1. Giải nén project ra một thư mục trên máy.
2. Trong IntelliJ chọn **File → Open**, chọn file `pom.xml` trong thư mục project và mở dưới dạng project.
3. Chọn **Project SDK: JDK 17** trong **File → Project Structure → Project**. Cần JDK, không chỉ JRE.
4. Đợi IntelliJ tải project Maven và các thư viện cần thiết.
5. Cấu hình thông tin kết nối MySQL theo file cấu hình của project.
6. Đảm bảo cơ sở dữ liệu đã được tạo và các bảng đã được khởi tạo.
7. Mở file `App.java`.
8. Nhấn biểu tượng tam giác cạnh `main()` → **Run App.main()**.
9. Kiểm tra kết quả chương trình trên console.
Nếu không thấy nút Run: kiểm tra SDK và bảo đảm `src/main/java` là Sources Root; tải lại Maven project.
## Cấu trúc 
- 'App.java' : chương trình chính, demo
- 'Mon.java' : là cha của các 'TraSua.java(IDMon)' , 'Soda.java(IDMon))' , ... :chứa thông tin từng nhóm, món
- 'NguyenLieu.java(IDNguyenLieu' : là cha của các 'Duong.java' , 'Muoi.java' , 'Tra.java':thông tin nguyên liệu
- 'DonHang.java(IDDonHang)':chứa thông tin đơn hàng
- 'BenThuBa.java(IDBenThuBa)':chứa thông tin bên thứ 3
- 'PhieuNhap.java(IDPhieuNhap)':chứa thông tin nhập hàng
- 'NhanVien.java(IDNhanVien)': chứa thông tin nhân viên
## Quy tắc
- Mỗi sản phẩm thuộc một danh mục và sử dụng nguyên liệu theo công thức.
- Khi bán hàng, nguyên liệu được trừ theo định mức của sản phẩm.
- Đơn hàng gồm sản phẩm, số lượng, đơn giá, tổng tiền, chiết khấu và phương thức thanh toán.
- Đơn hàng có thể mua trực tiếp hoặc qua bên thứ ba.
- Đơn hàng có 3 trạng thái: đang làm, hoàn thành, đã hủy.
- Chỉ đơn hàng hoàn thành được tính vào doanh thu.
- Đơn qua bên thứ ba có thể phát sinh chiết khấu và phụ phí bao bì.
- Khi nhập nguyên liệu, số lượng tồn kho được cộng thêm.
- Chi phí nguyên liệu được dùng để tính giá vốn và lợi nhuận sản phẩm.
- Ly nhựa được ghi nhận theo số lượng sử dụng trong các đơn hàng.
## Thành phần
Hệ thống sử dụng MySQL 8.x để lưu trữ dữ liệu quản lý quán Coffee.

Cơ sở dữ liệu quản lý các thông tin chính:

- Sản phẩm
- Danh mục sản phẩm
- Nguyên liệu
- Công thức sản phẩm
- Đơn hàng
- Chi tiết đơn hàng
- Nhân viên
- Phương thức thanh toán
- Bên thứ ba
- Ly nhựa
- Thông tin nhập nguyên liệu

Java kết nối và thao tác với cơ sở dữ liệu thông qua JDBC.

## Các thực thể chính

### Sản phẩm

Lưu thông tin các món được phục vụ tại quán.

Các thuộc tính chính:

- Mã sản phẩm
- Tên sản phẩm
- Nguyên liệu
- Size
- Giá bán
- Danh mục

### Danh mục

Phân loại các sản phẩm trong quán.

Ví dụ:

- Cà phê
- Trà
- Nước ép
- Đá xay
- Bánh

### Nguyên liệu

Lưu thông tin các nguyên liệu được sử dụng để tạo ra sản phẩm.

Các thuộc tính chính:

- Mã nguyên liệu
- Tên nguyên liệu
- Đơn vị tính
- Số lượng tồn
- Giá nhập
- Mức tồn tối thiểu

### Công thức

Xác định các nguyên liệu cần thiết và số lượng sử dụng để tạo ra một sản phẩm.

Các thuộc tính chính:

- Mã sản phẩm
- Mã nguyên liệu
- Số lượng sử dụng

### Đơn hàng

Lưu thông tin mỗi lần khách hàng gọi món.

Các thuộc tính chính:

- Mã đơn hàng
- Thời gian lập đơn
- Nhân viên thực hiện
- Mã sản phẩm
- Số lượng
- Ly nhựa
- Đơn giá
- Tổng tiền 
- Chiết khấu
- Thành tiền (nếu có chiết khấu)
- Phương thức thanh toán
- Phương thức mua ( trực tiếp / bên thứ 3 )
- Trạng thái đơn hàng (đã hoàn thành/ đang trong quá trình làm / đã hủy)
### Nhân viên

Lưu thông tin nhân viên của quán.

Các thuộc tính chính:

- Mã nhân viên
- Họ tên
- Số điện thoại
- Địa chỉ
- Chức vụ

### Phương thức thanh toán

Quản lý phương thức thanh toán của đơn hàng.

Các phương thức sử dụng:

- Tiền mặt
- Chuyển khoản

### Bên thứ ba

Quản lý các nền tảng bên thứ ba được sử dụng để đặt đơn.

Ví dụ:

- GrabFood
- ShopeeFood
- Các nền tảng đặt hàng khác

Các thuộc tính chính:

- Mã bên thứ ba
- Tên bên thứ ba
- Mức chiết khấu

### Ly nhựa

Theo dõi số lượng ly nhựa được sử dụng trong quá trình bán hàng.

Các thuộc tính chính:

- Loại ly
- Số lượng
- Đơn giá

### Nhập nguyên liệu

Lưu thông tin các lần nhập nguyên liệu từ bên thứ ba.

Các thuộc tính chính:

- Mã phiếu nhập
- Mã bên thứ ba
- Mã nguyên liệu
- Ngày nhập
- Số lượng
- Đơn giá nhập
- Thành tiền

## Mối quan hệ

- Một **Danh mục** có nhiều **Sản phẩm**.
- Một **Sản phẩm** có thể sử dụng nhiều **Nguyên liệu**.
- Một **Nguyên liệu** có thể được sử dụng cho nhiều **Sản phẩm**.
- Một **Sản phẩm** có một **Công thức**.
- Một **Sản phẩm** có thể xuất hiện trong nhiều **Đơn hàng**.
- Một **Nhân viên** có thể thực hiện nhiều **Đơn hàng**.
- Một **Đơn hàng** được thực hiện bởi một **Nhân viên**.
- Một **Đơn hàng** sử dụng một **Phương thức thanh toán**.
- Một **Đơn hàng** có thể được đặt trực tiếp hoặc thông qua **Bên thứ ba**.
- Một **Bên thứ ba** có thể nhận nhiều **Đơn hàng**.
- Một **Nguyên liệu** có thể xuất hiện trong nhiều lần nhập.
- Một **Phiếu nhập** có thể chứa nhiều loại nguyên liệu.
## Câu truy vấn

- 1	Liệt kê sản phẩm
- 2	Tìm sản phẩm đã/sắp hết nguyên liệu
- 3	Lọc đơn hoàn thành trong 1 tháng cụ thể
- 4	Xem chi tiết và thành tiền đơn cụ thể
- 5	Tính tổng tiền từng đơn
- 6	Tính tổng doanh thu đơn hoàn thành ( tổng ngày )
- 7	Thống kê doanh thu theo tháng
- 8 Tìm 5 sản phẩm bán nhiều nhất theo tháng
- 9	Tìm sản phẩm chưa bán thành công ( ít đơn trong ngày )
- 10	Thống kê số đơn và doanh thu từng nhân viên
- 11	Tìm đơn có giá trị trên max/trung bình/min
- 12	Liệt kê các món thuộc một danh mục cụ thể
- 13	Tìm sản phảm có lợi nhuận cao nhất ( theo giá vốn / bán )
- 14	Tìm hóa đơn có số sản phẩm trên (1 số cụ thể)
- 15	Thống kê hóa đơn được thanh toán bằng tiền mặt / chuyển khoản
- 16	Liệt kê doanh thu của nhân viên theo từng ngày
- 17	Tìm ngày có doanh thu cao nhất
- 18	Liệt kê nguyên liệu để tạo ra một sản phẩm
- 19	Tìm hóa đơn có giá trị trên (1 số tiền cụ thể)
- 20      Tìm sản phẩm có lượng đường cụ thể
- 21      Lọc các đơn được đặt qua nền tảng bên thứ 3 có tính phụ phí bao bì
- 22      Tính tổng chiết khấu cho bên thứ 3
- 23      Thống kê số ly nhựa đã được sử dụng
- 24      Tính chi phí nguyên liệu của từng sản phẩm
- 25      Xác định bên thứ ba có lượng đặt đơn nhiều nhất
- 26      Xác định chi phí nhập của một nguyên liệu cụ thể
- 27      Tìm nguyên liệu có chi phí nhập cao nhất
- 28      Tìm nguyên liệu có mức tiêu hao trung bình cao nhất
- 29      Tìm khung giờ có số lượng sản phẩm bán ra cao nhất
- 30	Xác định các đơn đã hủy trong ngày
## Trình tự demo

Ngày demo cố định: 23/09/2026.

1. Hiển thị danh sách sản phẩm → xem được các món đang có trong quán.
2. Kiểm tra nguyên liệu → hiển thị các sản phẩm đã/sắp hết nguyên liệu.
3. Lọc các đơn hàng hoàn thành trong tháng 09/2026.
4. Xem chi tiết đơn hàng 1001 → hiển thị các sản phẩm, số lượng, đơn giá và thành tiền.
5. Tính tổng tiền của từng đơn hàng hoàn thành.
6. Tính tổng doanh thu của các đơn hàng hoàn thành trong ngày 23/09/2026.
7. Thống kê doanh thu của quán theo từng tháng.
8. Tìm 5 sản phẩm bán nhiều nhất trong tháng 09/2026.
9. Tìm các sản phẩm có số lượng bán thấp trong ngày 23/09/2026.
10. Thống kê số đơn hàng và doanh thu của từng nhân viên.
11. Tìm các đơn hàng có giá trị lớn hơn giá trị lớn nhất, trung bình hoặc nhỏ nhất theo điều kiện truy vấn.
12. Liệt kê các món thuộc danh mục "Cà phê".
13. Tìm sản phẩm có lợi nhuận cao nhất dựa trên giá vốn và giá bán.
14. Tìm các hóa đơn có số lượng sản phẩm lớn hơn 3.
15. Thống kê số hóa đơn được thanh toán bằng tiền mặt và chuyển khoản.
16. Liệt kê doanh thu của từng nhân viên theo từng ngày.
17. Tìm ngày có doanh thu cao nhất.
18. Chọn một sản phẩm → hiển thị toàn bộ nguyên liệu và số lượng cần dùng để tạo ra sản phẩm đó.
19. Tìm các hóa đơn có giá trị trên 100.000 đồng.
20. Tìm các sản phẩm có lượng đường theo mức được chọn.
21. Lọc các đơn hàng được đặt thông qua bên thứ ba và có phát sinh phụ phí bao bì.
22. Tính tổng số tiền chiết khấu đã áp dụng cho các đơn hàng qua bên thứ ba.
23. Thống kê tổng số ly nhựa đã sử dụng trong các đơn hàng.
24. Tính tổng chi phí nguyên liệu cần thiết để tạo ra từng sản phẩm.
25. Xác định bên thứ ba có số lượng đơn hàng nhiều nhất.
26. Chọn một nguyên liệu → hiển thị chi phí nhập của nguyên liệu đó.
27. Tìm nguyên liệu có chi phí nhập cao nhất.
28. Tìm nguyên liệu có mức tiêu hao trung bình cao nhất.
29. Tìm khung giờ có số lượng sản phẩm bán ra cao nhất.
30. Lọc và hiển thị các đơn hàng đã bị hủy trong ngày 23/09/2026.
## Sơ đồ
<img width="1280" height="676" alt="image" src="https://github.com/user-attachments/assets/c1b3b2c2-e3fc-411a-98c6-69331e7a085d" />

