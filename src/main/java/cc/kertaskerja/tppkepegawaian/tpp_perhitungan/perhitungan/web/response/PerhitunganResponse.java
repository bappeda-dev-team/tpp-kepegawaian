package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.response;

public record PerhitunganResponse(
        Long id,
        String namaPerhitungan,
        Float maksimum,
        Float nilaiPerhitungan
) {
}
