CREATE TABLE pegawai (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    nama_pegawai        VARCHAR(255) NOT NULL,
    nip                 VARCHAR(18) UNIQUE NOT NULL,
    kode_opd            VARCHAR(22) NOT NULL,
    kode_jabatan        VARCHAR(10) NOT NULL,
    status_pegawai      VARCHAR(20) NOT NULL,
    role_pegawai        VARCHAR(20) NOT NULL,
    created_date        timestamp NOT NULL,
    last_modified_date  timestamp NOT NULL
);