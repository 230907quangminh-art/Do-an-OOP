public class NguyenLieu {
    private int maNL;
    private String tenNL;
    private String donVi;
    private long giaNhap;
    private int tonKho;
    private int mucToiThieu;

    public NguyenLieu(int maNL, String tenNL, String donVi, long giaNhap, int tonKho, int mucToiThieu) {
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

    // getter...
}
