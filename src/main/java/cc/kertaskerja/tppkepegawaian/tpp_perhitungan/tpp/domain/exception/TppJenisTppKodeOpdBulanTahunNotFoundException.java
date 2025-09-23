package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.JenisTpp;

public class TppJenisTppKodeOpdBulanTahunNotFoundException extends RuntimeException {
	public TppJenisTppKodeOpdBulanTahunNotFoundException(JenisTpp jenisTpp, String kodeOpd, Integer bulan, Integer tahun) {
		super("Tpp dengan jenis Tpp " + jenisTpp + " kode opd " +  kodeOpd + " bulan " + bulan + " tahun " + tahun + " tidak ditemukan.");
	}
}
