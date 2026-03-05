package cc.kertaskerja.tppkepegawaian.rekening.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table(name = "rekening_pegawai")
public record RekeningPegawai(
        @Id Long id,

        @Column("nip") String nip,

        @Column("nomor_rekening") String nomorRekening,

        @Column("nama_bank") String namaBank,

        @Column("nama_pemilik") String namaPemilik,

        @Column("status") String status,

        @CreatedDate Instant createdDate,

        @LastModifiedDate Instant lastModifiedDate) {

    private static final String NAMA_BANK_DEFAULT = "BANK-BELUM-TERISI";
    private static final String NON_AKTIF = "NON_AKTIF";

    public static RekeningPegawai of(
            String nip,
            String nomorRekening,
            String namaBank,
            String namaPemilik,
            String status) {

        String bank = (namaBank == null || namaBank.isBlank())
                ? NAMA_BANK_DEFAULT
                : namaBank;

        String defaultStatus = (status == null || status.isBlank())
                ? NON_AKTIF
                : status;

        return new RekeningPegawai(
                null,
                nip,
                nomorRekening,
                bank,
                namaPemilik,
                defaultStatus,
                null,
                null
        );
    }

    public static RekeningPegawai NonAktif(
            String nip,
            String nomorRekening,
            String namaBank,
            String namaPemilik) {
        return new RekeningPegawai(null,
                nip,
                nomorRekening,
                namaBank,
                namaPemilik,
                NON_AKTIF,
                null,
                null);
    }
}
