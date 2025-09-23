package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception;

public class TppPerhitunganKodeOpdBulanTahunSudahAdaException extends RuntimeException {
	public TppPerhitunganKodeOpdBulanTahunSudahAdaException(String kodeOpd, Integer bulan, Integer tahun ) {
		super("Tpp Perhitungan dengan kode opd " + kodeOpd + " bulan " + bulan + " tahun " + tahun + " sudah ada .");
	}
}
