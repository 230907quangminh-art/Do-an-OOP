public class App {

    public static void main(String[] args) throws Exception {
        DatabaseLoader dl = DatabaseLoader.nap();

        System.out.println("So san pham : " + dl.getDsSanPham().size());   // 20
        System.out.println("So don hang : " + dl.getDsDonHang().size());   // 636

        long tong = dl.getDsDonHang().stream()
                .filter(d -> d.getTrangThai() == TrangThaiDon.HOAN_THANH)
                .mapToLong(DonHang::tinhTongTien)
                .sum();
        System.out.println("Tong tien don hoan thanh: " + tong);           // 57937000

        System.out.println("Gia von SP so 1: "
                + dl.getDsSanPham().get(0).tinhGiaVon());                   // 7620
    }
}