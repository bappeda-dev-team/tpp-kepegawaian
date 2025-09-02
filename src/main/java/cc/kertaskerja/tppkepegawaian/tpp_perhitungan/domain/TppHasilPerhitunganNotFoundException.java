package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain;

public class TppHasilPerhitunganNotFoundException extends RuntimeException {
    public TppHasilPerhitunganNotFoundException(Float hasilPerhitungan) {
        super("Data TPP Hasil Perhitungan " + hasilPerhitungan + " tidak ditemukan .");
    }
}
