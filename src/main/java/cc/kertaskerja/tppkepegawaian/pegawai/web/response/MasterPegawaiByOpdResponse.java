package cc.kertaskerja.tppkepegawaian.pegawai.web.response;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.Eselon;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JenisJabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.StatusJabatan;
import cc.kertaskerja.tppkepegawaian.role.domain.IsActive;

import java.util.List;

public record MasterPegawaiByOpdResponse(
        String kodeOpd,
        String namaOPD,
        List<PegawaiItem> pegawai
) {
    public record PegawaiItem(
            Long id,
            String namaPegawai,
            String nip,
            String namaJabatan,
            StatusJabatan statusJabatan,
            JenisJabatan jenisJabatan,
            Eselon eselon,
            String pangkat,
            String golongan,
            String namaRole,
            IsActive isActive
    ) {}
}