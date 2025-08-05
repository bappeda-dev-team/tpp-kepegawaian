ALTER TABLE jabatan
ADD CONSTRAINT fk_jabatan_opd
FOREIGN KEY (kode_opd)
REFERENCES opd(kode_opd);
