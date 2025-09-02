package cc.kertaskerja.tppkepegawaian.role.domain;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class RoleCacheService {

    @CacheEvict(value = "rolesByNip", key = "#nip")
    public void evictRolesCache(String nip) {
        //
    }
}
