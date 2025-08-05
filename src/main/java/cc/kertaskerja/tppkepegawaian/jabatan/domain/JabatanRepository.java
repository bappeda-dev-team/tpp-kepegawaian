package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface JabatanRepository extends CrudRepository<Jabatan, Long> {
	Optional<Jabatan> findByNip(String nip);
	boolean existsByNip(String nip);
	Iterable<Jabatan> findByKodeOpd(String kodeOpd);
	@Modifying
	@Transactional
	@Query("DELETE FROM jabatan WHERE id = :id")
	void deleteById(String id);
}
