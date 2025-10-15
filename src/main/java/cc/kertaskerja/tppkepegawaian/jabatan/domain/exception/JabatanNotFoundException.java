package cc.kertaskerja.tppkepegawaian.jabatan.domain.exception;

public class JabatanNotFoundException extends RuntimeException {
	public JabatanNotFoundException(Long id) {
		super("Jabatan dengan Id " + id + " tidak ditemukan.");
	}
}
