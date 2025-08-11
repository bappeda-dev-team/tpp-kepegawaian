INSERT INTO opd (id, kode_opd, nama_opd, created_date, last_modified_date) VALUES
(1, 'OPD-001', 'Badan Perencanaan Pembangunan Daerah', NOW(), NOW()),
(2, 'OPD-002', 'Dinas Pendidikan', NOW(), NOW());

INSERT INTO pegawai (id, nama_pegawai, nip, kode_opd, status_pegawai, password_hash, created_date, last_modified_date) VALUES
(1, 'John Doe', '123456789012345678', 'OPD-001', 'AKTIF', 'hashedpassword123', NOW(), NOW()),
(2, 'Jane Smith', '234567890123456789', 'OPD-002', 'AKTIF', 'hashedpassword456', NOW(), NOW());