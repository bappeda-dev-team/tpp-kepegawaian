CREATE TABLE rekening_pegawai (
    id   BIGSERIAL PRIMARY KEY NOT NULL,
    nip  VARCHAR(30) NOT NULL,
    nomor_rekening VARCHAR(255),
    nama_bank VARCHAR(255),
    nama_pemilik VARCHAR(255),
    status			VARCHAR(20) NOT NULL DEFAULT('AKTIF'),
    created_date        timestamp NOT NULL DEFAULT(NOW()),
    last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);