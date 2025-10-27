package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.annotation.JsonFormat;


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
	String statusJabatan,

	@Column("jenis_jabatan")
	String jenisJabatan,

	@Column("eselon")
	String eselon,

    @Column("pangkat")
    String pangkat,

    @Column("golongan")
    String golongan,

	@Column("tanggal_mulai")
	@JsonFormat(pattern = "dd-MM-yyyy")
	Date tanggalMulai,

	@Column("tanggal_akhir")
	@JsonFormat(pattern = "dd-MM-yyyy")
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
            String statusJabatan,
            String jenisJabatan,
            String eselon,
            String pangkat,
            String golongan,
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
                pangkat,
                golongan,
                tanggalMulai,
                tanggalAkhir,
                null, 
                null
        );
    }
}
