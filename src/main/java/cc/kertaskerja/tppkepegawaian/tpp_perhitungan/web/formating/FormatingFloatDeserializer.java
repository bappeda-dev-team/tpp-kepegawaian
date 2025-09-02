package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.web.formating;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

public class FormatingFloatDeserializer extends JsonDeserializer<Float> {
    @Override
    public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            return p.getFloatValue();
        }
        if (t == JsonToken.VALUE_STRING) {
            String text = p.getText();
            if (text == null) {
                return null;
            }
            String trimmed = text.trim();
            if (trimmed.isEmpty()) {
                return null;
            }
            // Formating angka input : "1.000.000,50" -> "1000000.50"
            String normalized = trimmed.replace(".", "").replace(",", ".");
            try {
                BigDecimal bd = new BigDecimal(normalized);
                return bd.floatValue();
            } catch (NumberFormatException ex) {
                // error handling (throw 400 error)
                return (Float) ctxt.handleWeirdStringValue(Float.class, text, "Tidak dapat melakuka formating angka di nilaiInput");
            }
        }
        return (Float) ctxt.handleUnexpectedToken(Float.class, p);
    }
}
