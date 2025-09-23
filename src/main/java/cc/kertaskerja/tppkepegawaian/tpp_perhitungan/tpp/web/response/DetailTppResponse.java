package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response;

public record DetailTppResponse(
    Long id,
    String jenisTpp,
    Long maksimum,
    Float totalPersen,
    Long totalTpp
) {
}