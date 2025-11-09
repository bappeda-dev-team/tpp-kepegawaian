package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response;

public record DetailTppResponse(
    Long id,
    String jenisTpp,
    Long maksimum,
    Float pajak,
    Float bpjs,
    Float totalPersen,
    Long totalTpp,
    Long totalPajak,
    Long totalBpjs,
    Long totalTerimaTpp
) {
}