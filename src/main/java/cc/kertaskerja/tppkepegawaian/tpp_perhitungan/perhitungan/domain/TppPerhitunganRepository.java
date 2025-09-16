package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain;

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
    Iterable<TppPerhitungan> findByJenisTppAndNipAndBulanAndTahun(@NonNull JenisTpp jenisTpp, String nip, @NonNull Integer bulan, @NonNull Integer tahun);
    boolean existsByJenisTppAndNipAndBulanAndTahun(@NonNull JenisTpp jenisTpp, @NonNull String nip, @NonNull Integer bulan, @NonNull Integer tahun);

    @NonNull
    Iterable<TppPerhitungan> findByNip(@NonNull String nip);

    @NonNull
    Iterable<TppPerhitungan> findByNipAndBulanAndTahun(@NonNull String nip, @NonNull Integer bulan, @NonNull Integer tahun);

    @Nullable
    Iterable<TppPerhitungan> findByKodeOpd(@Nullable String kodeOpd);
    boolean existsByNip(@NonNull String nip);

    @Modifying
    @Transactional
    @Query("DELETE FROM tpp_perhitungan WHERE id = :id")
    void deleteById(@NonNull Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM tpp_perhitungan WHERE nip = :nip AND bulan = :bulan AND tahun = :tahun")
    void deleteByNipAndBulanAndTahun(@NonNull String nip, @NonNull Integer bulan, @NonNull Integer tahun);
}
