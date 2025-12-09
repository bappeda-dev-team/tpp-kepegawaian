package cc.kertaskerja.tppkepegawaian.pegawai.web.response;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;

import java.time.Instant;

public record PegawaiResponse(
        Long id,
        String namaPegawai,
        String nip,
        String kodeOpd,
        String namaRole,
        String statusPegawai,
        Instant createdDate,
        Instant lastModifiedDate
) {
    public static PegawaiResponse from(Pegawai pegawai) {
        return new PegawaiResponse(
                pegawai.id(),
                pegawai.namaPegawai(),
                pegawai.nip(),
                pegawai.kodeOpd(),
                pegawai.namaRole(),
                pegawai.statusPegawai(),
                pegawai.createdDate(),
                pegawai.lastModifiedDate()
        );
    }
}
