package cc.kertaskerja.tppkepegawaian.jabatan.web;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
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
		String statusJabatan,
		
		@NotNull(message = "Pilih jenis jabatan")
		String jenisJabatan,
		
		@NotNull(message = "Pilih eselon")
		String eselon,
		
		@NotNull(message = "Pangkat harus terdefinisi")
		@NotEmpty(message = "Pangkat tidak boleh kosong")
		String pangkat,
		
		@NotNull(message = "Golongan harus terdefinisi")
		@NotEmpty(message = "Golongan tidak boleh kosong")
		String golongan,
		
		@NotNull(message = "Tanggal mulai harus terdefinisi")
		@JsonFormat(pattern = "dd-MM-yyyy")
		Date tanggalMulai,
		
		@JsonFormat(pattern = "dd-MM-yyyy")
		Date tanggalAkhir
) {
}
