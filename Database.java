import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {

    private static final String JDBC_URL = "jdbc:hive2://localhost:10000/diabetes";
    private static final String USER = "hive";
    private static final String PASSWORD = "";

    public static void main(String[] args) {

        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");

            Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            Statement stmt = conn.createStatement();

            System.out.println("Kết nối Hive thành công");

            // ===== DATABASE =====
            stmt.execute("CREATE DATABASE IF NOT EXISTS diabetes");
            stmt.execute("USE diabetes");

            // ===== PATIENTS =====
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS patients (" +
                            "patient_id STRING," +
                            "full_name STRING," +
                            "gender STRING," +
                            "birth_year INT," +
                            "city STRING," +
                            "created_at TIMESTAMP" +
                            ") STORED AS ORC"
            );

            // ===== RECORDS =====
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS diabetes_records (" +
                            "record_id STRING," +
                            "patient_id STRING," +
                            "glucose_level DOUBLE," +
                            "hba1c DOUBLE," +
                            "blood_pressure INT," +
                            "bmi DOUBLE," +
                            "insulin DOUBLE," +
                            "record_date TIMESTAMP" +
                            ") STORED AS ORC"
            );

            // ===== DIAGNOSIS =====
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS diagnosis (" +
                            "record_id STRING," +
                            "diabetes_type STRING," +
                            "risk_level STRING," +
                            "note STRING" +
                            ") STORED AS ORC"
            );

            // ===== STAGING (CSV IMPORT) =====
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS staging_medical (" +
                            "patient_id STRING," +
                            "full_name STRING," +
                            "gender STRING," +
                            "birth_year INT," +
                            "city STRING," +

                            "record_id STRING," +
                            "glucose_level DOUBLE," +
                            "hba1c DOUBLE," +
                            "blood_pressure INT," +
                            "bmi DOUBLE," +
                            "insulin DOUBLE," +
                            "record_date TIMESTAMP," +

                            "diabetes_type STRING," +
                            "risk_level STRING," +
                            "note STRING" +
                            ") " +
                            "ROW FORMAT DELIMITED " +
                            "FIELDS TERMINATED BY ',' " +
                            "STORED AS TEXTFILE"
            );

            System.out.println("Khởi tạo database + 4 bảng thành công");

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
