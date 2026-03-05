package cc.kertaskerja.tppkepegawaian.npwp.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table(name = "npwp")
public record Npwp(
        @Id Long id,

        @Column("nip") String nip,

        @Column("npwp") String npwp,

        @Column("jenis_npwp") String jenisNpwp,

        @Column("status") String status,

        @CreatedDate Instant createdDate,

        @LastModifiedDate Instant lastModifiedDate) {

    private static final String DEFAULT_JENIS_NPWP = "NPWP_16";
    private static final String DEFAULT_STATUS = "AKTIF";

    public static Npwp of(
           String nip,
           String npwp,
           String jenisNpwp,
           String status
    ) {
        String defaultJenisNpwp =  (jenisNpwp == null || jenisNpwp.isBlank())
                ?
                DEFAULT_JENIS_NPWP
                :
                jenisNpwp;
        String defaultStatus =  (status == null || status.isBlank())
                ?
                DEFAULT_STATUS
                :
                status;

        return new Npwp(
                null,
                nip,
                npwp,
                defaultJenisNpwp,
                defaultStatus,
                null,
                null
        );
    }
}
