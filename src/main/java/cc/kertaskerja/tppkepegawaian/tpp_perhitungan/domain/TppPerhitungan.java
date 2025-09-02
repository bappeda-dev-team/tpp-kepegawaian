
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cc.kertaskerja.tppkepegawaian.tpp.web.formating.FormatingFloatSerializer;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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

        @Column("kode_pemda")
        String kodePemda,

        @Column("nama_perhitungan")
        String namaPerhitungan,

        @Column("nilai_perhitungan")
        String nilaiPerhitungan,

        @Column("maksimum")
        @JsonSerialize(using = FormatingFloatSerializer.class)
        Float maksimum,

        @Column("bulan")
        Integer bulan,

        @Column("tahun")
        Integer tahun,

        @Column("hasil_perhitungan")
        @JsonSerialize(using = FormatingFloatSerializer.class)
        Float hasilPerhitungan,

        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate
) {
    
    public static TppPerhitungan of(
            JenisTpp jenisTpp,
            String kodeOpd,
            String nip,
            String kodePemda,
            List<NamaPerhitungan> namaPerhitunganList,
            List<Float> nilaiPerhitunganList,
            Float maksimum,
            Integer bulan,
            Integer tahun,
            Float hasilPerhitungan
    ) {
        String namaPerhitunganString = namaPerhitunganList.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
                
        String nilaiPerhitunganString = nilaiPerhitunganList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
                
        return new TppPerhitungan(
                null,
                jenisTpp,
                kodeOpd,
                nip,
                kodePemda,
                namaPerhitunganString,
                nilaiPerhitunganString,
                maksimum,
                bulan,
                tahun,
                hasilPerhitungan,
                null,
                null
        );
    }
}
