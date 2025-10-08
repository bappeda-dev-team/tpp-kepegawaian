package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.pajak.domain;

public class PajakSudahAdaException extends RuntimeException {
    public PajakSudahAdaException(String nip) {
        super("Pajak dengan nip " + nip + " sudah ada.");
    }
}
