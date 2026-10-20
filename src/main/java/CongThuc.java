public class CongThuc {
    private NguyenLieu nguyenLieu;
    private double dinhLuong;      // double vi co dinh luong le (vd 0.5 qua cam)
 
    public CongThuc(NguyenLieu nguyenLieu, double dinhLuong) {
        this.nguyenLieu = nguyenLieu;
        this.dinhLuong = dinhLuong;
    }
 
    // chi phi nguyen lieu nay cho 1 san pham
    public double chiPhi() {
        return dinhLuong * nguyenLieu.getGiaNhap();
    }
 
    // ton kho khong du de lam 1 phan san pham
    public boolean khongDuChoMotPhan() {
        return nguyenLieu.getTonKho() < dinhLuong;
    }
 
    public NguyenLieu getNguyenLieu() { return nguyenLieu; }
    public double getDinhLuong() { return dinhLuong; }
}