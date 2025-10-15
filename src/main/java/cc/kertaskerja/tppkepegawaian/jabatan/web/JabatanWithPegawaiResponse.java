package cc.kertaskerja.tppkepegawaian.jabatan.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.StatusJabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JenisJabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.Eselon;
import java.util.Date;

public record JabatanWithPegawaiResponse(
    Long id,
    String nip,
    String namaPegawai,
    String namaJabatan,
    String kodeOpd,
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
