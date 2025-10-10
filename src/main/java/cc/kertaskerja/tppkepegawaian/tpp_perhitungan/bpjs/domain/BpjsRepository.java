package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.bpjs.domain;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import io.micrometer.common.lang.NonNull;

public interface BpjsRepository extends CrudRepository<Bpjs, Long> {
    @NonNull
    Optional<Bpjs> findByNip(@NonNull String nip);
    boolean existsByNip(@NonNull String nip);

    Boolean existsByNamaBpjs(@NonNull String namaBpjs);

    @NonNull
    Iterable<Bpjs> findByNilaiBpjs(@NonNull Float nilaiBpjs);

    @Modifying
    @Transactional
    @Query("DELETE FROM bpjs WHERE nip = :nip")
    void deleteByNip(@Param("nip") @NonNull String nip);
}
