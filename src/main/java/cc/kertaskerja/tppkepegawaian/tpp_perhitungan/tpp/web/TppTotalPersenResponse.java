package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.Tpp;

public record TppTotalPersenResponse(
    Tpp tpp,
    Float totalPersen
) {
}
