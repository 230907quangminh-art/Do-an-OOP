import java.io.InputStream;
import java.nio.file.*;
import java.sql.*;

public final class Database {
    private static Path dbFile;

    private Database() {}

    public static Connection getConnection() throws Exception {
        if (dbFile == null) {
            dbFile = Files.createTempFile("quan_cafe_", ".db");
            dbFile.toFile().deleteOnExit();               // xóa bản tạm khi tắt chương trình
            try (InputStream in = Database.class.getResourceAsStream("/quan_cafe.db")) {
                if (in == null) {
                    throw new IllegalStateException("Không tìm thấy quan_cafe.db trong resources");
                }
                Files.copy(in, dbFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return DriverManager.getConnection("jdbc:sqlite:" + dbFile.toAbsolutePath());
    }
}