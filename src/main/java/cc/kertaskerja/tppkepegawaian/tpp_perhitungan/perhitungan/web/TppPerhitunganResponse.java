package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web;

import java.util.List;

public record TppPerhitunganResponse(
        String jenisTpp,
        String kodeOpd,
        String nip,
        String nama,
        String kodePemda,
        Float maksimum,
        Integer bulan,
        Integer tahun,
        List<PerhitunganResponse> perhitungans,
        Float totalPersen
) {
}
