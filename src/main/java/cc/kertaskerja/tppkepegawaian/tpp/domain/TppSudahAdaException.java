package cc.kertaskerja.tppkepegawaian.tpp.domain;

public class TppSudahAdaException extends RuntimeException{
    public TppSudahAdaException(Long id) {
        super("Tpp dengan Id " + id + " sudah ada.");
    }
}
