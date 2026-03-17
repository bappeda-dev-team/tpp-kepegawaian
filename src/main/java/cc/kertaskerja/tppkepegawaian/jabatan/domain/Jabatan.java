package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import cc.kertaskerja.tppkepegawaian.domain.periode.HasId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.annotation.JsonFormat;

import cc.kertaskerja.tppkepegawaian.domain.periode.HasPeriode;

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

        @LastModifiedDate Instant lastModifiedDate) implements HasPeriode, HasId {

    private static final Set<String> JENIS_JABATAN_KEPALA_SET = Set.of(
            "JABATAN_PEMIMPIN_TINGGI",
            "JABATAN_PEMIMPIN_TINGGI_PRATAMA");

    // buat comparator periode
    // ambil dari tanggalMulai
    @Override
    public Integer bulan() {
        return tanggalMulai != null ? tanggalMulai.getMonthValue() : null;
    }

    @Override
    public Integer tahun() {
        return tanggalMulai != null ? tanggalMulai.getYear() : null;
    }

    public boolean isKepalaAt(LocalDate tanggal) {
        return JENIS_JABATAN_KEPALA_SET.contains(this.jenisJabatan())
                && isActiveAt(tanggal);
    }

//    public boolean isKepala() {
//        return JENIS_JABATAN_KEPALA_SET.contains(this.jenisJabatan());
//    }

    public boolean isActiveAt(LocalDate tanggal) {

        if (tanggalMulai == null) {
            return false;
        }

        if (tanggalAkhir == null) {
            return !tanggal.isBefore(tanggalMulai);
        }

        return !tanggal.isBefore(tanggalMulai)
                && !tanggal.isAfter(tanggalAkhir);
    }

    // initiator
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
