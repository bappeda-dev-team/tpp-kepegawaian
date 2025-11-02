package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception;

public class TppJenisTppNipBulanTahunNotFoundException extends RuntimeException {
    public TppJenisTppNipBulanTahunNotFoundException(String jenisTpp, String nip, Integer bulan, Integer tahun) {
        super("Tpp dengan jenis Tpp " + jenisTpp + " nip " +  nip + " bulan " + bulan + " tahun " + tahun + " tidak ditemukan.");
    }
}
