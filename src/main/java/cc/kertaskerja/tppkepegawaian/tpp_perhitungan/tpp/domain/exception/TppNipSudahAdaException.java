package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception;

public class TppNipSudahAdaException extends RuntimeException {
    public TppNipSudahAdaException(String nip) {
        super("Tpp dengan NIP " + nip + " sudah ada.");
    }
}
