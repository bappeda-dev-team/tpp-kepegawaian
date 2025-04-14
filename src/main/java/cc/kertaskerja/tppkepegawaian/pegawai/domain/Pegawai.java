package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table(name = "pegawai")
public record Pegawai(
        @Id
        Long id,

        @NotBlank(message = "Nama pegawai wajib diisi.")
        String namaPegawai,

        @NotBlank(message = "NIP wajib diisi.")
        @Pattern(
                regexp = "^([0-9]{18})$",
                message = "Format NIP harus valid."
        )
        String nip,

        @NotBlank(message = "OPD pegawai wajib diisi.")
        String kodeOpd,

        @NotBlank(message = "Jabatan pegawai wajib diisi.")
        String kodeJabatan,

        StatusPegawai statusPegawai,
        RolePegawai rolePegawai,

        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate
) {
    public static Pegawai of(
            String namaPegawai, String nip,
            String kodeOpd, String kodeJabatan,
            StatusPegawai statusPegawai,
            RolePegawai rolePegawai
    ) {
        return new Pegawai(
                null, namaPegawai, nip,
                kodeOpd, kodeJabatan,
                statusPegawai, rolePegawai,
                null, null
        );
    }
}
