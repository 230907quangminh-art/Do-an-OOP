public class SanPham {
    private int maSP;
    private String tenSP;
    private long giaBan;
    private int luongDuong;
    private int maDM;

    public SanPham(int maSP, String tenSP, long giaBan, int luongDuong, int maDM) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.giaBan = giaBan;
        this.luongDuong = luongDuong;
        this.maDM = maDM;
    }

    // Các phương thức tính toán sẽ dùng view vw_GiaVonSanPham
    public long tinhGiaVon() { /* tạm để sau */ return 0; }
    public long tinhLoiNhuan() { /* tạm để sau */ return 0; }

    // getter...
}
