package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain;

public class TppPerhitunganBulanTahunSudahAdaException extends RuntimeException{
    public TppPerhitunganBulanTahunSudahAdaException(Integer bulan, Integer tahun) {
        super("Data TPP Perhitungan untuk bulan " + bulan + " dan tahun " + tahun + " sudah ada.");
    }
}
