package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception;

public class TppPerhitunganKodeOpdBulanTahunNotFoundException extends RuntimeException {
	public TppPerhitunganKodeOpdBulanTahunNotFoundException(String kodeOpd, Integer bulan, Integer tahun) {
		super("Data TPP Perhitungan untuk pegawai dengan kode opd " + kodeOpd + " bulan " + bulan + "  tahun " + tahun + " tidak ditemukan.");
	}
}
