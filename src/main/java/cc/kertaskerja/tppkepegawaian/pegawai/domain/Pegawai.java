package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

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
        
        @Column("nama_role")
        @Nullable
        String namaRole,
        
        @Column("status_pegawai")
        @Nullable
        String statusPegawai,

        @Column("password_hash")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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
            String namaRole,
            String statusPegawai,
            String passwordHash
    ) {
        return new Pegawai(
                null, 
                namaPegawai, 
                nip,
                kodeOpd,
                namaRole,
                statusPegawai,
                passwordHash,
                null,
                null
        );
    }
}