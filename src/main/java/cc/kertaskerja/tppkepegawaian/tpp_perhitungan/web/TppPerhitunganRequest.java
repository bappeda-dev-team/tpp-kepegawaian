
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.web;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain.JenisTpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain.NamaPerhitungan;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.web.formating.FormatingFloatDeserializer;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.web.formating.FormatingFloatListDeserializer;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.web.formating.FormatingFloatSerializer;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record TppPerhitunganRequest(
        @Nullable
        Long tppPerhitunganId,
        
        @NotNull(message = "Jenis tpp harus terdefinisi")
        JenisTpp jenisTpp,
        
        @Nullable
        String kodeOpd,
        
        @NotNull(message = "NIP harus terdefinisi")
        @NotEmpty(message = "NIP tidak boleh kosong")
        String nip,
        
        @Nullable
        String kodePemda,
        
        @NotNull(message = "Nama perhitungan harus terdefinisi")
        @NotEmpty(message = "Minimal pilih satu nama perhitungan")
        List<NamaPerhitungan> namaPerhitungan,
        
        @Nullable
        @JsonDeserialize(using = FormatingFloatListDeserializer.class)
        @JsonSerialize(contentUsing = FormatingFloatSerializer.class)
        List<Float> nilaiPerhitungan,
        
        @Nullable
        @JsonDeserialize(using = FormatingFloatDeserializer.class)
        @JsonSerialize(using = FormatingFloatSerializer.class)
        Float maksimum,
        
        @NotNull(message = "Bulan harus terdefinisi")
        Integer bulan,
        
        @NotNull(message = "Tahun harus terdefinisi")
        Integer tahun,
        
        @Nullable
        @JsonDeserialize(using = FormatingFloatDeserializer.class)
        @JsonSerialize(using = FormatingFloatSerializer.class)
        Float hasilPerhitungan
) { 
    /**
     * Menghitung total dari semua nama perhitungan yang dipilih
     * @return total nilai dari semua nama perhitungan
     */
    public Float hitungTotalPerhitungan() {
        if (nilaiPerhitungan == null || nilaiPerhitungan.isEmpty()) {
            return 0.0f;
        }
        
        return nilaiPerhitungan.stream()
                .reduce(0.0f, Float::sum);
    }
    
    /**
     * Membuat TppPerhitunganRequest dengan hasil perhitungan yang sudah dihitung
     * @return TppPerhitunganRequest dengan hasilPerhitungan yang sudah diisi
     */
    public TppPerhitunganRequest withCalculatedResult() {
        return new TppPerhitunganRequest(
                tppPerhitunganId,
                jenisTpp,
                kodeOpd,
                nip,
                kodePemda,
                namaPerhitungan,
                nilaiPerhitungan,
                maksimum,
                bulan,
                tahun,
                hitungTotalPerhitungan()
        );
    }
}
