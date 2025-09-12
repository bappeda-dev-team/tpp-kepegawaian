
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;

public interface TppRepository extends CrudRepository<Tpp, Long> {
    @NonNull
    Optional<Tpp> findById(@NonNull Long id);
    boolean existsById(@NonNull Long id);

    @NonNull
    Iterable<Tpp> findByNip(@NonNull String nip);
    boolean existsByNip(@NonNull String nip);
    
    @Nullable
    Iterable<Tpp> findByKodeOpd(@Nullable String kodeOpd);

    @NonNull
    Iterable<Tpp> findByNipAndBulanAndTahun(@NonNull String nip, @NonNull Integer bulan, @NonNull Integer tahun);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM tpp WHERE id = :id")
    void deleteById(@NonNull Long id);
}
