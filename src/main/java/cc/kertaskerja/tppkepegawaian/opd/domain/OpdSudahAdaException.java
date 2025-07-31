package cc.kertaskerja.tppkepegawaian.opd.domain;

public class OpdSudahAdaException extends RuntimeException {
	public OpdSudahAdaException(String kodeOpd) {
		super("OPD dengan kode " + kodeOpd + " sudah ada.");
	}

}
