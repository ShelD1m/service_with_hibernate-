import java.sql.Connection;
import java.sql.DriverManager;

public class TestCon {
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5439/hibernate", "postgres", "postgres");
            System.out.println("Успешное подключение!");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}