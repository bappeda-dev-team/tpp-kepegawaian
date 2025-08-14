Create TABLE role (
    id                  BIGSERIAL PRIMARY KEY NOT NULL,
    nama_role           VARCHAR(50) NOT NULL,
    nip            		VARCHAR(20) NOT NULL,
    level_role			VARCHAR(20) NOT NULL DEFAULT('KOSONG'),
    is_active			VARCHAR(20) NOT NULL DEFAULT('AKTIF'),
    created_date        timestamp NOT NULL DEFAULT(NOW()),
    last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);