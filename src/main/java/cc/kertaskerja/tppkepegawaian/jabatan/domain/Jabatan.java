package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "jabatan")
public record Jabatan(
		@Id
		Long id,
		
		String nip,
		String namaJabatan,
		String kodeOpd,
		StatusJabatan statusJabatan,
		JenisJabatan jenisJabatan,
		Eselon eselon,
		Date tanggalMulai,
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
