package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception;

public class TppJenisTppKodeOpdBulanTahunNotFoundException extends RuntimeException {
	public TppJenisTppKodeOpdBulanTahunNotFoundException(String jenisTpp, String kodeOpd, Integer bulan, Integer tahun) {
		super("Tpp dengan jenis Tpp " + jenisTpp + " kode opd " +  kodeOpd + " bulan " + bulan + " tahun " + tahun + " tidak ditemukan.");
	}
}
