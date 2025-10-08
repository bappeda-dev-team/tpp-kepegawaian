package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.pajak.domain;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import io.micrometer.common.lang.NonNull;

import java.util.Optional;

public interface PajakRepository extends CrudRepository<Pajak, Long> {
    @NonNull
    Optional<Pajak> findByNip(@NonNull String nip);
    boolean existsByNip(@NonNull String nip);
    
    boolean existsByNamaPajak(@NonNull String namaPajak);
    
    @NonNull
    Iterable<Pajak> findByNilaiPajak(@NonNull String nilaiPajak);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM role WHERE nip = :nip")
    void deleteByNip(@Param("nip") @NonNull String nip);
}
