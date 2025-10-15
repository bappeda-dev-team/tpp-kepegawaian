package cc.kertaskerja.tppkepegawaian.jabatan.domain.exception;

public class JabatanSudahAdaException extends RuntimeException {
	public JabatanSudahAdaException(Long id) {
        super("Jabatan dengan Id " + id + " sudah ada.");
    }
}
