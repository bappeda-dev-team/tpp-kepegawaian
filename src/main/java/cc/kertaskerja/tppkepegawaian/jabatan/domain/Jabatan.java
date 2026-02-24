package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.annotation.JsonFormat;

@Table(name = "jabatan")
public record Jabatan(
        @Id Long id,

        @Column("nip") String nip,

        @Column("nama_pegawai") String namaPegawai,

        @Column("nama_jabatan") String namaJabatan,

        @Column("kode_opd") String kodeOpd,

        @Column("status_jabatan") String statusJabatan,

        @Column("jenis_jabatan") String jenisJabatan,

        @Column("eselon") String eselon,

        @Column("pangkat") String pangkat,

        @Column("golongan") String golongan,

        @Column("basic_tpp") Float basicTpp,

        @Column("tanggal_mulai") @JsonFormat(pattern = "dd-MM-yyyy") LocalDate tanggalMulai,

        @Column("tanggal_akhir") @JsonFormat(pattern = "dd-MM-yyyy") LocalDate tanggalAkhir,

        @CreatedDate Instant createdDate,

        @LastModifiedDate Instant lastModifiedDate) {
    public static Jabatan of(
            String nip,
            String namaPegawai,
            String namaJabatan,
            String kodeOpd,
            String statusJabatan,
            String jenisJabatan,
            String eselon,
            String pangkat,
            String golongan,
            Float basicTpp,
            LocalDate tanggalMulai,
            LocalDate tanggalAkhir) {
        return new Jabatan(
                null,
                nip,
                namaPegawai,
                namaJabatan,
                kodeOpd,
                statusJabatan,
                jenisJabatan,
                eselon,
                pangkat,
                golongan,
                basicTpp,
                tanggalMulai,
                tanggalAkhir,
                null,
                null);
    }

    public Jabatan withId(Long newId) {
        return new Jabatan(
                newId,
                nip,
                namaPegawai,
                namaJabatan,
                kodeOpd,
                statusJabatan,
                jenisJabatan,
                eselon,
                pangkat,
                golongan,
                basicTpp,
                tanggalMulai,
                tanggalAkhir,
                createdDate,
                lastModifiedDate);
    }
}
