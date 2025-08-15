package cc.kertaskerja.tppkepegawaian.opd.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "opd")
public record Opd(
        @Id
        Long id,
        
        @Column("kode_opd")
        String kodeOpd,

        @Column("nama_opd")
        String namaOpd,
        
        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate
) {
    public static Opd of(
            String kodeOpd,
            String namaOpd 
    ) {
        return new Opd(
                null, 
                kodeOpd,
                namaOpd,
                null, 
                null
        );
    }
}
