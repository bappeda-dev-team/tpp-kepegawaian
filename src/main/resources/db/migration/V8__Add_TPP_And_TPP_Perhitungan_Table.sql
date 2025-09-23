CREATE TABLE tpp_perhitungan (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    jenis_tpp           VARCHAR(50) NOT NULL DEFAULT('BELUM_DIATUR'),
    kode_opd            VARCHAR(30),
    nip                 VARCHAR(30) NOT NULL,
    nama                VARCHAR(255),
    bulan               INTEGER NOT NULL,
    tahun               INTEGER NOT NULL,
    maksimum            FLOAT(50) NOT NULL,
    nama_perhitungan    VARCHAR(255) NOT NULL,
    nilai_perhitungan   FLOAT(50) NOT NULL,
    created_date        timestamp NOT NULL DEFAULT(NOW()),
    last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);

CREATE TABLE tpp (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    jenis_tpp           VARCHAR(50) NOT NULL DEFAULT('BELUM_DIATUR'),
    kode_opd            VARCHAR(30),
    kode_pemda          VARCHAR(30),
    nip                 VARCHAR(30) NOT NULL,
    maksimum_tpp        FLOAT(50) NOT NULL,
    bulan               INTEGER NOT NULL,
    tahun               INTEGER NOT NULL,
    created_date        timestamp NOT NULL DEFAULT(NOW()),
    last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);