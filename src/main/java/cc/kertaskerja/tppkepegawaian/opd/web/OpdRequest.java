package cc.kertaskerja.tppkepegawaian.opd.web;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OpdRequest(
		@Nullable
		Long opdId,
		
		@NotNull(message = "Kode OPD tidak boleh kosong")
		@NotEmpty(message = "kode OPD tidak boleh kosong")
		String kodeOpd,
		
		@NotNull(message = "Nama OPD harus terdefinisi")
		@NotEmpty(message = "Nama OPD tidak boleh kosong")
		String namaOpd
) {

}
