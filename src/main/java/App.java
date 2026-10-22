import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * MAN HINH DUY NHAT cho 30 cau truy van.
 * Ben trai: danh sach 30 cau (loc theo Nam / Minh / Hung).
 * Ben phai: o nhap / chon tham so + nut Chay + bang ket qua.
 * Moi cau chi goi 1 ham cua QuanLyCafe roi doi ket qua thanh chu de hien thi.
 */
public class App extends Application {

    // ===================== KIEU DU LIEU CHO MAN HINH =====================
    private enum Loai { SO, THANG, NGAY, DANH_MUC, SAN_PHAM, NGUYEN_LIEU, CHON }

    private record ThamSo(String ten, Loai loai, String macDinh, List<String> luaChon) {}

    /** Ket qua 1 cau: dong thong bao + bang (ten cot, cac dong). */
    private record KetQua(String thongBao, List<String> cot, List<List<String>> dong) {}

    private record Cau(int so, String nguoi, String ten, List<ThamSo> thamSo,
                       Function<Map<String, String>, KetQua> chay) {}

    // Ten cac o tham so (dung lam khoa de lay gia tri)
    private static final String K_THANG = "Tháng";
    private static final String K_NGAY = "Ngày";
    private static final String K_DANH_MUC = "Danh mục";
    private static final String K_SAN_PHAM = "Sản phẩm";
    private static final String K_NGUYEN_LIEU = "Nguyên liệu";
    private static final String K_MA_DON = "Mã đơn";
    private static final String K_LOAI = "Loại đơn";
    private static final String K_DUONG = "Lượng đường (g)";
    private static final String K_N = "Số sản phẩm (n)";
    private static final String K_TIEN = "Số tiền (đ)";

    private static final DateTimeFormatter NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter THANG = DateTimeFormatter.ofPattern("MM/yyyy");
    private static final DateTimeFormatter GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ===================== TRANG THAI MAN HINH =====================
    private DatabaseLoader dl;
    private QuanLyCafe ql;

    private ListView<Cau> listCau;
    private Label tieuDeCau;
    private Label thongBao;
    private HBox thamSoBox;
    private Button nutChay;
    private TableView<List<String>> bang;
    private final Map<String, Supplier<String>> lay = new LinkedHashMap<>();

    @Override
    public void start(Stage stage) {
        try {
            dl = DatabaseLoader.nap();
            ql = new QuanLyCafe(dl);
        } catch (Exception e) {
            e.printStackTrace();
            Label loi = new Label("Không đọc được cơ sở dữ liệu: " + e);
            loi.setWrapText(true);
            stage.setScene(new Scene(new StackPane(loi), 700, 200));
            stage.setTitle("Lỗi");
            stage.show();
            return;
        }

        // ----- Ben trai: loc + danh sach cau -----
        ComboBox<String> locNguoi = new ComboBox<>(
                FXCollections.observableArrayList("Tất cả", "Nam", "Minh", "Hưng"));
        locNguoi.setValue("Tất cả");

        FilteredList<Cau> loc = new FilteredList<>(FXCollections.observableArrayList(danhSachCau()), c -> true);
        locNguoi.setOnAction(e -> {
            String nguoi = locNguoi.getValue();
            loc.setPredicate(c -> nguoi.equals("Tất cả") || c.nguoi().equals(nguoi));
        });

        listCau = new ListView<>(loc);
        listCau.setCellFactory(lv -> new ListCell<Cau>() {
            @Override
            protected void updateItem(Cau c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.so() + ". " + c.ten() + "  (" + c.nguoi() + ")");
            }
        });
        listCau.getSelectionModel().selectedItemProperty().addListener((o, cu, moi) -> chonCau(moi));

        Label tieuDeTrai = new Label("30 câu truy vấn");
        tieuDeTrai.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        VBox trai = new VBox(8, tieuDeTrai, new Label("Xem theo người làm:"), locNguoi, listCau);
        VBox.setVgrow(listCau, Priority.ALWAYS);
        trai.setPadding(new Insets(10));
        trai.setPrefWidth(340);

        // ----- Ben phai: tham so + ket qua -----
        tieuDeCau = new Label("Chọn một câu ở bên trái");
        tieuDeCau.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        thamSoBox = new HBox(14);
        thamSoBox.setAlignment(Pos.BOTTOM_LEFT);

