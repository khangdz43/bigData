import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {

    // Thông tin kết nối Hive
    private static final String JDBC_URL = "jdbc:hive2://localhost:10000/diabetes";
    private static final String USER = "hive";
    private static final String PASSWORD = "";

    public static void main(String[] args) {

        try {
            // 1. Load Hive JDBC Driver
            Class.forName("org.apache.hive.jdbc.HiveDriver");

            // 2. Kết nối Hive
            Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            Statement stmt = conn.createStatement();

            System.out.println("Kết nối Hive thành công");

            // 3. Tạo database
            stmt.execute("CREATE DATABASE IF NOT EXISTS diabetes");
            stmt.execute("USE diabetes");

            System.out.println("Tạo database diabetes");

            // 4. Tạo bảng patients
            String createPatientsTable = "CREATE TABLE IF NOT EXISTS patients (\n" +
                    "    patient_id STRING,\n" +
                    "    full_name STRING,\n" +
                    "    gender STRING,\n" +
                    "    birth_year INT,\n" +
                    "    city STRING,\n" +
                    "    created_at TIMESTAMP\n" +
                    ")\n" +
                    "STORED AS ORC";
            stmt.execute(createPatientsTable);
            System.out.println("Tạo bảng patients");

            // 5. Tạo bảng diabetes_records (partition)
            String createRecordsTable = "CREATE TABLE IF NOT EXISTS diabetes_records (\n" +
                    "    record_id STRING,\n" +
                    "    patient_id STRING,\n" +
                    "    glucose_level DOUBLE,\n" +
                    "    hba1c DOUBLE,\n" +
                    "    blood_pressure INT,\n" +
                    "    bmi DOUBLE,\n" +
                    "    insulin DOUBLE,\n" +
                    "    record_date TIMESTAMP\n" +
                    ")\n" +
                    "STORED AS ORC";
            stmt.execute(createRecordsTable);
            System.out.println("Tạo bảng diabetes_records");

            // 6. Tạo bảng diagnosis
            String createDiagnosisTable = "CREATE TABLE IF NOT EXISTS diagnosis (\n" +
                    "    record_id STRING,\n" +
                    "    diabetes_type STRING,\n" +
                    "    risk_level STRING,\n" +
                    "    note STRING\n" +
                    ")\n" +
                    "STORED AS ORC";
            stmt.execute(createDiagnosisTable);
            System.out.println("Tạo bảng diagnosis");

            // 7. Đóng kết nối
            stmt.close();
            conn.close();

            System.out.println("Khởi tạo database & bảng thành công!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
