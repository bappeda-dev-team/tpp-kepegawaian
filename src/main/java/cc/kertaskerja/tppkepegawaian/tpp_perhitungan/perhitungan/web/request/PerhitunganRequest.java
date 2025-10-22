package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.request;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.NamaPerhitungan;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record PerhitunganRequest(
        @NotNull JenisTpp jenisTpp,
        @Nullable String kodeOpd,
        @NotNull String nip,
        @Nullable String nama,
        @Nullable String kodePemda,
        @NotNull NamaPerhitungan namaPerhitungan,
        @NotNull Integer bulan,
        @NotNull Integer tahun,
        @NotNull Float maksimum,
        @NotNull Float nilaiPerhitungan
) {

}
