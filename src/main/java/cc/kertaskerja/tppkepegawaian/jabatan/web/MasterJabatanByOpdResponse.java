package cc.kertaskerja.tppkepegawaian.jabatan.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.StatusJabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JenisJabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.Eselon;
import java.util.Date;
import java.util.List;

public record MasterJabatanByOpdResponse(
    String kodeOpd,
    String namaOpd,
    String nip,
    String namaPegawai,
    List<JabatanDetail> jabatan
) {
    public record JabatanDetail(
        Long id,
        String namaJabatan,
        StatusJabatan statusJabatan,
        JenisJabatan jenisJabatan,
        Eselon eselon,
        String pangkat,
        String golongan,
        @JsonFormat(pattern = "dd-MM-yyyy")
        Date tanggalMulai,
        @JsonFormat(pattern = "dd-MM-yyyy")
        Date tanggalAkhir
    ) {}
}