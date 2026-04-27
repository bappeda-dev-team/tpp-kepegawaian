package cc.kertaskerja.tppkepegawaian.rekening.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
        return rekeningRepository.findFirstByNipOrderByIdDesc(nip)
                .map(RekeningPegawai::decryptRekening);
    }

    public Map<String, RekeningPegawai> findByNipIn(List<String> nips) {
        List<RekeningPegawai> all = rekeningRepository.findByNipIn(nips);

        return all.stream()
                .collect(Collectors.toMap(
                        RekeningPegawai::nip,
                        RekeningPegawai::decryptRekening,
                        this::pickLatest));
    }

    private RekeningPegawai pickLatest(RekeningPegawai r1, RekeningPegawai r2) {

        return r1.id() > r2.id() ? r1 : r2;

    }
}
