package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "jabatan")
public record Jabatan(
	@Id
	Long id,

	@Column("nip")
	String nip,

	@Column("nama_jabatan")
	String namaJabatan,

	@Column("kode_opd")
	String kodeOpd,

	@Column("status_jabatan")
	StatusJabatan statusJabatan,

	@Column("jenis_jabatan")
	JenisJabatan jenisJabatan,

	@Column("eselon")
	Eselon eselon,

	@Column("tanggal_mulai")
	Date tanggalMulai,

	@Column("tanggal_akhir")
	Date tanggalAkhir,

	@CreatedDate
	Instant createdDate,

	@LastModifiedDate
	Instant lastModifiedDate
) {
    public static Jabatan of(
            String nip,
            String namaJabatan,
            String kodeOpd,
            StatusJabatan statusJabatan,
            JenisJabatan jenisJabatan,
            Eselon eselon,
            Date   tanggalMulai,
            Date   tanggalAkhir
    ) {
        return new Jabatan(
                null, 
                nip,
                namaJabatan,
                kodeOpd,
                statusJabatan,
                jenisJabatan,
                eselon,
                tanggalMulai,
                tanggalAkhir,
                null, 
                null
        );
    }
}
