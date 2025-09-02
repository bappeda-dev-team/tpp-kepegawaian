package cc.kertaskerja.tppkepegawaian.tpp.domain;

public class TppNipSudahAdaException extends RuntimeException {
    public TppNipSudahAdaException(String nip) {
        super("Tpp dengan NIP " + nip + " sudah ada.");
    }
}
