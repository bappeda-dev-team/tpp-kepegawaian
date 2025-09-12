package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain;

public class TppPerhitunganSudahAdaException extends RuntimeException {
    public TppPerhitunganSudahAdaException(Long id) {
        super("Tpp Perhitungan dengan Id " + id + " sudah ada .");
    }
}
