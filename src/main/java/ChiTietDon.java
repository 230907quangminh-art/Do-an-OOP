public class ChiTietDon {
    private int maDon;
    private int maSP;
    private int soLuong;
    private long donGia;

    public ChiTietDon(int maDon, int maSP, int soLuong, long donGia) {
        this.maDon = maDon;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public long thanhTien() {
        return soLuong * donGia;
    }

    // getter...
}
