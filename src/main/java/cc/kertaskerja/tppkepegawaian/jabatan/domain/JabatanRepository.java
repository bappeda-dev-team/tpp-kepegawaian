package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface JabatanRepository extends CrudRepository<Jabatan, Long> {
    @NonNull
	Optional<Jabatan> findById(@NonNull Long id);
	boolean existsById(@NonNull Long id);
	@NonNull
	Iterable<Jabatan> findByKodeOpd(@NonNull String kodeOpd);
	@Modifying
	@Transactional
	@Query("DELETE FROM jabatan WHERE id = :id")
	void deleteById(@NonNull Long id);
}
