USE diabetes;
-- đã có data trong baảng stagin rồi
-- KIỂM TRA STAGING

--Extract – Transform – Load
SELECT COUNT(*) AS total_rows FROM staging_medical;

-- CLEAN DATA (LOẠI HEADER)

-- tạo 1 view v_clean...
CREATE OR REPLACE VIEW v_clean_medical AS
SELECT

--
--Hive đọc từng cột CSV dưới dạng TEXT trước, sau đó:
--
--Cố gắng parse sang kiểu  khai báo
--
--Nếu parse thất bại → NULL (KHÔNG báo lỗi)
    patient_id,
    full_name,
    gender,
    CAST(birth_year AS INT)           AS birth_year,
    city,

    record_id,
    CAST(glucose_level AS DOUBLE)     AS glucose_level,
    CAST(hba1c AS DOUBLE)             AS hba1c,
    CAST(blood_pressure AS INT)       AS blood_pressure,
    CAST(bmi AS DOUBLE)               AS bmi,
    CAST(insulin AS DOUBLE)           AS insulin,
    CAST(record_date AS TIMESTAMP)    AS record_date,

    diabetes_type,
    risk_level,
    note
FROM staging_medical
WHERE
    --  LOẠI HEADER CSV
    patient_id != 'patient_id'
    AND full_name != 'full_name'
    AND gender IN ('Nam', 'Nữ')
    AND city != 'city'

    --  BẮT BUỘC CÓ KHÓA
    AND patient_id IS NOT NULL
    AND record_id IS NOT NULL
    AND record_date IS NOT NULL;

-- LOAD PATIENTS (KHÔNG TRÙNG)

INSERT INTO TABLE patients
SELECT DISTINCT
    patient_id,
    full_name,
    gender,
    birth_year,
    city,
    current_timestamp()
FROM v_clean_medical;

-- LOAD DIABETES RECORDS

INSERT INTO TABLE diabetes_records
SELECT
    record_id,
    patient_id,
    glucose_level,
    hba1c,
    blood_pressure,
    bmi,
    insulin,
    record_date
FROM v_clean_medical;

-- LOAD DIAGNOSIS


INSERT INTO TABLE diagnosis
SELECT
    record_id,
    diabetes_type,
    risk_level,
    note
FROM v_clean_medical;

-- VERIFY ETL

SELECT 'patients' AS table_name, COUNT(*) AS total FROM patients
UNION ALL
SELECT 'diabetes_records', COUNT(*) FROM diabetes_records
UNION ALL
SELECT 'diagnosis', COUNT(*) FROM diagnosis;

-- CHECK HEADER ĐÃ BỊ LOẠI

SELECT DISTINCT gender, city FROM patients;

