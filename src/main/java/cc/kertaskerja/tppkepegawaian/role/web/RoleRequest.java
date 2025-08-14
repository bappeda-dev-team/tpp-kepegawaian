package cc.kertaskerja.tppkepegawaian.role.web;

import cc.kertaskerja.tppkepegawaian.role.domain.IsActive;
import cc.kertaskerja.tppkepegawaian.role.domain.LevelRole;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RoleRequest(
        @Nullable
        Long levelId,

        @NotNull(message = "Nama role harus terdefinisi")
        @NotEmpty(message = "Nama role tidak boleh kosong")
        String namaRole,

        @NotNull(message = "NIP harus tidak boleh kosong")
        @NotEmpty(message = "NIP tidak boleh kosong")
        String nip,

        @NotNull(message = "Level role harus terdefinisi")
        LevelRole levelRole,

        @NotNull(message = "Is active harus terdefinisi")
        IsActive isActive
) {
}
