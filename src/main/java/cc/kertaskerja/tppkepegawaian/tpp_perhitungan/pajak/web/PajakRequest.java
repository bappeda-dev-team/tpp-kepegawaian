package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.pajak.web;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PajakRequest(
        @Nullable
        Long pajakId,

        @NotNull(message = "Nip tidak boleh kosong")
        @NotEmpty(message = "Nip harus terdefinisi")
        String nip,

        @NotNull(message = "Nama pajak tidak boleh kosong")
        @NotEmpty(message = "Nama pajak harus terdefinisi")
        String namaPajak,

        @NotNull(message = "Dasar hukum tidak boleh kosong")
        @NotEmpty(message = "Dasar hukum harus terdefinisi")
        String dasarHukum,

        @NotNull(message = "Komponen pajak tidak boleh kosong")
        @NotEmpty(message = "Komponen pajak harus terdefinisi")
        String komponenPajak,

        @NotNull(message = "Nilai pajak tidak boleh kosong")
        Float nilaiPajak
) {
    
}
