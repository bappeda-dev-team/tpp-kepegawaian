package cc.kertaskerja.tppkepegawaian.pegawai.web.response;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan;
import cc.kertaskerja.tppkepegawaian.role.domain.Role;

import java.util.Set;

public record PegawaiWithJabatanAndRolesResponse(
        Long id,
        String namaPegawai,
        String nip,
        String kodeOpd,
        String namaJabatan,
        String statusJabatan,
        String jenisJabatan,
        String eselon,
        String pangkat,
        String golongan,
        String namaRole,
        String isActive
) {
    public static PegawaiWithJabatanAndRolesResponse of(
            Long id,
            String namaPegawai,
            String nip,
            String kodeOpd,
            Set<Role> roles,
            Jabatan jabatan
    ) {
        // Nilai default untuk role
        String namaRole = null;
        String isActive = null;

        // Extract role data if available
        if (roles != null && !roles.isEmpty()) {
            Role firstRole = roles.iterator().next();
            namaRole = firstRole.namaRole();
            isActive = firstRole.isActive();
        }

        // Nilai default untuk jabatan
        String namaJabatan = null;
        String statusJabatan = null;
        String jenisJabatan = null;
        String eselon = null;
        String pangkat = null;
        String golongan = null;

        // Ekstrak data jabatan jika ada
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