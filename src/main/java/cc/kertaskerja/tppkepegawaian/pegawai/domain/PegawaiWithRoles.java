package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import cc.kertaskerja.tppkepegawaian.role.domain.Role;

import java.util.Set;

public record PegawaiWithRoles(
        Long id,
        String namaPegawai,
        String nip,
        String kodeOpd,
        Set<Role> roles
) {

    public static PegawaiWithRoles of(Pegawai pegawai, Set<Role> roles) {
        return new PegawaiWithRoles(
                pegawai.id(),
                pegawai.namaPegawai(),
                pegawai.nip(),
                pegawai.kodeOpd(),
                roles
        );
    }
}
