
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.JenisTpp;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record TppRequest(
        @Nullable
        Long tppId,
        
        @NotNull(message = "Jenis tpp harus dipilih")
        JenisTpp jenisTpp,
        
        @Nullable
        String kodeOpd,
        
        @NotNull(message = "NIP harus terdefinisi")
        @NotEmpty(message = "NIP tidak boleh kosong")
        String nip,

        @Nullable
        String kodePemda,

        @NotNull(message = "Maksimum TPP harus terdefinisi")
        @jakarta.validation.constraints.Positive(message = "Maksimum TPP harus positif")
        @jakarta.validation.constraints.Max(value = 10000000, message = "Nilai maksimum lebih dari 10 juta")
        Float maksimumTpp,

        @NotNull
        Integer bulan,

        @NotNull
        Integer tahun

) {
}
