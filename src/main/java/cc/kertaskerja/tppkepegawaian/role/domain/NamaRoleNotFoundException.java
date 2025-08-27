package cc.kertaskerja.tppkepegawaian.role.domain;

public class NamaRoleNotFoundException extends RuntimeException {
    public NamaRoleNotFoundException(String namaRole) {
        super("Nama Role dengan Nama " + namaRole + " tidak ditemukan.");
    }
}
