package cc.kertaskerja.tppkepegawaian.jabatan.web;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public record JabatanWithPegawaiResponse(
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
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate tanggalMulai,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate tanggalAkhir) {
}
