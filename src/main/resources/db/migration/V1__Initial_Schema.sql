CREATE TABLE pegawai (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    nama_pegawai        VARCHAR(255) NOT NULL,
    nip                 VARCHAR(18) UNIQUE NOT NULL,
    kode_opd            VARCHAR(22) NOT NULL,
    nama_role           VARCHAR(50) NOT NULL,
    status_pegawai      VARCHAR(20) NOT NULL DEFAULT('AKTIF'),
    password_hash       VARCHAR(100) NOT NULL,
    created_date        timestamp NOT NULL DEFAULT(NOW()),
    last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);