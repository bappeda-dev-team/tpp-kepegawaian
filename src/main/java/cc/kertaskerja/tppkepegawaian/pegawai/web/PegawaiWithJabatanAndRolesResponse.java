package cc.kertaskerja.tppkepegawaian.pegawai.web;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.Eselon;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JenisJabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.StatusJabatan;
import cc.kertaskerja.tppkepegawaian.role.domain.Role;
import cc.kertaskerja.tppkepegawaian.role.domain.IsActive;

import java.util.Set;

public record PegawaiWithJabatanAndRolesResponse(
        Long id,
        String namaPegawai,
        String nip,
        String kodeOpd,
        String namaJabatan,
        StatusJabatan statusJabatan,
        JenisJabatan jenisJabatan,
        Eselon eselon,
        String pangkat,
        String golongan,
        String namaRole,
        IsActive isActive
) {
    public static PegawaiWithJabatanAndRolesResponse of(
            Long id,
            String namaPegawai,
            String nip,
            String kodeOpd,
            Set<Role> roles,
            Jabatan jabatan
    ) {
        // Default values for role
        String namaRole = null;
        IsActive isActive = null;

        if (roles != null && !roles.isEmpty()) {
            Role role = roles.iterator().next();
            namaRole = role.namaRole();
            isActive = role.isActive();
        }

        // Default values for jabatan
        String namaJabatan = null;
        StatusJabatan statusJabatan = null;
        JenisJabatan jenisJabatan = null;
        Eselon eselon = null;
        String pangkat = null;
        String golongan = null;

        if (jabatan != null) {
            namaJabatan = jabatan.namaJabatan();
            statusJabatan = jabatan.statusJabatan();
            jenisJabatan = jabatan.jenisJabatan();
            eselon = jabatan.eselon();
            pangkat = jabatan.pangkat();
            golongan = jabatan.golongan();
        }

        return new PegawaiWithJabatanAndRolesResponse(
                id,
                namaPegawai,
                nip,
                kodeOpd,
                namaJabatan,
                statusJabatan,
                jenisJabatan,
                eselon,
                pangkat,
                golongan,
                namaRole,
                isActive
        );
    }
}