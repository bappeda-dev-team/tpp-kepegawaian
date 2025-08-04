package cc.kertaskerja.tppkepegawaian.jabatan.domain;

public class JabatanSudahAdaException extends RuntimeException {
	public JabatanSudahAdaException(String nip) {
        super("Jabatan dengan NIP " + nip + " sudah ada.");
    }
}
