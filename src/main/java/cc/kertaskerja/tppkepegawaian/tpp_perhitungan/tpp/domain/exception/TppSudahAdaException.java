package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception;

public class TppSudahAdaException extends RuntimeException{
    public TppSudahAdaException(Long id) {
        super("Tpp dengan Id " + id + " sudah ada.");
    }
}
