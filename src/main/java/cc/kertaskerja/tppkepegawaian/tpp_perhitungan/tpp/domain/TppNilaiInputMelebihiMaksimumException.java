package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain;

public class TppNilaiInputMelebihiMaksimumException extends RuntimeException {
    public TppNilaiInputMelebihiMaksimumException() {
        super("Nilai input tidak boleh melebihi nilai maksimum = 30.");
    }
}
