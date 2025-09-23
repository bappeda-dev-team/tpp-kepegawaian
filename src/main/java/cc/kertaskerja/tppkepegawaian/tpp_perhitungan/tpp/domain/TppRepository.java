
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
    
    @NonNull
    Iterable<Tpp> findByKodeOpdAndBulanAndTahun(@NonNull String kodeOpd, @NonNull Integer bulan, @NonNull Integer tahun);
    boolean existsByJenisTppAndKodeOpdAndBulanAndTahun(@NonNull JenisTpp jenisTpp, @NonNull String kodeOpd, @NonNull Integer bulan, @NonNull Integer tahun);
    
    @NonNull
    Optional<Tpp> findByJenisTppAndNipAndBulanAndTahun(@NonNull JenisTpp jenisTpp, @NonNull String nip, @NonNull Integer bulan, @NonNull Integer tahun);
    boolean existsByJenisTppAndNipAndBulanAndTahun(@NonNull JenisTpp jenisTpp, @NonNull String nip, @NonNull Integer bulan, @NonNull Integer tahun);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM tpp WHERE nip = :nip AND bulan = :bulan AND tahun = :tahun")
    void deleteByNipAndBulanAndTahun(@NonNull String nip, @NonNull Integer bulan, @NonNull Integer tahun);
}
