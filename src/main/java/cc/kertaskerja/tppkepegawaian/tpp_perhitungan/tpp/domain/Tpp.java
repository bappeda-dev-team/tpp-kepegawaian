package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp;

@Table(name = "tpp")
public record Tpp(
        @Id
        Long id,
        
        @Column("jenis_tpp")
        JenisTpp jenisTpp,
        
        @Column("kode_opd")
        String kodeOpd,

        @Column("nip")
        String nip,

        @Column("kode_pemda")
        String kodePemda,

        @Column("maksimum_tpp")
        Float maksimumTpp,

        @Column("bulan")
        Integer bulan,

        @Column("tahun")
        Integer tahun,
        
        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate
) {
    public static Tpp of(
            JenisTpp jenisTpp,
            String kodeOpd,
            String nip,
            String kodePemda,
            Float maksimumTpp,
            Integer bulan,
            Integer tahun
    ) {
        return new Tpp(
                null,
                jenisTpp,
                kodeOpd,
                nip,
                kodePemda,
                maksimumTpp,
                bulan,
                tahun,
                null,
                null
        );
    }
}
