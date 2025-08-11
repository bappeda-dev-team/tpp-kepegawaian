package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table(name = "pegawai")
public record Pegawai(
        @Id
        Long id,

        @Column("nama_pegawai")
        String namaPegawai,
        
        @Column("nip")
        String nip,

        @Column("kode_opd")
        String kodeOpd,
        
        @Column("status_pegawai")
        StatusPegawai statusPegawai,

        @Column("password_hash")
        String passwordHash,

        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate
) {
    public static Pegawai of(
            String namaPegawai, 
            String nip,
            String kodeOpd,
            StatusPegawai statusPegawai,
            String passwordHash
    ) {
        return new Pegawai(
                null, 
                namaPegawai, 
                nip,
                kodeOpd, 
                statusPegawai,
                passwordHash,
                null, 
                null
        );
    }
}
