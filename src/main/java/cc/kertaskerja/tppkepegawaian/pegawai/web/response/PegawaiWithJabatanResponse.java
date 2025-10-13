package cc.kertaskerja.tppkepegawaian.pegawai.web.response;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.StatusPegawai;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.Eselon;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JenisJabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.StatusJabatan;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record PegawaiWithJabatanResponse(
        Long id,

        @JsonProperty("nama_pegawai")
        String namaPegawai,
        
        @JsonProperty("nip")
        String nip,

        @JsonProperty("kode_opd")
        String kodeOpd,
        
        @JsonProperty("nama_role")
        String namaRole,
        
        @JsonProperty("status_pegawai")
        StatusPegawai statusPegawai,

        @JsonProperty("nama_jabatan")
        String namaJabatan,

        @JsonProperty("status_jabatan")
        StatusJabatan statusJabatan,

        @JsonProperty("jenis_jabatan")
        JenisJabatan jenisJabatan,

        @JsonProperty("eselon")
        Eselon eselon,

        @JsonProperty("pangkat")
        String pangkat,

        @JsonProperty("golongan")
        String golongan,
        
        @JsonProperty("created_date")
        Instant createdDate,

        @JsonProperty("last_modified_date")
        Instant lastModifiedDate

) {
    public static PegawaiWithJabatanResponse from(
            cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai pegawai,
            cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan jabatan
    ) {
        return new PegawaiWithJabatanResponse(
                pegawai.id(),
                pegawai.namaPegawai(),
                pegawai.nip(),
                pegawai.kodeOpd(),
                pegawai.namaRole(),
                pegawai.statusPegawai(),
                jabatan != null ? jabatan.namaJabatan() : null,
                jabatan != null ? jabatan.statusJabatan() : null,
                jabatan != null ? jabatan.jenisJabatan() : null,
                jabatan != null ? jabatan.eselon() : null,
                jabatan != null ? jabatan.pangkat() : null,
                jabatan != null ? jabatan.golongan() : null,
                pegawai.createdDate(),
                pegawai.lastModifiedDate()
        );
    }
}
