package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response;

import java.util.List;

public record DataTppResponse(
    Long id,
    String nama,
    String opd,
    Long totalPerolehan,
    List<DetailTppResponse> detailTpp
) {
}