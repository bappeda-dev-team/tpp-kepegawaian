package cc.kertaskerja.tppkepegawaian.rekening.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.List;

public interface RekeningRepository extends CrudRepository<RekeningPegawai, Long> {
    Optional<RekeningPegawai> findFirstByNipOrderByIdDesc(String nip);

    List<RekeningPegawai> findByNipIn(List<String> nips);
}
