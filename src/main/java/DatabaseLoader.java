import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Doc toan bo du lieu tu SQLite mot lan, tao doi tuong va noi cac doi tuong voi nhau.
 * Cach dung:  DatabaseLoader dl = DatabaseLoader.nap();  roi dung dl.getDsSanPham() ...
 */
public class DatabaseLoader {
    private final List<DanhMuc> dsDanhMuc = new ArrayList<>();
    private final List<NguyenLieu> dsNguyenLieu = new ArrayList<>();
    private final List<SanPham> dsSanPham = new ArrayList<>();
    private final List<NhanVien> dsNhanVien = new ArrayList<>();
    private final List<BenThuBa> dsBenThuBa = new ArrayList<>();
    private final List<DonHang> dsDonHang = new ArrayList<>();

    // map de tra cuu nhanh theo ma khi noi cac doi tuong
    private final Map<Integer, DanhMuc> mapDanhMuc = new HashMap<>();
    private final Map<Integer, NguyenLieu> mapNguyenLieu = new HashMap<>();
    private final Map<Integer, SanPham> mapSanPham = new HashMap<>();
    private final Map<Integer, NhanVien> mapNhanVien = new HashMap<>();
    private final Map<Integer, BenThuBa> mapBenThuBa = new HashMap<>();
    private final Map<Integer, DonHang> mapDonHang = new HashMap<>();

    private DatabaseLoader() {}

    public static DatabaseLoader nap() throws Exception {
        DatabaseLoader dl = new DatabaseLoader();
        try (Connection conn = Database.getConnection()) {
            dl.napDanhMuc(conn);
            dl.napNguyenLieu(conn);
            dl.napSanPham(conn);       // can DanhMuc
            dl.napCongThuc(conn);      // can SanPham + NguyenLieu
            dl.napNhanVien(conn);
            dl.napBenThuBa(conn);
            dl.napDonHang(conn);       // can NhanVien + BenThuBa
            dl.napChiTietDon(conn);    // can DonHang + SanPham
        }
        return dl;
    }

    private void napDanhMuc(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaDM, TenDM FROM DanhMuc")) {
            while (rs.next()) {
                DanhMuc dm = new DanhMuc(rs.getInt("MaDM"), rs.getString("TenDM"));
                dsDanhMuc.add(dm);
                mapDanhMuc.put(dm.getMaDM(), dm);
            }
        }
    }

    private void napNguyenLieu(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT MaNL, TenNL, DonVi, GiaNhap, TonKho, MucToiThieu FROM NguyenLieu")) {
            while (rs.next()) {
                NguyenLieu nl = new NguyenLieu(rs.getInt("MaNL"), rs.getString("TenNL"),
                        rs.getString("DonVi"), rs.getLong("GiaNhap"),
                        rs.getDouble("TonKho"), rs.getDouble("MucToiThieu"));
                dsNguyenLieu.add(nl);
                mapNguyenLieu.put(nl.getMaNL(), nl);
            }
        }
    }

    private void napSanPham(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT MaSP, TenSP, GiaBan, LuongDuong, MaDM FROM SanPham")) {
            while (rs.next()) {
                SanPham sp = new SanPham(rs.getInt("MaSP"), rs.getString("TenSP"),
                        rs.getLong("GiaBan"), rs.getInt("LuongDuong"),
                        mapDanhMuc.get(rs.getInt("MaDM")));
                dsSanPham.add(sp);
                mapSanPham.put(sp.getMaSP(), sp);
            }
        }
    }

    private void napCongThuc(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaSP, MaNL, DinhLuong FROM CongThuc")) {
            while (rs.next()) {
                SanPham sp = mapSanPham.get(rs.getInt("MaSP"));
                NguyenLieu nl = mapNguyenLieu.get(rs.getInt("MaNL"));
                sp.themCongThuc(new CongThuc(nl, rs.getDouble("DinhLuong")));
            }
        }
    }

    private void napNhanVien(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaNV, TenNV FROM NhanVien")) {
            while (rs.next()) {
                NhanVien nv = new NhanVien(rs.getInt("MaNV"), rs.getString("TenNV"));
                dsNhanVien.add(nv);
                mapNhanVien.put(nv.getMaNV(), nv);
            }
        }
    }

    private void napBenThuBa(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaBen, TenBen, TyLeChietKhau FROM BenThuBa")) {
            while (rs.next()) {
                BenThuBa b = new BenThuBa(rs.getInt("MaBen"), rs.getString("TenBen"),
                        rs.getDouble("TyLeChietKhau"));
                dsBenThuBa.add(b);
                mapBenThuBa.put(b.getMaBen(), b);
            }
        }
    }

    private void napDonHang(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT MaDon, NgayGio, MaNV, TrangThai, PhuongThucTT, MaBen, PhuThuBaoBi "
                 + "FROM DonHang ORDER BY MaDon")) {
            while (rs.next()) {
                // NgayGio dang TEXT 'YYYY-MM-DD HH:MM:SS' -> LocalDateTime
                LocalDateTime ngayGio = LocalDateTime.parse(rs.getString("NgayGio").replace(' ', 'T'));

                TrangThaiDon trangThai = "HoanThanh".equals(rs.getString("TrangThai"))
                        ? TrangThaiDon.HOAN_THANH : TrangThaiDon.DA_HUY;
                PhuongThucTT pttt = "TienMat".equals(rs.getString("PhuongThucTT"))
                        ? PhuongThucTT.TIEN_MAT : PhuongThucTT.CHUYEN_KHOAN;

                int maBen = rs.getInt("MaBen");
                BenThuBa ben = rs.wasNull() ? null : mapBenThuBa.get(maBen);   // NULL = ban tai quan

                DonHang d = new DonHang(rs.getInt("MaDon"), ngayGio,
                        mapNhanVien.get(rs.getInt("MaNV")), trangThai, pttt, ben,
                        rs.getLong("PhuThuBaoBi"));
                dsDonHang.add(d);
                mapDonHang.put(d.getMaDon(), d);
            }
        }
    }

    private void napChiTietDon(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaDon, MaSP, SoLuong, DonGia FROM ChiTietDon")) {
            while (rs.next()) {
                DonHang d = mapDonHang.get(rs.getInt("MaDon"));
                SanPham sp = mapSanPham.get(rs.getInt("MaSP"));
                d.themChiTiet(new ChiTietDon(sp, rs.getInt("SoLuong"), rs.getLong("DonGia")));
            }
        }
    }

    public List<DanhMuc> getDsDanhMuc() { return dsDanhMuc; }
    public List<NguyenLieu> getDsNguyenLieu() { return dsNguyenLieu; }
    public List<SanPham> getDsSanPham() { return dsSanPham; }
    public List<NhanVien> getDsNhanVien() { return dsNhanVien; }
    public List<BenThuBa> getDsBenThuBa() { return dsBenThuBa; }
    public List<DonHang> getDsDonHang() { return dsDonHang; }
}