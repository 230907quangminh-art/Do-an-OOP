import java.util.ArrayList;
import java.util.List;
 
public class SanPham {
    private int maSP;
    private String tenSP;
    private long giaBan;
    private int luongDuong;
    private DanhMuc danhMuc;
    private List<CongThuc> congThuc = new ArrayList<>();
 
    public SanPham(int maSP, String tenSP, long giaBan, int luongDuong, DanhMuc danhMuc) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.giaBan = giaBan;
        this.luongDuong = luongDuong;
        this.danhMuc = danhMuc;
    }
 
    public void themCongThuc(CongThuc ct) {
        congThuc.add(ct);
    }
 
    public long tinhGiaVon() {
        return Math.round(congThuc.stream().mapToDouble(CongThuc::chiPhi).sum());
    }
 
    public long tinhLoiNhuan() {
        return giaBan - tinhGiaVon();
    }
 
    // cau 2: da het (ton < dinh luong) hoac sap het (ton <= muc toi thieu)
    public boolean hetHoacSapHetNguyenLieu() {
        return congThuc.stream()
                .anyMatch(c -> c.khongDuChoMotPhan() || c.getNguyenLieu().sapHet());
    }
 
    public int getMaSP() { return maSP; }
    public String getTenSP() { return tenSP; }
    public long getGiaBan() { return giaBan; }
    public int getLuongDuong() { return luongDuong; }
    public DanhMuc getDanhMuc() { return danhMuc; }
    public List<CongThuc> getCongThuc() { return congThuc; }
}
 