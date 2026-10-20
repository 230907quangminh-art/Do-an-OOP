import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
public class DonHang {
    private int maDon;
    private LocalDateTime ngayGio;
    private NhanVien nhanVien;
    private TrangThaiDon trangThai;
    private PhuongThucTT phuongThucTT;
    private BenThuBa benThuBa;     // null = ban tai quan
    private long phuThuBaoBi;
    private List<ChiTietDon> chiTiet = new ArrayList<>();
 
    public DonHang(int maDon, LocalDateTime ngayGio, NhanVien nhanVien,
                   TrangThaiDon trangThai, PhuongThucTT phuongThucTT,
                   BenThuBa benThuBa, long phuThuBaoBi) {
        this.maDon = maDon;
        this.ngayGio = ngayGio;
        this.nhanVien = nhanVien;
        this.trangThai = trangThai;
        this.phuongThucTT = phuongThucTT;
        this.benThuBa = benThuBa;
        this.phuThuBaoBi = phuThuBaoBi;
    }
 
    public void themChiTiet(ChiTietDon ct) {
        chiTiet.add(ct);
    }
 
    // tong tien = tong thanh tien + phu thu bao bi
    public long tinhTongTien() {
        return phuThuBaoBi + chiTiet.stream().mapToLong(ChiTietDon::thanhTien).sum();
    }
 
    public int tongSoSanPham() {
        return chiTiet.stream().mapToInt(ChiTietDon::getSoLuong).sum();
    }
 
    public long tinhChietKhau() {
        if (benThuBa == null) return 0;
        return Math.round(tinhTongTien() * benThuBa.getTyLeChietKhau());
    }
 
    public int getMaDon() { return maDon; }
    public LocalDateTime getNgayGio() { return ngayGio; }
    public NhanVien getNhanVien() { return nhanVien; }
    public TrangThaiDon getTrangThai() { return trangThai; }
    public PhuongThucTT getPhuongThucTT() { return phuongThucTT; }
    public BenThuBa getBenThuBa() { return benThuBa; }
    public long getPhuThuBaoBi() { return phuThuBaoBi; }
    public List<ChiTietDon> getChiTiet() { return chiTiet; }
}