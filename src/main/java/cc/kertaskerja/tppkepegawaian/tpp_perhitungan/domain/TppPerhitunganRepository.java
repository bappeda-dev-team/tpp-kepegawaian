package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;

public interface TppPerhitunganRepository extends CrudRepository<TppPerhitungan, Long> {
    @NonNull
    Optional<TppPerhitungan> findById(@NonNull Long id);
    boolean existsById(@NonNull Long id);

    @NonNull
    Optional<TppPerhitungan> findByHasilPerhitungan(@NonNull Float hasilPerhitungan);
    boolean existsByHasilPerhitungan(@NonNull Float hasilPerhitungan);
    
    @NonNull
    Iterable<TppPerhitungan> findByNip(@NonNull String nip);
    boolean existsByNip(@NonNull String nip);
    
    @Nullable
    Iterable<TppPerhitungan> findByKodeOpd(@Nullable String kodeOpd);
    
    @Query("SELECT * FROM tpp_perhitungan WHERE bulan = :bulan AND tahun = :tahun ORDER BY id")
    Iterable<TppPerhitungan> findByBulanAndTahun(@NonNull Integer bulan, @NonNull Integer tahun);
    
    boolean existsByBulanAndTahun(@NonNull Integer bulan, @NonNull Integer tahun);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM tpp_perhitungan WHERE id = :id")
    void deleteById(@NonNull Long id);
}
