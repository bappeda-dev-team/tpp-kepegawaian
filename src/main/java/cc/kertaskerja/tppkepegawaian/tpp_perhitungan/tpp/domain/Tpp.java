package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain;

import java.text.NumberFormat;
import java.time.Instant;
import java.util.Locale;

import cc.kertaskerja.tppkepegawaian.domain.periode.HasId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import cc.kertaskerja.tppkepegawaian.domain.periode.HasPeriode;

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

        @LastModifiedDate Instant lastModifiedDate) implements HasPeriode, HasId {

    private static final String BASIC_TPP = "BASIC_TPP";
    private static final Float BASE_MAX_TPP = 0.0f;
    private static final Float BASE_PAJAK = 0.0f;
    private static final Float BASE_BPJS_1 = 0.01f;
    private static final Float BASE_BPJS_4 = 0.04f;

    private static Float nilaiBpjsDefault(Integer bulan) {
        if (bulan == null) {
            return 0.0f;
        }

        if (bulan == 13 || bulan == 14) {
            return 0.0f;
        }

        return 1.0f; // default multiplier
    }

    public static Float bpjs_1(Integer bulan) {
        return BASE_BPJS_1 * nilaiBpjsDefault(bulan);
    }

    public static Float bpjs_4(Integer bulan) {
        return BASE_BPJS_4 * nilaiBpjsDefault(bulan);
    }

    public String maksimumTppFormatted() {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.of("id", "ID"));
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return format.format(this.maksimumTpp);
    }

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
                bpjs_1(bulan),
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
                bpjs_1(bulan),
                bulan,
                tahun,
                null,
                null);
    }
}