        nutChay = new Button("Chạy");
        nutChay.setDefaultButton(true);
        nutChay.setOnAction(e -> chay());

        thongBao = new Label();
        thongBao.setWrapText(true);

        bang = new TableView<>();
        bang.setPlaceholder(new Label("Chưa có kết quả"));

        VBox phai = new VBox(10, tieuDeCau, thamSoBox, thongBao, bang);
        VBox.setVgrow(bang, Priority.ALWAYS);
        phai.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setLeft(trai);
        root.setCenter(phai);

        stage.setTitle("Quản lý quán cà phê - 30 câu truy vấn");
        stage.setScene(new Scene(root, 1150, 680));
        stage.show();

        listCau.getSelectionModel().selectFirst();
    }

    // ===================== XU LY MAN HINH =====================

    /** Chon 1 cau: dung lai o tham so roi chay luon voi gia tri mac dinh. */
    private void chonCau(Cau c) {
        thamSoBox.getChildren().clear();
        lay.clear();
        bang.getColumns().clear();
        bang.getItems().clear();
        thongBao.setText("");
        if (c == null) {
            tieuDeCau.setText("Chọn một câu ở bên trái");
            return;
        }
        tieuDeCau.setText("Câu " + c.so() + ". " + c.ten() + "   (" + c.nguoi() + ")");
        for (ThamSo ts : c.thamSo()) {
            thamSoBox.getChildren().add(taoThamSo(ts));
        }
        thamSoBox.getChildren().add(nutChay);
        chay();
    }

    /** Doc gia tri cac o tham so, goi ham cua cau dang chon, hien ket qua. */
    private void chay() {
        Cau c = listCau.getSelectionModel().getSelectedItem();
        if (c == null) return;
        Map<String, String> p = new HashMap<>();
        lay.forEach((k, s) -> p.put(k, s.get()));
        try {
            hienKetQua(c.chay().apply(p));
            thongBao.setStyle("-fx-text-fill: black;");
        } catch (Exception e) {
            bang.getColumns().clear();
            bang.getItems().clear();
            thongBao.setText("Lỗi: " + e.getMessage());
            thongBao.setStyle("-fx-text-fill: red;");
        }
    }

    private void hienKetQua(KetQua kq) {
        thongBao.setText(kq.thongBao());
        bang.getColumns().clear();
        for (int i = 0; i < kq.cot().size(); i++) {
            final int idx = i;
            TableColumn<List<String>, String> col = new TableColumn<>(kq.cot().get(i));
            col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().get(idx)));
            col.setSortable(false);

            int rong = kq.cot().get(i).length();
            boolean toanSo = !kq.dong().isEmpty();
            for (int j = 0; j < Math.min(kq.dong().size(), 200); j++) {
                rong = Math.max(rong, kq.dong().get(j).get(idx).length());
            }
            for (List<String> dong : kq.dong()) {
                if (!dong.get(idx).matches("-?[\\d.,]+( đ)?")) {
                    toanSo = false;
                    break;
                }
            }
            if (toanSo) col.setStyle("-fx-alignment: CENTER-RIGHT;");
            col.setPrefWidth(Math.max(70, Math.min(320, rong * 8 + 28)));
            bang.getColumns().add(col);
        }
        bang.setItems(FXCollections.observableArrayList(kq.dong()));
    }

    /** Tao o nhap / chon cho 1 tham so. */
    private VBox taoThamSo(ThamSo ts) {
        Control ctl;
        Supplier<String> gia;
        switch (ts.loai()) {
            case THANG -> {
                ComboBox<String> cb = new ComboBox<>();
                ql.doanhThuTheoThang().keySet().forEach(k -> cb.getItems().add(k.toString()));
                if (cb.getItems().contains(ts.macDinh())) cb.setValue(ts.macDinh());
                else if (!cb.getItems().isEmpty()) cb.setValue(cb.getItems().get(0));
                cb.setOnAction(e -> chay());
                ctl = cb;
                gia = () -> cb.getValue() == null ? "" : cb.getValue();
            }
            case NGAY -> {
                DatePicker dp = new DatePicker(LocalDate.parse(ts.macDinh()));
                dp.setEditable(false);
                dp.setOnAction(e -> chay());
                ctl = dp;
                gia = () -> dp.getValue() == null ? "" : dp.getValue().toString();
            }
            case DANH_MUC -> {
                ComboBox<String> cb = comboId(dl.getDsDanhMuc().stream()
                        .map(x -> x.getMaDM() + " - " + x.getTenDM()).toList(), ts.macDinh());
                ctl = cb;
                gia = () -> layMa(cb);
            }
            case SAN_PHAM -> {
                ComboBox<String> cb = comboId(dl.getDsSanPham().stream()
                        .map(x -> x.getMaSP() + " - " + x.getTenSP()).toList(), ts.macDinh());
                ctl = cb;
                gia = () -> layMa(cb);
            }
            case NGUYEN_LIEU -> {
                ComboBox<String> cb = comboId(dl.getDsNguyenLieu().stream()
                        .map(x -> x.getMaNL() + " - " + x.getTenNL()).toList(), ts.macDinh());
                ctl = cb;
                gia = () -> layMa(cb);
            }
            case CHON -> {
                ComboBox<String> cb = new ComboBox<>(FXCollections.observableArrayList(ts.luaChon()));
                cb.setValue(ts.luaChon().get(0));
                cb.setOnAction(e -> chay());
                ctl = cb;
                gia = () -> cb.getValue() == null ? "" : cb.getValue();
            }
            default -> {
                TextField tf = new TextField(ts.macDinh());
                tf.setPrefWidth(150);
                tf.setOnAction(e -> chay());
                ctl = tf;
                gia = tf::getText;
            }
        }
        lay.put(ts.ten(), gia);
        return new VBox(4, new Label(ts.ten()), ctl);
    }

    /** Combo "ma - ten"; chon san muc co ma = macDinh. */
    private ComboBox<String> comboId(List<String> muc, String macDinh) {
        ComboBox<String> cb = new ComboBox<>(FXCollections.observableArrayList(muc));
        cb.setPrefWidth(230);
        String chon = muc.stream().filter(m -> m.startsWith(macDinh + " - ")).findFirst()
                .orElse(muc.isEmpty() ? null : muc.get(0));
        cb.setValue(chon);
        cb.setOnAction(e -> chay());
        return cb;
    }

    private static String layMa(ComboBox<String> cb) {
        String v = cb.getValue();
        return v == null ? "" : v.split(" - ")[0].trim();
    }

    // ===================== DOC THAM SO =====================

    private static String layChuoi(Map<String, String> p, String khoa) {
        String s = p.getOrDefault(khoa, "").trim();
        if (s.isEmpty()) throw new IllegalArgumentException("Hãy nhập / chọn \"" + khoa + "\"");
        return s;
    }

    private static long layLong(Map<String, String> p, String khoa) {
        String s = p.getOrDefault(khoa, "").replaceAll("[^0-9]", "");
        if (s.isEmpty()) throw new IllegalArgumentException("Hãy nhập số cho ô \"" + khoa + "\"");
        return Long.parseLong(s);
    }

    private static int layInt(Map<String, String> p, String khoa) {
        return (int) layLong(p, khoa);
    }

    private static LocalDate layNgay(Map<String, String> p, String khoa) {
        return LocalDate.parse(layChuoi(p, khoa));
    }

    private static YearMonth layThang(Map<String, String> p, String khoa) {
        return YearMonth.parse(layChuoi(p, khoa));
    }

    // ===================== HAM TIEN ICH HIEN THI =====================

    private static String tien(long v) {
        return String.format(Locale.US, "%,d", v).replace(',', '.') + " đ";
    }

    private static String fmt(double v) {
        return v == Math.rint(v) ? String.valueOf((long) v) : String.format(Locale.US, "%.2f", v);
    }

    private static List<String> h(String... cot) {
        return List.of(cot);
    }

    private static List<String> r(Object... gt) {
        return Arrays.stream(gt).map(String::valueOf).toList();
    }

    private static KetQua khong() {
        return new KetQua("Không có dữ liệu", List.of(), List.of());
    }

    private static String trangThai(TrangThaiDon t) {
        return t == TrangThaiDon.HOAN_THANH ? "Hoàn thành" : "Đã hủy";
    }

    private static String thanhToan(PhuongThucTT t) {
        return t == PhuongThucTT.TIEN_MAT ? "Tiền mặt" : "Chuyển khoản";
    }

    private static final List<String> COT_SP = h("Mã SP", "Tên sản phẩm", "Giá bán", "Đường (g)", "Danh mục");

    private static List<List<String>> dongSP(List<SanPham> ds) {
        return ds.stream().map(sp -> r(sp.getMaSP(), sp.getTenSP(), tien(sp.getGiaBan()),
                sp.getLuongDuong(), sp.getDanhMuc().getTenDM())).toList();
    }

    private static final List<String> COT_DON = h("Mã đơn", "Ngày giờ", "Nhân viên", "Trạng thái",
            "Thanh toán", "Bên thứ 3", "Phụ phí", "Số SP", "Khách phải trả");

    private static List<List<String>> dongDon(List<DonHang> ds) {
        return ds.stream().map(d -> r(d.getMaDon(), d.getNgayGio().format(GIO), d.getNhanVien().getTenNV(),
                trangThai(d.getTrangThai()), thanhToan(d.getPhuongThucTT()),
                d.getBenThuBa() == null ? "Tại quán" : d.getBenThuBa().getTenBen(),
                tien(d.getPhuThuBaoBi()), d.tongSoSanPham(), tien(d.tinhTongTien()))).toList();
    }

    // ===================== KHAI BAO THAM SO =====================
    private static ThamSo tsSo(String ten, String macDinh) { return new ThamSo(ten, Loai.SO, macDinh, List.of()); }
    private static ThamSo tsThang(String macDinh) { return new ThamSo(K_THANG, Loai.THANG, macDinh, List.of()); }
    private static ThamSo tsNgay(String macDinh) { return new ThamSo(K_NGAY, Loai.NGAY, macDinh, List.of()); }
    private static ThamSo tsDanhMuc(String maMacDinh) { return new ThamSo(K_DANH_MUC, Loai.DANH_MUC, maMacDinh, List.of()); }
    private static ThamSo tsSanPham(String maMacDinh) { return new ThamSo(K_SAN_PHAM, Loai.SAN_PHAM, maMacDinh, List.of()); }
    private static ThamSo tsNguyenLieu(String maMacDinh) { return new ThamSo(K_NGUYEN_LIEU, Loai.NGUYEN_LIEU, maMacDinh, List.of()); }
    private static ThamSo tsChon(String ten, String... luaChon) { return new ThamSo(ten, Loai.CHON, luaChon[0], List.of(luaChon)); }

    // ===================== 30 CAU TRUY VAN =====================
    private List<Cau> danhSachCau() {
        // ngay mac dinh cho cau 30: ngay co don huy dau tien trong du lieu
        String ngayHuy = dl.getDsDonHang().stream()
                .filter(d -> d.getTrangThai() == TrangThaiDon.DA_HUY)
                .map(d -> d.getNgayGio().toLocalDate().toString())
                .findFirst().orElse("2026-08-15");

        List<Cau> tatCa = new ArrayList<>();

        // ============================================================
        //  NAM  - cau 1, 2, 12, 13, 18, 20, 24, 26, 27, 28
        // ============================================================
        tatCa.add(new Cau(1, "Nam", "Liệt kê sản phẩm", List.of(), p -> {
            var kq = ql.lietKeSanPham();
            return new KetQua("Có " + kq.size() + " sản phẩm", COT_SP, dongSP(kq));
        }));

        tatCa.add(new Cau(2, "Nam", "Sản phẩm đã / sắp hết nguyên liệu", List.of(), p -> {
            var kq = ql.timSanPhamHetNguyenLieu();
            return new KetQua("Có " + kq.size() + " dòng cảnh báo (mỗi dòng: sản phẩm - nguyên liệu thiếu)",
                    h("Sản phẩm", "Nguyên liệu", "Tồn kho", "Mức tối thiểu", "Tình trạng"),
                    kq.stream().map(x -> r(x.sanPham().getTenSP(), x.nguyenLieu().getTenNL(),
                            fmt(x.nguyenLieu().getTonKho()), fmt(x.nguyenLieu().getMucToiThieu()),
                            x.tinhTrang().equals("Da het") ? "Đã hết" : "Sắp hết")).toList());
        }));

        tatCa.add(new Cau(12, "Nam", "Món thuộc một danh mục", List.of(tsDanhMuc("1")), p -> {
            var kq = ql.monTheoDanhMuc(layInt(p, K_DANH_MUC));
            return new KetQua("Có " + kq.size() + " món trong danh mục", COT_SP, dongSP(kq));
        }));

        tatCa.add(new Cau(13, "Nam", "Sản phẩm có lợi nhuận cao nhất", List.of(), p -> {
            var g = ql.sanPhamLoiNhuanCaoNhat();
            if (g == null) return khong();
            return new KetQua("Lợi nhuận = giá bán - giá vốn",
                    h("Sản phẩm", "Giá bán", "Giá vốn", "Lợi nhuận"),
                    List.of(r(g.sanPham().getTenSP(), tien(g.sanPham().getGiaBan()),
                            tien(g.giaVon()), tien(g.loiNhuan()))));
        }));

        tatCa.add(new Cau(18, "Nam", "Nguyên liệu để tạo ra một sản phẩm", List.of(tsSanPham("7")), p -> {
            int maSP = layInt(p, K_SAN_PHAM);
            var kq = ql.nguyenLieuCuaSanPham(maSP);
            return new KetQua("Sản phẩm mã " + maSP + " dùng " + kq.size() + " nguyên liệu",
                    h("Nguyên liệu", "Định lượng", "Đơn vị", "Chi phí"),
                    kq.stream().map(c -> r(c.getNguyenLieu().getTenNL(), fmt(c.getDinhLuong()),
                            c.getNguyenLieu().getDonVi(), tien(Math.round(c.chiPhi())))).toList());
        }));

        tatCa.add(new Cau(20, "Nam", "Sản phẩm có lượng đường trên một mức", List.of(tsSo(K_DUONG, "30")), p -> {
            var kq = ql.sanPhamTheoDuong(layInt(p, K_DUONG));
            int duong = layInt(p, K_DUONG);
            return new KetQua(
                    "Có "
                            + kq.size()
                            + " sản phẩm có lượng đường lớn hơn "
                            + duong
                            + "g",
                    COT_SP,
                    dongSP(kq));
        }));

        tatCa.add(new Cau(24, "Nam", "Chi phí nguyên liệu từng sản phẩm", List.of(), p -> {
            var kq = ql.chiPhiNguyenLieuTungSanPham();
            return new KetQua("Giá vốn = tổng (định lượng x giá nhập) của các nguyên liệu",
                    h("Sản phẩm", "Giá bán", "Giá vốn", "Lợi nhuận"),
                    kq.stream().map(x -> r(x.sanPham().getTenSP(), tien(x.sanPham().getGiaBan()),
                            tien(x.giaVon()), tien(x.loiNhuan()))).toList());
        }));

        tatCa.add(new Cau(26, "Nam", "Chi phí nhập của một nguyên liệu", List.of(tsNguyenLieu("8")), p -> {
            int maNL = layInt(p, K_NGUYEN_LIEU);
            long gia = ql.chiPhiNhap(maNL);
            NguyenLieu nl = dl.getDsNguyenLieu().stream()
                    .filter(x -> x.getMaNL() == maNL).findFirst().orElse(null);
            if (nl == null) return khong();
            return new KetQua("Giá nhập tính trên 1 " + nl.getDonVi(),
                    h("Nguyên liệu", "Đơn vị", "Giá nhập"),
                    List.of(r(nl.getTenNL(), nl.getDonVi(), tien(gia))));
        }));

        tatCa.add(new Cau(27, "Nam", "Nguyên liệu có chi phí nhập cao nhất", List.of(), p -> {
            var nl = ql.nguyenLieuNhapDatNhat();
            if (nl == null) return khong();
            return new KetQua("Nguyên liệu có giá nhập cao nhất",
                    h("Nguyên liệu", "Đơn vị", "Giá nhập"),
                    List.of(r(nl.getTenNL(), nl.getDonVi(), tien(nl.getGiaNhap()))));
        }));

        tatCa.add(new Cau(28, "Nam", "Nguyên liệu tiêu hao TB (mỗi ngày) cao nhất", List.of(), p -> {
            var t = ql.nguyenLieuTieuHaoCaoNhat();
            if (t == null) return khong();
            return new KetQua("Tính trên các đơn hoàn thành, chia cho số ngày có dùng nguyên liệu",
                    h("Nguyên liệu", "Đơn vị", "Tiêu hao TB / ngày"),
                    List.of(r(t.nguyenLieu().getTenNL(), t.nguyenLieu().getDonVi(),
                            fmt(t.tieuHaoTrungBinhNgay()))));
        }));

        // ============================================================
        //  MINH - cau 3, 4, 5, 6, 7, 11, 14, 17, 19, 30
        // ============================================================
        tatCa.add(new Cau(3, "Minh", "Đơn hoàn thành trong một tháng", List.of(tsThang("2026-08")), p -> {
            YearMonth thang = layThang(p, K_THANG);
            var kq = ql.locDonHoanThanh(thang);
            return new KetQua("Có " + kq.size() + " đơn hoàn thành trong tháng " + thang.format(THANG),
                    COT_DON, dongDon(kq));
        }));

        tatCa.add(new Cau(4, "Minh", "Chi tiết và thành tiền của một đơn",
                List.of(tsSo(K_MA_DON, "10")), p -> {

            int maDon = layInt(p, K_MA_DON);

            DonHang don = ql.xemChiTietDon(maDon);

            if (don == null) {
                return new KetQua(
                        "Không tìm thấy đơn mã " + maDon,
                        List.of(),
                        List.of());
            }

            return new KetQua(
                    "Thông tin đơn #" + maDon,

                    h("Mã đơn",
                            "Ngày giờ",
                            "Nhân viên",
                            "Trạng thái",
                            "Thanh toán",
                            "Bên thứ 3",
                            "Số SP",
                            "Phụ phí",
                            "Khách phải trả"),

                    List.of(
                            r(
                                    don.getMaDon(),
                                    don.getNgayGio().format(GIO),
                                    don.getNhanVien().getTenNV(),
                                    trangThai(don.getTrangThai()),
                                    thanhToan(don.getPhuongThucTT()),
                                    don.getBenThuBa() == null
                                            ? "Tại quán"
                                            : don.getBenThuBa().getTenBen(),
                                    don.tongSoSanPham(),
                                    tien(don.getPhuThuBaoBi()),
                                    tien(don.tinhTongTien())
                            )
                    )
            );
        }));

        tatCa.add(new Cau(5, "Minh", "Tổng tiền của một đơn",
                List.of(tsSo(K_MA_DON, "10")), p -> {

            int maDon = layInt(p, K_MA_DON);

            DonHang don = ql.xemChiTietDon(maDon);

            if (don == null) {
                return new KetQua(
                        "Không tìm thấy đơn #" + maDon,
                        List.of(),
                        List.of());
            }

            long tongTien = don.getChiTiet().stream()
                    .mapToLong(ChiTietDon::thanhTien)
                    .sum();

            return new KetQua(
                    "Đơn #" + maDon
                            + " | Tổng tiền: " + tien(tongTien)
                            + " | Phụ phí: " + tien(don.getPhuThuBaoBi())
                            + " | Khách phải trả: " + tien(don.tinhTongTien()),

                    h("Sản phẩm",
                            "Số lượng",
                            "Đơn giá",
                            "Thành tiền"),

                    don.getChiTiet().stream()
                            .map(ct -> r(
                                    ct.getSanPham().getTenSP(),
                                    ct.getSoLuong(),
                                    tien(ct.getDonGia()),
                                    tien(ct.thanhTien())
                            ))
                            .toList()
            );
        }));

        tatCa.add(new Cau(
                6,
                "Minh",
                "Doanh thu đơn hoàn thành theo ngày",

                List.of(tsNgay("2026-08-15")),

                p -> {

                    LocalDate ngay =
                            layNgay(p, K_NGAY);

                    var ds =
                            ql.donTheoNgay(ngay);

                    long tong =
                            ds.stream()
                                    .mapToLong(DonHang::tinhTongTien)
                                    .sum();

                    return new KetQua(
                            "Ngày "
                                    + ngay.format(NGAY)
                                    + " | Tổng doanh thu: "
                                    + tien(tong),

                            COT_DON,

                            dongDon(ds));
                }));


        tatCa.add(new Cau(7, "Minh", "Thống kê doanh thu theo tháng", List.of(), p -> {
            var kq = ql.doanhThuTheoThang();
            long tong = kq.values().stream().mapToLong(Long::longValue).sum();
            return new KetQua("Tổng doanh thu: " + tien(tong), h("Tháng", "Doanh thu"),
                    kq.entrySet().stream().map(x -> r(x.getKey().format(THANG), tien(x.getValue()))).toList());
        }));

        tatCa.add(new Cau(11, "Minh", "Đơn có giá trị lớn nhất / trên TB / nhỏ nhất",
                List.of(tsChon(K_LOAI, "Lớn nhất", "Trên trung bình", "Nhỏ nhất")), p -> {
            String loai = layChuoi(p, K_LOAI);
            List<DonHang> kq = switch (loai) {
                case "Lớn nhất" -> ql.donLonNhat();
                case "Trên trung bình" -> ql.donTrenTrungBinh();
                default -> ql.donNhoNhat();
            };
            return new KetQua(loai + ": " + kq.size() + " đơn (chỉ tính đơn hoàn thành)", COT_DON, dongDon(kq));
        }));

        tatCa.add(new Cau(14, "Minh", "Hóa đơn có số sản phẩm trên n", List.of(tsSo(K_N, "6")), p -> {
            int n = layInt(p, K_N);
            var kq = ql.donCoNhieuHonNSanPham(n);
            return new KetQua("Có " + kq.size() + " đơn có hơn " + n + " sản phẩm (tính cả đơn hủy)",
                    COT_DON, dongDon(kq));
        }));

        tatCa.add(new Cau(17, "Minh", "Ngày có doanh thu cao nhất", List.of(), p -> {
            var kq = ql.ngayDoanhThuCaoNhat();
            if (kq == null) return khong();
            return new KetQua("Doanh thu tính trên đơn hoàn thành", h("Ngày", "Doanh thu"),
                    List.of(r(kq.ngay().format(NGAY), tien(kq.doanhThu()))));
        }));

        tatCa.add(new Cau(19, "Minh", "Hóa đơn có giá trị trên một số tiền", List.of(tsSo(K_TIEN, "200000")), p -> {
            long soTien = layLong(p, K_TIEN);
            var kq = ql.donTrenGiaTri(soTien);
            return new KetQua("Có " + kq.size() + " đơn trên " + tien(soTien) + " (tính cả đơn hủy)",
                    COT_DON, dongDon(kq));
        }));

        tatCa.add(new Cau(30, "Minh", "Các đơn đã hủy trong ngày", List.of(tsNgay(ngayHuy)), p -> {
            LocalDate ngay = layNgay(p, K_NGAY);
            var kq = ql.donHuyTrongNgay(ngay);
            return new KetQua("Có " + kq.size() + " đơn hủy ngày " + ngay.format(NGAY), COT_DON, dongDon(kq));
        }));

        // ============================================================
        //  HUNG - cau 8, 9, 10, 15, 16, 21, 22, 23, 25, 29
        // ============================================================
        tatCa.add(new Cau(8, "Hưng", "Top 5 sản phẩm bán nhiều nhất theo tháng", List.of(tsThang("2026-08")), p -> {
            YearMonth thang = layThang(p, K_THANG);
            var kq = ql.top5SanPham(thang);
            List<List<String>> dong = new ArrayList<>();
            for (int i = 0; i < kq.size(); i++) {
                dong.add(r(i + 1, kq.get(i).sanPham().getTenSP(), kq.get(i).soLuong()));
            }
            return new KetQua("Top 5 tháng " + thang.format(THANG) + " (đơn hoàn thành)",
                    h("Hạng", "Sản phẩm", "Số lượng bán"), dong);
        }));

        tatCa.add(new Cau(9, "Hưng", "Sản phẩm chưa bán được trong ngày", List.of(tsNgay("2026-08-15")), p -> {
            LocalDate ngay = layNgay(p, K_NGAY);
            var kq = ql.sanPhamChuaBan(ngay);
            return new KetQua("Có " + kq.size() + " sản phẩm chưa bán được trong ngày " + ngay.format(NGAY),
                    COT_SP, dongSP(kq));
        }));

        tatCa.add(new Cau(10, "Hưng", "Số đơn và doanh thu từng nhân viên", List.of(), p -> {
            var kq = ql.thongKeNhanVien();
            return new KetQua("Tính trên đơn hoàn thành", h("Nhân viên", "Số đơn", "Doanh thu"),
                    kq.stream().map(x -> r(x.nhanVien().getTenNV(), x.soDon(), tien(x.doanhThu()))).toList());
        }));

        tatCa.add(new Cau(
                15,
                "Hưng",
                "Hóa đơn tiền mặt / chuyển khoản",

                List.of(
                        tsChon(
                                K_LOAI,
                                "Tiền mặt",
                                "Chuyển khoản")),

                p -> {

                    PhuongThucTT pt =
                            layChuoi(p, K_LOAI)
                                    .equals("Tiền mặt")
                                    ? PhuongThucTT.TIEN_MAT
                                    : PhuongThucTT.CHUYEN_KHOAN;

                    var kq =
                            ql.donTheoThanhToan(pt);

                    return new KetQua(
                            "Có "
                                    + kq.size()
                                    + " đơn",

                            COT_DON,

                            dongDon(kq));
                }));

        tatCa.add(new Cau(16, "Hưng", "Doanh thu nhân viên theo từng ngày", List.of(tsNgay("2026-08-15")), p -> {
            var kq =
                    ql.doanhThuNhanVienTheoNgay(layNgay(p, K_NGAY));
            return new KetQua("Có " + kq.size() + " dòng (nhân viên - ngày)", h("Nhân viên", "Ngày", "Doanh thu"),
                    kq.stream().map(x -> r(x.nhanVien().getTenNV(), x.ngay().format(NGAY),
                            tien(x.doanhThu()))).toList());
        }));

        tatCa.add(new Cau(21, "Hưng", "Đơn bên thứ 3 có phụ phí bao bì", List.of(), p -> {
            var kq = ql.donBenThuBaCoPhuThu();
            return new KetQua("Có " + kq.size() + " đơn", COT_DON, dongDon(kq));
        }));

        tatCa.add(new Cau(22, "Hưng", "Tổng chiết khấu cho bên thứ 3", List.of(), p -> {
            var kq = ql.tongChietKhau();
            long tong = kq.stream().mapToLong(x -> x.tongChietKhau()).sum();
            return new KetQua("Tổng chiết khấu tất cả các bên: " + tien(tong),
                    h("Bên thứ 3", "Tỷ lệ chiết khấu", "Tổng chiết khấu"),
                    kq.stream().map(x -> r(x.benThuBa().getTenBen(),
                            fmt(x.benThuBa().getTyLeChietKhau() * 100) + "%", tien(x.tongChietKhau()))).toList());
        }));

        tatCa.add(new Cau(23, "Hưng", "Thống kê số ly nhựa đã sử dụng", List.of(), p -> {
            long ly = ql.thongKeLyNhua();
            return new KetQua("Tính trên đơn hoàn thành", h("Chỉ tiêu", "Giá trị"),
                    List.of(r("Số ly nhựa đã dùng", ly)));
        }));

        tatCa.add(new Cau(25, "Hưng", "Bên thứ 3 có nhiều đơn nhất", List.of(), p -> {
            var kq = ql.benNhieuDonNhat();
            if (kq == null) return khong();
            return new KetQua("Tính trên đơn hoàn thành", h("Bên thứ 3", "Số đơn"),
                    List.of(r(kq.benThuBa().getTenBen(), kq.soDon())));
        }));

        tatCa.add(new Cau(29, "Hưng", "Khung giờ bán được nhiều sản phẩm nhất", List.of(), p -> {
            var kq = ql.khungGioBanChayNhat();
            if (kq == null) return khong();
            return new KetQua("Tính trên đơn hoàn thành", h("Khung giờ", "Số sản phẩm bán ra"),
                    List.of(r(kq.gio() + ":00 - " + kq.gio() + ":59", kq.soSanPham())));
        }));

        tatCa.sort((a, b) -> Integer.compare(a.so(), b.so()));
        return tatCa;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
