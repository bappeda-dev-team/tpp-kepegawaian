package cc.kertaskerja.tppkepegawaian.rekening.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RekeningRepository extends CrudRepository<RekeningPegawai, Long> {
   Optional<RekeningPegawai> findFirstByNipOrderByIdDesc(String nip);
}
