package cc.kertaskerja.tppkepegawaian.pegawai.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Optional;
import java.util.List;
import java.util.stream.StreamSupport;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitungan;

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
        String statusPegawai,

        @JsonProperty("nama_jabatan")
        String namaJabatan,

        @JsonProperty("status_jabatan")
        String statusJabatan,

        @JsonProperty("jenis_jabatan")
        String jenisJabatan,

        @JsonProperty("eselon")
        String eselon,

        @JsonProperty("pangkat")
        String pangkat,

        @JsonProperty("golongan")
        String golongan,

        @JsonProperty("jenis_tpp")
        String jenisTpp,

        @JsonProperty("bulan")
        Integer bulan,

        @JsonProperty("tahun")
        Integer tahun,
        
        @JsonProperty("total_terima_tpp")
        Long totalTerimaTpp,

        @JsonProperty("created_date")
        Instant createdDate,

        @JsonProperty("last_modified_date")
        Instant lastModifiedDate
        

) {
    public static PegawaiWithJabatanResponse from(
            cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai pegawai,
            cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan jabatan,
            Optional<Tpp> tpp,
            List<TppPerhitungan> perhitunganList
    ) {
        Long totalTerimaTpp = null;
        if (tpp != null && tpp.isPresent()) {
            Tpp tppData = tpp.get();
            if (tppData.maksimumTpp() != null) {
                Float totalPersen = 0.0f;
                for (var perhitungan : perhitunganList) {
                    if (perhitungan.nilaiPerhitungan() != null &&
                        perhitungan.jenisTpp().equals(tppData.jenisTpp())) {
                        totalPersen += perhitungan.nilaiPerhitungan();
                    }
                }

                Long totalTpp = (long) Math.round(tppData.maksimumTpp() * (totalPersen / 100.0f));

                Long totalPajak = (long) Math.round(totalTpp * (tppData.pajak() / 100.0f));

                Long totalBpjs = (long) Math.round(totalTpp * (tppData.bpjs() / 100.0f));

                totalTerimaTpp = totalTpp - totalPajak - totalBpjs;
            }
        }

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
                tpp != null && tpp.isPresent() ? tpp.get().jenisTpp() : null,
                tpp != null && tpp.isPresent() ? tpp.get().bulan() : null,
                tpp != null && tpp.isPresent() ? tpp.get().tahun() : null,
                totalTerimaTpp,
                pegawai.createdDate(),
                pegawai.lastModifiedDate()
        );
    }

    // Backward compatibility method
    public static PegawaiWithJabatanResponse from(
            cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai pegawai,
            cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan jabatan,
            Optional<Tpp> tpp
    ) {
        return from(pegawai, jabatan, tpp, List.of());
    }
}
