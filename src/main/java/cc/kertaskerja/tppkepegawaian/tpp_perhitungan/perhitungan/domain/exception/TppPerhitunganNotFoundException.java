package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception;

public class TppPerhitunganNotFoundException extends RuntimeException {
    public TppPerhitunganNotFoundException(Long id) {
        super("Tpp Perhitungan dengan id " + id + " tidak ditemukan.");
    }
}
