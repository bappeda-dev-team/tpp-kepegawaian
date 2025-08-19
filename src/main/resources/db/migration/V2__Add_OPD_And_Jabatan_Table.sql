Create TABLE opd (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    kode_opd            VARCHAR(22) UNIQUE NOT NULL,
    nama_opd            VARCHAR(255) NOT NULL,
    created_date        timestamp NOT NULL DEFAULT(NOW()),
    last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);

CREATE TABLE jabatan (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    nip                	VARCHAR(18)  NOT NULL,
    nama_jabatan        VARCHAR(255) NOT NULL,
    kode_opd            VARCHAR(22)  NOT NULL,
    status_jabatan      VARCHAR(20)  NOT NULL DEFAULT('UTAMA'),
    jenis_jabatan       VARCHAR(20)  NOT NULL DEFAULT('BELUM_DIATUR'),
    eselon				VARCHAR(20)  NOT NULL DEFAULT('NON_ESELON'),
    pangkat             VARCHAR(20)  NOT NULL,
    golongan            VARCHAR(20)  NOT NULL,
    tanggal_mulai       DATE NOT NULL,
    tanggal_akhir       DATE,
    created_date        timestamp NOT NULL DEFAULT(NOW()),
    last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);