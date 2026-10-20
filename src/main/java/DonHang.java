import java.time.LocalDateTime;
import java.util.List;

public class DonHang {
    private int maDon;
    private LocalDateTime ngayGio;
    private int maNV;
    private TrangThaiDon trangThai;
    private PhuongThucTT phuongThucTT;
    private Integer maBen; // có thể null
    private long phuThuBaoBi;
    private List<ChiTietDon> chiTiet; // nạp thêm sau

    public DonHang(int maDon, LocalDateTime ngayGio, int maNV,
                   TrangThaiDon trangThai, PhuongThucTT phuongThucTT,
                   Integer maBen, long phuThuBaoBi) {
        this.maDon = maDon;
        this.ngayGio = ngayGio;
        this.maNV = maNV;
        this.trangThai = trangThai;
        this.phuongThucTT = phuongThucTT;
        this.maBen = maBen;
        this.phuThuBaoBi = phuThuBaoBi;
    }

    public long tinhTongTien() {
        long tong = phuThuBaoBi;
        if (chiTiet != null) {
            for (ChiTietDon ct : chiTiet) {
                tong += ct.thanhTien();
            }
        }
        return tong;
    }

    public int tongSoSanPham() {
        int tong = 0;
        if (chiTiet != null) {
            for (ChiTietDon ct : chiTiet) {
                tong += ct.getSoLuong();
            }
        }
        return tong;
    }

    public long tinhChietKhau(BenThuBa ben) {
        if (maBen == null) return 0;
        return (long)(tinhTongTien() * ben.getTyLeChietKhau());
    }

    // getter...
}
