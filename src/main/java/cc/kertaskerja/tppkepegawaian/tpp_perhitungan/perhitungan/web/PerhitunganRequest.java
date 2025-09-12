package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record PerhitunganRequest(
        @NotNull JenisTpp jenisTpp,
        @Nullable String kodeOpd,
        @NotNull String nip,
        @Nullable String nama,
        @Nullable String kodePemda,
        @NotNull String namaPerhitungan,
        @NotNull Integer bulan,
        @NotNull Integer tahun,
        @NotNull Float maksimum,
        @NotNull Float nilaiPerhitungan
) {

}
