package cc.kertaskerja.tppkepegawaian.npwp.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface NpwpRepository extends CrudRepository<Npwp, Long> {
    Optional<Npwp> findByNip(String nip);
}
