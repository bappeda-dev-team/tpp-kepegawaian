package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response;

public record TppTotalTppResponse(
    String jenisTpp,
    String kodeOpd,
    String nip,
    String kodePemda,
    Float maksimumTpp,
    Integer bulan,
    Integer tahun,
    Float hasilPerhitungan,
    Float totaltpp
) {
}
