package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.response;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.NamaPerhitungan;

public record PerhitunganResponse(
        NamaPerhitungan namaPerhitungan,
        Float maksimum,
        Float nilaiPerhitungan
) {
}
