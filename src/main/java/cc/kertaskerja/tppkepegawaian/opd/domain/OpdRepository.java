package cc.kertaskerja.tppkepegawaian.opd.domain;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OpdRepository extends CrudRepository<Opd, Long> {
	Optional<Opd> findByKodeOpd(String kodeOpd);
	boolean existsByKodeOpd(String kodeOpd);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM opd WHERE kode_opd = :kodeOpd")
	void deleteByKodeOpd(@Param("kodeOpd") String kodeOpd);
}
