package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.bpjs.web;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.bpjs.domain.NamaBpjs;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BpjsRequest(
        @Nullable
        Long bpjsId,
        
        @NotNull(message = "Nip tidak boleh kosong")
        @NotEmpty(message = "Nip harus terdefinisi")
        String nip,
        
        @NotNull(message = "Nama bpjs tidak boleh kosong")
        NamaBpjs namaBpjs,
        
        @NotNull(message = "Komponen iuran tidak boleh kosong")
        @NotEmpty(message = "Komponen iuran harus terdefinisi")
        String komponenIuran,
        
        @NotNull(message = "Nilai bpjs tidak boleh kosong")
        Float nilaiBpjs
) {    
}
