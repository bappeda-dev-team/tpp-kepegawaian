package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp;

public class TppPerhitunganNipBulanTahunNotFoundException extends RuntimeException {
    public TppPerhitunganNipBulanTahunNotFoundException(String nip, Integer bulan, Integer tahun) {
        super("Data TPP Perhitungan untuk pegawai dengan nip " + nip + " bulan " + bulan + "  tahun " + tahun + " tidak ditemukan.");
    }
}
