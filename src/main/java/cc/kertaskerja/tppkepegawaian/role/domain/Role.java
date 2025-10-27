package cc.kertaskerja.tppkepegawaian.role.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Table(name = "role")
public record Role(
	@Id
	Long id,

	@Column("nama_role")
	String namaRole,

	@Column("nip")
	String nip,

	@Column("level_role")
	String levelRole,

	@Column("is_active")
	String isActive,

	@CreatedDate
	Instant createdDate,

	@LastModifiedDate
	Instant lastModifiedDate
) {
    public static Role of(
            String namaRole,
            String nip,
            String levelRole,
            String isActive
    ) {
        return new Role(
                null, 
                namaRole, 
                nip,
                levelRole, 
                isActive,
                null, 
                null
        );
    }
}
