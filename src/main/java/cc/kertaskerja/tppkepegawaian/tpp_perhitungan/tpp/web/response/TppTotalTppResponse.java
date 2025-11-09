package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response;

public record TppTotalTppResponse(
    String jenisTpp,
    String kodeOpd,
    String nip,
    String kodePemda,
    Float maksimumTpp,
    Float pajak,
    Float bpjs,
    Integer bulan,
    Integer tahun,
    Float hasilPerhitungan,
    Float totaltpp,
    Float totalPajak,
    Float totalBpjs,
    Float totalTerimaTpp
) {
}
