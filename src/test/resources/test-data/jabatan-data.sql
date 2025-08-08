INSERT INTO opd (id, kode_opd, nama_opd, created_date, last_modified_date) VALUES
(1, 'OPD-001', 'Badan Perencanaan Pembangunan Daerah', NOW(), NOW()),
(2, 'OPD-002', 'Dinas Pendidikan', NOW(), NOW());

INSERT INTO jabatan (id, nip, nama_jabatan, kode_opd, status_jabatan, jenis_jabatan, eselon, tanggal_mulai, tanggal_akhir, created_date, last_modified_date) VALUES
(1, '1234567890123456', 'Analis Kebijakan Ahli Muda', 'OPD-001', 'UTAMA', 'JABATAN_FUNGSIONAL', 'ESELON_II', '2024-01-01', '2024-12-31', NOW(), NOW()),
(2, '2345678901234567', 'Perencana Ahli Muda', 'OPD-002', 'UTAMA', 'JABATAN_FUNGSIONAL', 'ESELON_III', '2024-01-01', '2024-12-31', NOW(), NOW());