package cc.kertaskerja.tppkepegawaian.jabatan.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

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
        @JsonFormat(pattern = "dd-MM-yyyy") Date tanggalMulai,
        @JsonFormat(pattern = "dd-MM-yyyy") Date tanggalAkhir,
        Integer bulan,
        Integer tahun) {
}
