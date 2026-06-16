CREATE TABLE master_jabatan (
       id BIGSERIAL PRIMARY KEY NOT NULL,
       kode_jabatan VARCHAR(255) NOT NULL,
       nama_jabatan VARCHAR(255) NOT NULL,
       jenis_jabatan VARCHAR(20) NOT NULL DEFAULT('BELUM_DIATUR'),
       kode_opd VARCHAR(255) NOT NULL,
       kode_lembaga VARCHAR(5) NOT NULL,
       jabatan_aktif BOOLEAN NOT NULL DEFAULT(TRUE),

       created_date        timestamp NOT NULL DEFAULT(NOW()),
       last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);
