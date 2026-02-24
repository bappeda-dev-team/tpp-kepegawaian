
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    Iterable<Tpp> findByKodeOpdAndBulanAndTahun(@NonNull String kodeOpd, @NonNull Integer bulan,
            @NonNull Integer tahun);

    boolean existsByJenisTppAndKodeOpdAndBulanAndTahun(@NonNull String jenisTpp, @NonNull String kodeOpd,
            @NonNull Integer bulan, @NonNull Integer tahun);

    @NonNull
    Optional<Tpp> findByJenisTppAndNipAndBulanAndTahun(@NonNull String jenisTpp, @NonNull String nip,
            @NonNull Integer bulan, @NonNull Integer tahun);

    boolean existsByJenisTppAndNipAndBulanAndTahun(@NonNull String jenisTpp, @NonNull String nip,
            @NonNull Integer bulan, @NonNull Integer tahun);

    @NonNull
    List<Tpp> findAllByJenisTppAndNipInAndBulanAndTahun(@NonNull String jenisTpp, @NonNull List<String> nips, @NonNull Integer bulan,
            @NonNull Integer tahun);

    List<Tpp> findAllByJenisTppAndNipInAndKodeOpd(@NonNull String jenisTpp, @NonNull List<String> nips, @NonNull String kodeOpd);

    @Modifying
    @Transactional
    @Query("DELETE FROM tpp WHERE nip = :nip AND bulan = :bulan AND tahun = :tahun")
    void deleteByNipAndBulanAndTahun(@NonNull String nip, @NonNull Integer bulan, @NonNull Integer tahun);
}
