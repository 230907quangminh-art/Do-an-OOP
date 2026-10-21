public class BenThuBa {
    private int maBen;
    private String tenBen;
    private double tyLeChietKhau;

    public BenThuBa(int maBen, String tenBen, double tyLeChietKhau) {
        this.maBen = maBen;
        this.tenBen = tenBen;
        this.tyLeChietKhau = tyLeChietKhau;
    }

    public int getMaBen() { return maBen; }
    public String getTenBen() { return tenBen; }
    public double getTyLeChietKhau() { return tyLeChietKhau; }
}