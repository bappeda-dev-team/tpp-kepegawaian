package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.bpjs.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "bpjs")
public record Bpjs(
    @Id
    Long id,

    @Column("nip")
    String nip,

    @Column("nama_bpjs")
    NamaBpjs namaBpjs,
    
    @Column("komponen_iuran")
    String komponenIuran,
    
    @Column("nilai_bpjs")
    Float nilaiBpjs,

    @CreatedDate
    Instant createdDate,

    @LastModifiedDate
    Instant lastModifiedDate
) {
    public static Bpjs of (
        String nip,
        NamaBpjs namaBpjs,
        String komponenIuran,
        Float nilaiBpjs
    ) {
        return new Bpjs(
            null,
            nip,
            namaBpjs,
            komponenIuran,
            nilaiBpjs,
            null,
            null
        );
    }
}
