package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain;

public class TppPerhitunganBulanTahunNotFoundException extends RuntimeException {
    public TppPerhitunganBulanTahunNotFoundException(Integer Bulan, Integer Tahun) {
        super("Data Tpp Perhitungan untuk bulan " + Bulan + " dan tahun " + Tahun + " tidak ditemukan");
    }
}
