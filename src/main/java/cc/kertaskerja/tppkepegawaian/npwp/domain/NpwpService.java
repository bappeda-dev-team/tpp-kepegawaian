package cc.kertaskerja.tppkepegawaian.npwp.domain;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NpwpService {
    private final NpwpRepository npwpRepository;

    public NpwpService(NpwpRepository npwpRepository) {
        this.npwpRepository = npwpRepository;
    }

    public Npwp save(Npwp npwp) {
        return npwpRepository.save(npwp);
    }

    public Optional<Npwp> findByNip(String nip) {
        return npwpRepository.findByNip(nip);
    }
}
