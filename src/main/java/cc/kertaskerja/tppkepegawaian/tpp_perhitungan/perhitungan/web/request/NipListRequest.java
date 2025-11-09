package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record NipListRequest(
        @NotNull(message = "List NIP harus terdefinisi")
        @NotEmpty(message = "List NIP tidak boleh kosong")
        List<@NotEmpty(message = "NIP tidak boleh kosong") String> nip
) {
}