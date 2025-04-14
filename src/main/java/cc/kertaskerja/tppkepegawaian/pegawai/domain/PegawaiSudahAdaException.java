package cc.kertaskerja.tppkepegawaian.pegawai.domain;

public class PegawaiSudahAdaException extends RuntimeException {
    public PegawaiSudahAdaException(String nip) {
        super("Pegawai dengan NIP " + nip + " sudah ada.");
    }
}
