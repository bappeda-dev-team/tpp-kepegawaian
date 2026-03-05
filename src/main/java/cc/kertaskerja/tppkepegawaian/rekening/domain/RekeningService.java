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
        return rekeningRepository.save(rekeningPegawai);
    }

    public Optional<RekeningPegawai> findByNip(String nip) {
        return rekeningRepository.findByNip(nip);
    }
}
