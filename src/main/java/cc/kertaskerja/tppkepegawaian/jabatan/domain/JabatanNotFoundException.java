package cc.kertaskerja.tppkepegawaian.jabatan.domain;

public class JabatanNotFoundException extends RuntimeException {
	public JabatanNotFoundException(String nip) {
		super("Jabatan dengan NIP " + nip + " tidak ditemukan.");
	}
}
