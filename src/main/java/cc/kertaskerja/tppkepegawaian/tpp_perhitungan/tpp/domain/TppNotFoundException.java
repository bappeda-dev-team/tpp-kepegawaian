package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain;

public class TppNotFoundException extends RuntimeException {
    public TppNotFoundException(Long id) {
        super("Tpp dengan Id " + id + " tidak ditemukan.");
    }
}
