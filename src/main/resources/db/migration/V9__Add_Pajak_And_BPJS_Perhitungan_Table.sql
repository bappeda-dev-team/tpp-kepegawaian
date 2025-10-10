CREATE TABLE pajak (
	id                  BIGSERIAL PRIMARY KEY NOT NULL,
    nip                 VARCHAR(18) UNIQUE NOT NULL,
    nama_pajak          VARCHAR(100) NOT NULL,
    dasar_hukum         VARCHAR(50) NOT NULL,
    komponen_pajak      VARCHAR(50) NOT NULL,
    nilai_pajak         FLOAT(50) NOT NULL,
    created_date        timestamp NOT NULL DEFAULT(NOW()),
    last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);

CREATE TABLE bpjs (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    nip                 VARCHAR(18) UNIQUE NOT NULL,
    nama_bpjs           VARCHAR(50) NOT NULL,
    komponen_iuran      VARCHAR(50) NOT NULL,
    nilai_bpjs          FLOAT(50) NOT NULL,
    created_date        timestamp NOT NULL DEFAULT(NOW()),
    last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);
