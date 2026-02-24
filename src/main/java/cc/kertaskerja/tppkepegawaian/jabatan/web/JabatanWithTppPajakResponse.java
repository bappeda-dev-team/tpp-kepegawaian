package cc.kertaskerja.tppkepegawaian.jabatan.web;

import io.micrometer.common.lang.Nullable;

public record JabatanWithTppPajakResponse(
        Long id,
        String nip,
        String namaPegawai,
        String namaJabatan,
        String kodeOpd,
        String statusJabatan,
        String jenisJabatan,
        String eselon,
        String pangkat,
        String golongan,
        Float basicTpp,
        Float pajak,
        Integer bulanMulai,
        Integer tahunMulai,
        @Nullable Integer bulanBerakhir,
        @Nullable Integer tahunBerakhir,
        Integer bulan,
        Integer tahun) {
}
