
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.request;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record TppPerhitunganRequest(
        @Nullable
        Long tppPerhitunganId,
        
        @NotNull(message = "Jenis tpp harus terdefinisi")
        String jenisTpp,
        
        @Nullable
        String kodeOpd,
        
        @NotNull(message = "NIP harus terdefinisi")
        @NotEmpty(message = "NIP tidak boleh kosong")
        String nip,

        @Nullable
        String nama,

        @Nullable
        String kodePemda,

        @NotNull(message = "Bulan harus terdefinisi")
        @jakarta.validation.constraints.Min(value = 1, message = "Bulan minimal 1")
        @jakarta.validation.constraints.Max(value = 12, message = "Bulan maksimal 12")
        Integer bulan,

        @NotNull(message = "Tahun harus terdefinisi")
        @jakarta.validation.constraints.Min(value = 1900, message = "Tahun minimal 1900")
        @jakarta.validation.constraints.Max(value = 2100, message = "Tahun maksimal 2100")
        Integer tahun,

        @NotNull
        @jakarta.validation.constraints.Min(value = 0, message = "Nilai minimal 0")
        @jakarta.validation.constraints.Max(value = 100, message = "Nilai maksimum 100")
        Float maksimum,

        List<PerhitunganRequest> perhitungans
) {

}
