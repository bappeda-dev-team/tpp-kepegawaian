package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.bpjs.domain.exception;

public class BpjsNotFoundException extends RuntimeException {
    public BpjsNotFoundException(String nip) {
        super("Bpjs dengan nip " + nip + " tidak ditemukan.");
    }
}
