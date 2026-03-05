package cc.kertaskerja.tppkepegawaian.rekening.domain;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RekeningService {
    private final RekeningRepository rekeningRepository;

    public RekeningService(RekeningRepository rekeningRepository) {
        this.rekeningRepository = rekeningRepository;
    }

    public RekeningPegawai save(RekeningPegawai rekeningPegawai) {
        RekeningPegawai encrypted = rekeningPegawai.encryptRekening();
        RekeningPegawai saved = rekeningRepository.save(encrypted);
        return saved.decryptRekening();
    }

    public Optional<RekeningPegawai> findByNip(String nip) {
        return rekeningRepository.findByNip(nip)
                .map(RekeningPegawai::decryptRekening);
    }
}
