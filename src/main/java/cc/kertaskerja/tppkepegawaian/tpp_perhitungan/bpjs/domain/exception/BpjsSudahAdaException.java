package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.bpjs.domain.exception;

public class BpjsSudahAdaException extends RuntimeException {
    public BpjsSudahAdaException(String nip) {
        super("Bpjs dengan nip " + nip + " sudah ada.");
    }
}
