CREATE TABLE jabatan (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    kode_jabatan        VARCHAR(30) NOT NULL,
    nama_jabatan        VARCHAR(255) UNIQUE NOT NULL,
    kode_opd            VARCHAR(22) NOT NULL,
    jenis_jabatan       VARCHAR(20) NOT NULL,
    tahun               INTEGER NOT NULL,
    bulan               INTEGER NOT NULL,
    esselon             VARCHAR(10) NOT NULL,
    created_date        timestamp NOT NULL DEFAULT(NOW()),
    last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);

Create TABLE opd (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    kode_opd            VARCHAR(22) UNIQUE NOT NULL,
    nama_opd            VARCHAR(255) NOT NULL,
    created_date        timestamp NOT NULL DEFAULT(NOW()),
    last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);