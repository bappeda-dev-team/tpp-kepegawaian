package cc.kertaskerja.tppkepegawaian.opd.domain;

public class OpdNotFoundException extends RuntimeException {
	public OpdNotFoundException(String kodeOpd) {
		super("Opd dengan Kode OPD " + kodeOpd + " tidak ditemukan.");
	}
}
