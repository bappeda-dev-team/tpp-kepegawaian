package cc.kertaskerja.tppkepegawaian.jabatan.web;

import java.util.Date;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.Eselon;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JenisJabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.StatusJabatan;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record JabatanRequest(
		@Nullable
		Long jabatanId,
		
		@NotNull(message = "NIP tidak boleh kosong")
		@NotEmpty(message = "NIP tidak boleh kosong")
		String nip,
		
		@NotNull(message = "Nama jabatan harus terdefinisi")
		@NotEmpty(message = "Nama jabatan tidak boleh kosong")
		String namaJabatan,
		
		@NotNull(message = "Kode OPD tidak boleh kosong")
		@NotEmpty(message = "Kode OPD tidak boleh kosong")
		String kodeOpd,
		
		@NotNull(message = "Pilih status jabatan")
		StatusJabatan statusJabatan,
		
		@NotNull(message = "Pilih jenis jabatan")
		JenisJabatan jenisJabatan,
		
		@NotNull(message = "Pilih eselon")
		Eselon eselon,
		
		@NotNull(message = "Tanggal mulai harus terdefinisi")
		Date tanggalMulai,
		
		Date tanggalBerakhir
) {
}
