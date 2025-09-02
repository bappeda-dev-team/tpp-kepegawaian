package cc.kertaskerja.tppkepegawaian.tpp.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cc.kertaskerja.tppkepegawaian.tpp.web.formating.FormatingFloatSerializer;

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
        
        @Column("keterangan")
        String keterangan,
        
        @Column("nilai_input")
        @JsonSerialize(using = FormatingFloatSerializer.class)
        Float nilaiInput,
        
        @Column("maksimum")
        @JsonSerialize(using = FormatingFloatSerializer.class)
        Float maksimum,

        @Column("hasil_perhitungan")
        @JsonSerialize(using = FormatingFloatSerializer.class)
        Float hasilPerhitungan,

        @Column("bulan")
        Integer bulan,
        
        @Column("tahun")
        Integer tahun,
        
        @Column("total_tpp")
        @JsonSerialize(using = FormatingFloatSerializer.class)
        Float totalTpp,
        
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
            String keterangan,
            Float nilaiInput,
            Float maksimum,
            Float hasilPerhitungan,
            Integer bulan,
            Integer tahun,
            Float totalTpp 
    ) {
        return new Tpp(
                null,
                jenisTpp,
                kodeOpd, 
                nip,
                kodePemda, 
                keterangan,
                nilaiInput,
                maksimum,
                hasilPerhitungan,
                bulan,
                tahun,
                totalTpp,
                null, 
                null
        );
    }
}
