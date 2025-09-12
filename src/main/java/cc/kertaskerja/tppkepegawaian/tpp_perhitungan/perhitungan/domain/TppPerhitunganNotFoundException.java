package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain;

public class TppPerhitunganNotFoundException extends RuntimeException {
    public TppPerhitunganNotFoundException(Long id) {
        super("Tpp Perhitungan dengan Id " + id + " tidak ditemukan.");
    }
}
