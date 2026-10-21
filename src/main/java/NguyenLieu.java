public class NguyenLieu {
    private int maNL;
    private String tenNL;
    private String donVi;
    private long giaNhap;          // gia nhap tren 1 don vi
    private double tonKho;         // double vi co the le
    private double mucToiThieu;
 
    public NguyenLieu(int maNL, String tenNL, String donVi, long giaNhap, double tonKho, double mucToiThieu) {
        this.maNL = maNL;
        this.tenNL = tenNL;
        this.donVi = donVi;
        this.giaNhap = giaNhap;
        this.tonKho = tonKho;
        this.mucToiThieu = mucToiThieu;
    }
 
    public boolean sapHet() {
        return tonKho <= mucToiThieu;
    }
 
    public int getMaNL() { return maNL; }
    public String getTenNL() { return tenNL; }
    public String getDonVi() { return donVi; }
    public long getGiaNhap() { return giaNhap; }
    public double getTonKho() { return tonKho; }
    public double getMucToiThieu() { return mucToiThieu; }
}