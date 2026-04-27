package cc.kertaskerja.tppkepegawaian.npwp.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.List;

public interface NpwpRepository extends CrudRepository<Npwp, Long> {
    Optional<Npwp> findFirstByNipOrderByIdDesc(String nip);

    List<Npwp> findByNipIn(List<String> nips);
}
