public class ChiTietDon {
    private SanPham sanPham;
    private int soLuong;
    private long donGia;           // gia ban tai thoi diem dat
 
    public ChiTietDon(SanPham sanPham, int soLuong, long donGia) {
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }
 
    public long thanhTien() {
        return soLuong * donGia;
    }
 
    public SanPham getSanPham() { return sanPham; }
    public int getSoLuong() { return soLuong; }
    public long getDonGia() { return donGia; }
}