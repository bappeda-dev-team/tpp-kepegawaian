package cc.kertaskerja.tppkepegawaian.pegawai.web;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.StatusPegawai;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PegawaiRequest(
		@Nullable
		Long pegawaiId,
		
		@NotNull(message = "Nama Pegawai harus terdefinisi")
		@NotEmpty(message = "Nama Pegawai tidak boleh kosong")
		String namaPegawai,
		
		@NotNull(message = "NIP tidak boleh kosong")
		@NotEmpty(message = "NIP tidak boleh kosong")
		@Pattern(
	                regexp = "^([0-9]{18})$",
	                message = "Format NIP harus valid."
	        )
		String nip,
		
		@NotNull(message = "Kode OPD tidak boleh kosong")
		@NotEmpty(message = "Kode OPD tidak boleh kosong")
		String kodeOpd,
		
		@NotNull(message = "Status Pegawai harus terdefinisi")
		StatusPegawai statusPegawai,
		
		@NotNull(message = "Password Hash harus terdefinisi")
		@NotEmpty(message = "Password Hash tidak boleh kosong")
		String passwordHash
) {
}
