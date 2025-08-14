package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PegawaiRepository extends CrudRepository<Pegawai, String> {
    Optional<Pegawai> findByNip(String nip);
    boolean existsByNip(String nip);
    Iterable<Pegawai> findByKodeOpd(String kodeOpd);
    @Modifying
    @Transactional
    @Query("DELETE FROM pegawai WHERE nip = :nip")
    void deleteByNip(@Param("nip") String nip);
}
