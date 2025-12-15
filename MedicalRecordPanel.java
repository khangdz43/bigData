import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.UUID;


//LAY OUT THÊM DATA
public class MedicalRecordPanel extends JPanel {

    private Connection conn;

    // ===== PATIENT =====
    private JTextField txtName, txtBirthYear, txtCity;
    private JComboBox<String> cbGender;

    // ===== RECORD =====
    private JTextField txtGlucose, txtHba1c, txtBlood, txtBmi, txtInsulin, txtRecordDate;

    // ===== DIAGNOSIS =====
    private JTextField txtDiabetesType, txtRisk;
    private JTextArea txtNote;

    public MedicalRecordPanel(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout(10, 10));

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        main.add(patientPanel());
        main.add(recordPanel());
        main.add(diagnosisPanel());

        JButton btnSave = new JButton("LƯU HỒ SƠ (NHẬP TAY)");
        btnSave.addActionListener(e -> insertAll());

        JButton btnImport = new JButton("IMPORT CSV");
        btnImport.addActionListener(e -> importCSV());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnImport);
        bottom.add(btnSave);

        add(main, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    // ================= PATIENT =================
    private JPanel patientPanel() {
        JPanel p = new JPanel(new GridLayout(4, 2, 8, 8));
        p.setBorder(BorderFactory.createTitledBorder("Thông tin bệnh nhân"));

        txtName = new JTextField();
        cbGender = new JComboBox<>(new String[]{"Nam", "Nữ"});
        txtBirthYear = new JTextField();
        txtCity = new JTextField();

        p.add(new JLabel("Họ tên"));
        p.add(txtName);
        p.add(new JLabel("Giới tính"));
        p.add(cbGender);
        p.add(new JLabel("Năm sinh"));
        p.add(txtBirthYear);
        p.add(new JLabel("Thành phố"));
        p.add(txtCity);

        return p;
    }

    // ================= RECORD =================
    private JPanel recordPanel() {
        JPanel p = new JPanel(new GridLayout(6, 2, 8, 8));
        p.setBorder(BorderFactory.createTitledBorder("Chỉ số xét nghiệm"));

        txtGlucose = new JTextField();
        txtHba1c = new JTextField();
        txtBlood = new JTextField();
        txtBmi = new JTextField();
        txtInsulin = new JTextField();
        txtRecordDate = new JTextField("2025-01-01 00:00:00");

        p.add(new JLabel("Glucose"));
        p.add(txtGlucose);
        p.add(new JLabel("HbA1c"));
        p.add(txtHba1c);
        p.add(new JLabel("Huyết áp"));
        p.add(txtBlood);
        p.add(new JLabel("BMI"));
        p.add(txtBmi);
        p.add(new JLabel("Insulin"));
        p.add(txtInsulin);
        p.add(new JLabel("Ngày khám (yyyy-MM-dd HH:mm:ss)"));
        p.add(txtRecordDate);

        return p;
    }

    // ================= DIAGNOSIS =================
    private JPanel diagnosisPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createTitledBorder("Chẩn đoán"));

        JPanel top = new JPanel(new GridLayout(2, 2, 8, 8));

        txtDiabetesType = new JTextField();
        txtRisk = new JTextField();
        txtNote = new JTextArea(3, 20);

        top.add(new JLabel("Loại tiểu đường"));
        top.add(txtDiabetesType);
        top.add(new JLabel("Mức rủi ro"));
        top.add(txtRisk);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(txtNote), BorderLayout.CENTER);

        return p;
    }

    // ================= INSERT MANUAL =================
    private void insertAll() {
        try {
            String patientId = UUID.randomUUID().toString();
            String recordId = UUID.randomUUID().toString();

            Statement stmt = conn.createStatement();

            // PATIENT
            stmt.execute(
                    "INSERT INTO patients SELECT " +
                            "'" + patientId + "'," +
                            "'" + txtName.getText() + "'," +
                            "'" + cbGender.getSelectedItem() + "'," +
                            txtBirthYear.getText() + "," +
                            "'" + txtCity.getText() + "'," +
                            "current_timestamp()"
            );

            // RECORD
            stmt.execute(
                    "INSERT INTO diabetes_records SELECT " +
                            "'" + recordId + "'," +
                            "'" + patientId + "'," +
                            txtGlucose.getText() + "," +
                            txtHba1c.getText() + "," +
                            txtBlood.getText() + "," +
                            txtBmi.getText() + "," +
                            txtInsulin.getText() + "," +
                            "timestamp('" + txtRecordDate.getText() + "')"
            );

            // DIAGNOSIS
            stmt.execute(
                    "INSERT INTO diagnosis SELECT " +
                            "'" + recordId + "'," +
                            "'" + txtDiabetesType.getText() + "'," +
                            "'" + txtRisk.getText() + "'," +
                            "'" + txtNote.getText() + "'"
            );

            JOptionPane.showMessageDialog(this, "Lưu hồ sơ thành công!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // ================= RUN ETL.SQL =================
    private void runETLScript() throws Exception {

        ProcessBuilder pb = new ProcessBuilder(
                "docker", "exec", "hive-server",
                "hive", "-f", "/opt/etl.sql"
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        // đọc log hive (rất nên có)
        try (java.io.BufferedReader reader =
                     new java.io.BufferedReader(
                             new java.io.InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("ETL failed, exit code = " + exitCode);
        }
    }

    // ================= IMPORT CSV + RUN ETL =================
    private void importCSV() {
        try {
            //  Chọn file CSV
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(this, "Bạn chưa chọn file CSV!");
                return;
            }

            File csvFile = fileChooser.getSelectedFile();
            String localPath = csvFile.getAbsolutePath();
            String containerPath = "/tmp/medical_data.csv"; // đường dẫn trong container

            //  Copy file lên container Hive
            String dockerCmd = "docker cp \"" + localPath + "\" hive-server:" + containerPath;

            Process copyProcess = Runtime.getRuntime().exec(dockerCmd);
            int copyExit = copyProcess.waitFor();
            if (copyExit != 0) {
                JOptionPane.showMessageDialog(this, "Lỗi copy file lên container Hive!");
                return;
            }

            //  Load CSV vào staging table
            Statement stmt = conn.createStatement();
            String loadSql = "LOAD DATA LOCAL INPATH '/tmp/medical_data.csv' " + "INTO TABLE staging_medical";
            stmt.execute(loadSql);

            //  Chạy ETL để clean dữ liệu và insert vào các bảng chính
            runETLScript();

            JOptionPane.showMessageDialog(this, "Import CSV + ETL thành công!");

        } catch (IOException | InterruptedException e) {
            JOptionPane.showMessageDialog(this, "Lỗi thao tác file/Docker: " + e.getMessage());
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi Hive SQL/ETL: " + e.getMessage());
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
    }



}
