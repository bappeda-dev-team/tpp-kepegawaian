
package cc.kertaskerja.tppkepegawaian.tpp.web;

import cc.kertaskerja.tppkepegawaian.tpp.domain.JenisTpp;
import cc.kertaskerja.tppkepegawaian.tpp.web.formating.FormatingFloatDeserializer;
import cc.kertaskerja.tppkepegawaian.tpp.web.formating.FormatingFloatSerializer;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public record TppRequest(
        @Nullable
        Long tppId,
        
        @NotNull(message = "Jenis tpp harus terdefinisi")
        JenisTpp jenisTpp,
        
        @Nullable
        String kodeOpd,
        
        @NotNull(message = "NIP harus terdefinisi")
        @NotEmpty(message = "NIP tidak boleh kosong")
        String nip,
        
        @Nullable
        String kodePemda,
        
        @NotNull(message = "Keterangan harus terdefinisi")
        @NotEmpty(message = "Keterangan tidak boleh kosong")
        String keterangan,
        
        @Nullable
        @JsonDeserialize(using = FormatingFloatDeserializer.class)
        @JsonSerialize(using = FormatingFloatSerializer.class)
        Float nilaiInput,
        
        @Nullable
        @JsonDeserialize(using = FormatingFloatDeserializer.class)
        @JsonSerialize(using = FormatingFloatSerializer.class)
        Float maksimum,

        @Nullable
        @JsonDeserialize(using = FormatingFloatDeserializer.class)
        @JsonSerialize(using = FormatingFloatSerializer.class)
        Float hasilPerhitungan,
        
        @NotNull(message = "Bulan harus terdefinisi")
        Integer bulan,
        
        @NotNull(message = "Tahun harus terdefinisi")
        Integer tahun,
        
        @Nullable
        @JsonSerialize(using = FormatingFloatSerializer.class)
        Float totalTpp
) {
        /**
         * Menghitung total TPP berdasarkan nilai input dan hasil perhitungan (dalam persen)
         * @param hasilPerhitunganParam hasil perhitungan dalam bentuk persen dari TppPerhitungan (opsional)
         * @return total TPP yang sudah dihitung
         */
        public Float calculateTotalTpp(Float hasilPerhitunganParam) {
                // Jika totalTpp sudah ada, gunakan nilai tersebut
                if (totalTpp != null) {
                        return totalTpp;
                }

                // Gunakan parameter jika ada, jika tidak gunakan field hasilPerhitungan
                Float perhitunganToUse = hasilPerhitunganParam != null ? hasilPerhitunganParam : hasilPerhitungan;

                // Jika ada hasil perhitungan dan nilaiInput, hitung berdasarkan rumus
                if (perhitunganToUse != null && nilaiInput != null) {
                        Float persenHasilPerhitungan = perhitunganToUse / 100.0f;
                        Float calculated = nilaiInput * persenHasilPerhitungan;
                        if (maksimum != null) {
                                return Math.min(calculated, maksimum);
                        }
                        return calculated;
                }

                // Gunakan nilaiInput atau 0 jika tidak ada data
                return nilaiInput != null ? nilaiInput : 0.0f;
        }

        public Float getTotalTppCalculated() {
                return calculateTotalTpp(hasilPerhitungan);
        }

        /**
         * Membuat TppRequest baru dengan total TPP yang sudah dihitung
         * @param hasilPerhitunganParam dalam bentuk persent
         * @return TppRequest dengan totalTpp yang sudah diisi
         */
        public TppRequest withCalculatedTotalTpp(Float hasilPerhitunganParam) {
                Float calculatedTotal = calculateTotalTpp(hasilPerhitunganParam);

                return new TppRequest(
                        tppId,
                        jenisTpp,
                        kodeOpd,
                        nip,
                        kodePemda,
                        keterangan,
                        nilaiInput,
                        maksimum,
                        hasilPerhitungan,
                        bulan,
                        tahun,
                        calculatedTotal
                );
        }
}
