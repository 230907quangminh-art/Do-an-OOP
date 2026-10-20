import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * QuanLyCafe - 30 cau truy van, 3 nguoi cung lam tren 1 file, chia bang cac khung comment:
 *   Nam : san pham, nguyen lieu      -> cau 1, 2, 12, 13, 18, 20, 24, 26, 27, 28
 *   Minh : don hang, doanh thu        -> cau 3, 4, 5, 6, 7, 11, 14, 17, 19, 30
 *   Hung : nhan vien, thanh toan, ben thu 3, thong ke ban hang
 *                                               -> cau 8, 9, 10, 15, 16, 21, 22, 23, 25, 29
 * Quy uoc: ham chi tra du lieu (khong in). Doi tuong don le khong co -> null; list khong co -> rong.
 */
public class QuanLyCafe {

    private final DatabaseLoader dl;

    public QuanLyCafe(DatabaseLoader dl) {
        this.dl = dl;
    }

    // ---- Ham dung chung cho ca 3 nguoi (khong sua) ----
    private static boolean hoanThanh(DonHang d) {
        return d.getTrangThai() == TrangThaiDon.HOAN_THANH;
    }

    private static final String TEN_LY_NHUA = "Ly nhua";   // dung o cau 23

    // =====================================================================
    // =====================================================================
    //  PHAN CUA NAM
    //  San pham, danh muc, nguyen lieu
    //  Cac cau: 1, 2, 12, 13, 18, 20, 24, 26, 27, 28
    //  (Chi sua trong khung nay. Khong sua phan cua nguoi khac.)
    // =====================================================================
    // =====================================================================

    public record CanhBaoHet(SanPham sanPham, NguyenLieu nguyenLieu, String tinhTrang) {}   // cau 2
    public record GiaVonSP(SanPham sanPham, long giaVon, long loiNhuan) {}                  // cau 13, 24
    public record TieuHaoNL(NguyenLieu nguyenLieu, double tieuHaoTrungBinhNgay) {}          // cau 28

    // Cau 1: Liet ke san pham
    public List<SanPham> lietKeSanPham() {
        return new ArrayList<>(dl.getDsSanPham());
    }

    // Cau 2: San pham da / sap het nguyen lieu (moi dong = 1 nguyen lieu bi thieu cua 1 san pham)
    public List<CanhBaoHet> timSanPhamHetNguyenLieu() {
        List<CanhBaoHet> kq = new ArrayList<>();
        for (SanPham sp : dl.getDsSanPham()) {
            for (CongThuc ct : sp.getCongThuc()) {
                NguyenLieu nl = ct.getNguyenLieu();
                if (ct.khongDuChoMotPhan()) {
                    kq.add(new CanhBaoHet(sp, nl, "Da het"));
                } else if (nl.sapHet()) {
                    kq.add(new CanhBaoHet(sp, nl, "Sap het"));
                }
            }
        }
        return kq;
    }

    // Cau 12: Cac mon thuoc 1 danh muc
    public List<SanPham> monTheoDanhMuc(int maDM) {
        return dl.getDsSanPham().stream()
                .filter(sp -> sp.getDanhMuc().getMaDM() == maDM)
                .toList();
    }

    // Cau 13: San pham co loi nhuan (gia ban - gia von) cao nhat
    public GiaVonSP sanPhamLoiNhuanCaoNhat() {
        return chiPhiNguyenLieuTungSanPham().stream()
                .max(Comparator.comparingLong(GiaVonSP::loiNhuan))
                .orElse(null);
    }

    // Cau 18: Nguyen lieu de tao ra 1 san pham
    public List<CongThuc> nguyenLieuCuaSanPham(int maSP) {
        return dl.getDsSanPham().stream()
                .filter(sp -> sp.getMaSP() == maSP)
                .findFirst()
                .map(SanPham::getCongThuc)
                .orElse(List.of());
    }

    // Cau 20: San pham co luong duong cu the (gram)
    public List<SanPham> sanPhamTheoDuong(int luongDuong) {
        return dl.getDsSanPham().stream()
                .filter(sp -> sp.getLuongDuong() == luongDuong)
                .toList();
    }

    // Cau 24: Chi phi nguyen lieu (gia von) cua tung san pham
    public List<GiaVonSP> chiPhiNguyenLieuTungSanPham() {
        return dl.getDsSanPham().stream()
                .map(sp -> new GiaVonSP(sp, sp.tinhGiaVon(), sp.tinhLoiNhuan()))
                .toList();
    }

    // Cau 26: Chi phi nhap cua 1 nguyen lieu (gia nhap tren 1 don vi). Khong thay thi tra 0
    public long chiPhiNhap(int maNL) {
        return dl.getDsNguyenLieu().stream()
                .filter(nl -> nl.getMaNL() == maNL)
                .mapToLong(NguyenLieu::getGiaNhap)
                .findFirst()
                .orElse(0);
    }

    // Cau 27: Nguyen lieu co chi phi nhap cao nhat
    public NguyenLieu nguyenLieuNhapDatNhat() {
        return dl.getDsNguyenLieu().stream()
                .max(Comparator.comparingLong(NguyenLieu::getGiaNhap))
                .orElse(null);
    }

    // Cau 28: Nguyen lieu co muc tieu hao trung binh moi ngay cao nhat
    // tieu hao = tong (so luong ban x dinh luong) cua don HOAN THANH, chia cho so ngay co dung nguyen lieu do
    public TieuHaoNL nguyenLieuTieuHaoCaoNhat() {
        Map<NguyenLieu, Double> tongTieuHao = new HashMap<>();
        Map<NguyenLieu, Set<LocalDate>> ngayDung = new HashMap<>();

        for (DonHang d : dl.getDsDonHang()) {
            if (d.getTrangThai() != TrangThaiDon.HOAN_THANH) continue;
            LocalDate ngay = d.getNgayGio().toLocalDate();
            for (ChiTietDon ct : d.getChiTiet()) {
                for (CongThuc c : ct.getSanPham().getCongThuc()) {
                    NguyenLieu nl = c.getNguyenLieu();
                    tongTieuHao.merge(nl, ct.getSoLuong() * c.getDinhLuong(), Double::sum);
                    ngayDung.computeIfAbsent(nl, k -> new HashSet<>()).add(ngay);
                }
            }
        }

        return tongTieuHao.entrySet().stream()
                .map(e -> new TieuHaoNL(e.getKey(), e.getValue() / ngayDung.get(e.getKey()).size()))
                .max(Comparator.comparingDouble(TieuHaoNL::tieuHaoTrungBinhNgay))
                .orElse(null);
    }

    // ----------------------- HET PHAN CUA NAM -----------------------
}