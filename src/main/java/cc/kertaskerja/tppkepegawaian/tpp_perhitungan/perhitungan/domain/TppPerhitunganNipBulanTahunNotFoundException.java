package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain;

public class TppPerhitunganNipBulanTahunNotFoundException extends RuntimeException {
    public TppPerhitunganNipBulanTahunNotFoundException(String nip, Integer bulan, Integer tahun) {
        super("Data TPP Perhitungan untuk pegawai dengan nip " + nip + " pada bulan " + bulan + " dan tahun " + tahun + " tidak ada.");
    }
}
