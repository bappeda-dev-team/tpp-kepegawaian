package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.web.formating;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Formating pemisah antara '.' dan decimal ',' sesuai dengan format mata uang Indonesia
 * Contoh:
 *  - 500000f -> "500.000"
 *  - 1000000.5f -> "1.000.000,5"
 */
public class FormatingFloatSerializer extends JsonSerializer<Float> {

    private static final ThreadLocal<DecimalFormat> INDONESIAN_FORMAT = ThreadLocal.withInitial(() -> {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0.##", symbols);
        df.setGroupingUsed(true);
        return df;
    });

    @Override
    public void serialize(Float value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        String formatted = INDONESIAN_FORMAT.get().format(value);
        gen.writeString(formatted);
    }
}
