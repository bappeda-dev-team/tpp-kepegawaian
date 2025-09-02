CREATE TABLE tpp_perhitungan (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    jenis_tpp           VARCHAR(255) NOT NULL DEFAULT('BELUM_DIATUR'),
    kode_opd            VARCHAR(22),
    nip                 VARCHAR(18) NOT NULL,
    kode_pemda          VARCHAR(22),
    nama_perhitungan    TEXT,
    nilai_perhitungan   TEXT,
    maksimum            FLOAT(20),
    bulan               INTEGER NOT NULL,
    tahun               INTEGER NOT NULL,
    hasil_perhitungan   FLOAT(20),
    created_date        timestamp NOT NULL DEFAULT(NOW()),
    last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);

CREATE TABLE tpp (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    jenis_tpp           VARCHAR(255) NOT NULL DEFAULT('BELUM_DIATUR'),
    kode_opd            VARCHAR(22),
    nip                 VARCHAR(18) NOT NULL,
    kode_pemda          VARCHAR(22),
    keterangan          VARCHAR(255) NOT NULL,
    nilai_input         FLOAT(20),
    maksimum            FLOAT(20),
    hasil_perhitungan   FLOAT(20),
    bulan               INTEGER NOT NULL,
    tahun               INTEGER NOT NULL,
    total_tpp           FLOAT(20),
    created_date        timestamp NOT NULL DEFAULT(NOW()),
    last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);