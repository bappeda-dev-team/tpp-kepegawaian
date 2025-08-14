package cc.kertaskerja.tppkepegawaian.role.domain;

public class RoleSudahAdaException extends RuntimeException {
    public RoleSudahAdaException(Long id) {
        super("Level dengan Id " + id + " sudah ada.");
    }
}
