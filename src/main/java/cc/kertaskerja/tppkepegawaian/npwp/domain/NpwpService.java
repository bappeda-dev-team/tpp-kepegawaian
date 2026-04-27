package cc.kertaskerja.tppkepegawaian.npwp.domain;

import java.util.function.Function;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
        return npwpRepository.findFirstByNipOrderByIdDesc(nip);
    }

    public Map<String, Npwp> findByNipIn(List<String> nips) {
        List<Npwp> all = npwpRepository.findByNipIn(nips);

        return all.stream()
                .collect(Collectors.toMap(
                        Npwp::nip,
                        Function.identity(),
                        this::pickLatest));
    }

    private Npwp pickLatest(Npwp r1, Npwp r2) {
        return r1.id() > r2.id() ? r1 : r2;
    }
}
