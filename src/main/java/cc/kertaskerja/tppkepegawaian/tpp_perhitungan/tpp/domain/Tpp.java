package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "tpp")
public record Tpp(
        @Id Long id,

        @Column("jenis_tpp") String jenisTpp,

        @Column("kode_opd") String kodeOpd,

        @Column("nip") String nip,

        @Column("kode_pemda") String kodePemda,

        @Column("maksimum_tpp") Float maksimumTpp,

        @Column("pajak") Float pajak,

        @Column("bpjs") Float bpjs,

        @Column("bulan") Integer bulan,

        @Column("tahun") Integer tahun,

        @CreatedDate Instant createdDate,

        @LastModifiedDate Instant lastModifiedDate) {

    private static final String BASIC_TPP = "BASIC_TPP";
    private static final Float BASE_MAX_TPP = 0.0f;
    private static final Float BASE_PAJAK = 0.0f;
    private static final Float BASE_BPJS = 0.01f;

    public static Tpp of(
            String jenisTpp,
            String kodeOpd,
            String nip,
            String kodePemda,
            Float maksimumTpp,
            Float pajak,
            Float bpjs,
            Integer bulan,
            Integer tahun) {
        return new Tpp(
                null,
                jenisTpp,
                kodeOpd,
                nip,
                kodePemda,
                maksimumTpp,
                pajak,
                bpjs,
                bulan,
                tahun,
                null,
                null);
    }

    public Tpp updateFrom(Tpp other) {
        return new Tpp(
                this.id(),
                this.jenisTpp(),
                other.kodeOpd(),
                this.nip(),
                other.kodePemda(),
                other.maksimumTpp(),
                other.pajak(),
                other.bpjs(),
                this.bulan(),
                this.tahun(),
                this.createdDate(),
                Instant.now());
    }

    public static Tpp basicTpp(
            String kodeOpd,
            String nip,
            String kodePemda,
            Float maxTpp,
            Float pajak,
            Integer bulan,
            Integer tahun) {
        return new Tpp(
                null,
                BASIC_TPP,
                kodeOpd,
                nip,
                kodePemda,
                maxTpp,
                pajak,
                BASE_BPJS,
                bulan,
                tahun,
                null,
                null);
    }

    public static Tpp zero(
            String jenisTpp,
            String kodeOpd,
            String nip,
            String kodePemda,
            Integer bulan,
            Integer tahun) {
        return new Tpp(
                null,
                jenisTpp,
                kodeOpd,
                nip,
                kodePemda,
                BASE_MAX_TPP,
                BASE_PAJAK,
                BASE_BPJS,
                bulan,
                tahun,
                null,
                null);
    }
}
