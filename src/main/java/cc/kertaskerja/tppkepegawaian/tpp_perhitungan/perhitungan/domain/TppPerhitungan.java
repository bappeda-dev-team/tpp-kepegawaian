
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table(name = "tpp_perhitungan")
public record TppPerhitungan(
        @Id
        Long id,

        @Column("jenis_tpp")
        JenisTpp jenisTpp,

        @Column("kode_opd")
        String kodeOpd,

        @Column("nip")
        String nip,

        @Column("nama")
        String nama,

        @Column("bulan")
        Integer bulan,

        @Column("tahun")
        Integer tahun,

        @Column("maksimum")
        Float maksimum,

        @Column("nama_perhitungan")
        String namaPerhitungan,

        @Column("nilai_perhitungan")
        Float nilaiPerhitungan,

        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate
) {
    
    public static TppPerhitungan of(
            JenisTpp jenisTpp,
            String kodeOpd,
            String nip,
            String nama,
            Integer bulan,
            Integer tahun,
            Float maksimum,
            String namaPerhitungan,
            Float nilaiPerhitungan
    ) {
                
        return new TppPerhitungan(
                null,
                jenisTpp,
                kodeOpd,
                nip,
                nama,
                bulan,
                tahun,
                maksimum,
                namaPerhitungan,
                nilaiPerhitungan,
                null,
                null
        );
    }
}
