package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.pajak.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "pajak")
public record Pajak(
    @Id
    Long id,

    @Column("nip")
    String nip,

    @Column("nama_pajak")
    String namaPajak,

    @Column("dasar_hukum")
    String dasarHukum,

    @Column("komponen_pajak")
    String komponenPajak,

    @Column("nilai_pajak")
    Float nilaiPajak,

    @CreatedDate
    Instant createdDate,

    @LastModifiedDate
    Instant lastModifiedDate
) {
    public static Pajak of(
            String nip,
            String namaPajak,
            String dasarHukum,
            String komponenPajak,
            Float nilaiPajak
    ) {
        return new Pajak(
            null,
            nip,
            namaPajak,
            dasarHukum,
            komponenPajak,
            nilaiPajak,
            null,
            null
        );
    }
}
