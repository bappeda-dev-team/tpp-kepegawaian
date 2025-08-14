package cc.kertaskerja.tppkepegawaian.role.domain;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(Long id) {
	super("Level dengan Id " + id + " tidak ditemukan.");
    }
}
