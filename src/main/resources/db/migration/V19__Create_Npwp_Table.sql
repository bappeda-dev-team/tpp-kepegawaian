CREATE TABLE npwp (
  id   BIGSERIAL PRIMARY KEY NOT NULL,
  nip  VARCHAR(30) NOT NULL,
  npwp VARCHAR(18) NOT NULL,
  jenis_npwp VARCHAR(30) NOT NULL,
  status			VARCHAR(20) NOT NULL DEFAULT('AKTIF'),
  created_date        timestamp NOT NULL DEFAULT(NOW()),
  last_modified_date  timestamp NOT NULL DEFAULT(NOW())
);
