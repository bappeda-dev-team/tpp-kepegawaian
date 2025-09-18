package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response;

import java.util.List;

public record RekapTppResponse(
    Integer bulan,
    Integer tahun,
    String nip,
    List<DataTppResponse> data
) {
}