package cc.kertaskerja.tppkepegawaian.role.domain;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    @NonNull
    Optional<Role> findById(@NonNull Long id);
    boolean existsById(@NonNull Long id);
    boolean existsByNamaRole(@NonNull String namaRole);
    @NonNull
    Iterable<Role> findByNip(@NonNull String nip);

    @Modifying
    @Transactional
    @Query("DELETE FROM role WHERE id = :id")
    void deleteById(@NonNull Long id);
}
