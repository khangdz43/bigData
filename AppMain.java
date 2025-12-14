import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AppMain extends JFrame {

    // ===== THÔNG TIN KẾT NỐI HIVE =====
    private static final String JDBC_URL = "jdbc:hive2://localhost:10000/diabetes";
    private static final String USER = "hive";
    private static final String PASSWORD = "";

    private Connection connection;

    // ===== CONSTRUCTOR =====
    public AppMain() {

        setTitle("Hệ thống Quản lý Bệnh nhân Tiểu đường (Hive)");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        connectToDatabase();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Bệnh nhân", new PatientPanel(connection));
         tabs.addTab("Chỉ số tiểu đường", new RecordPanel(connection));
         tabs.addTab("Thống kê", new StatisticPanel(connection));

        add(tabs, BorderLayout.CENTER);
        setLocationRelativeTo(null); // căn giữa màn hình
        setVisible(true);
    }

    // ===== KẾT NỐI HIVE =====
    private void connectToDatabase() {
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            System.out.println("✅ Kết nối Hive thành công!");
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể kết nối Hive:\n" + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppMain());
    }
}
