package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.web.formating;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Formating nilai List<Float> untuk membedakan tanda koma pada angka dan tanda koma pemisah angka, contoh:
 * - list angka : [25.5, 22.5]
 * - user input angka menggunakan tanda koma : ["25,5", "22,5"]
 * - hasil formating : [25,5, 22,5] -> [25.5, 22.5]
 */
public class FormatingFloatListDeserializer extends JsonDeserializer<List<Float>> {

    @Override
    public List<Float> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = p.currentToken();
        if (currentToken == null) {
            currentToken = p.nextToken();
        }

        // Support single string containing delimited values (fallback)
        if (currentToken == JsonToken.VALUE_STRING) {
            String text = p.getText();
            return parseDelimitedString(text);
        }

        if (currentToken != JsonToken.START_ARRAY) {
            // Coba mengurai nilai tunggal sebagai daftar satu elemen
            Float single = tryParseSingle(p, currentToken);
            List<Float> result = new ArrayList<>();
            if (single != null) {
                result.add(single);
            }
            return result;
        }

        ArrayNode arrayNode = p.getCodec().readTree(p);
        int size = arrayNode.size();
        List<Float> values = new ArrayList<>();

        boolean allIntegral = true;
        for (JsonNode node : arrayNode) {
            if (!node.isIntegralNumber()) {
                allIntegral = false;
                break;
            }
        }

        // Jika semua elemennya bilangan bulat dan jumlahnya genap, interpretasikan sebagai pasangan koma-desimal
        if (allIntegral && size % 2 == 0 && size > 0) {
            for (int i = 0; i < size; i += 2) {
                int whole = arrayNode.get(i).asInt();
                int frac = arrayNode.get(i + 1).asInt();
                float value;
                if (frac == 0) {
                    value = (float) whole;
                } else {
                    // Tentukan skala berdasarkan jumlah digit bagian pecahan
                    int scale = (frac >= 100) ? 1000 : (frac >= 10 ? 100 : 10);
                    value = whole + (frac / (float) scale);
                }
                values.add(value);
            }
            return values;
        }

        // Mengurai setiap elemen secara individual
        for (JsonNode node : arrayNode) {
            if (node.isNumber()) {
                values.add(node.floatValue());
            } else if (node.isTextual()) {
                values.add(parseLocalized(node.asText()));
            } else if (node.isNull()) {
                values.add(null);
            } else {
                // Tipe tidak didukung, coba upaya terbaik untuk mengurai String
                values.add(parseLocalized(node.asText()));
            }
        }

        return values;
    }

    private Float tryParseSingle(JsonParser p, JsonToken token) throws IOException {
        if (token == JsonToken.VALUE_NUMBER_INT || token == JsonToken.VALUE_NUMBER_FLOAT) {
            return p.getFloatValue();
        }
        if (token == JsonToken.VALUE_STRING) {
            return parseLocalized(p.getText());
        }
        return null;
    }

    private List<Float> parseDelimitedString(String text) {
        List<Float> result = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return result;
        }
        // Menerima beberapa input : ';', '|', spasi, dan koma diantara nilai angka.
        String[] parts = text.split("[;|\\n\\r\\t ]+");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) continue;
            result.add(parseLocalized(trimmed));
        }
        return result;
    }

    private Float parseLocalized(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;
        // hapus spasi
        s = s.replace("\u00A0", "").replace(" ", "");
        // Normalisasi pemisah desimal dan ribuan
        int lastComma = s.lastIndexOf(',');
        int lastDot = s.lastIndexOf('.');
        if (lastComma >= 0 && lastDot >= 0) {
            // Jika semua ditampilkan, desimal adalah yang terakhir
            if (lastComma > lastDot) {
                // Tanda koma = desimal, hilangkan titik sebagai ribuan
                s = s.replace(".", "");
                s = s.replace(',', '.');
            } else {
                // Tanda koma = titik, hilangkan titik sebagai ribuan
                s = s.replace(",", "");
            }
        } else if (lastComma >= 0) {
            // Hanya menampilkan koma -> koma = desimal
            s = s.replace(".", "");
            s = s.replace(',', '.');
        } else {
            // Hanya titik atau empty -> titik = desimal, hilangkan koma sebagai ribuan
            s = s.replace(",", "");
        }
        // Hapus semua karakter non-digit/non-titik yang tersisa
        s = s.replaceAll("[^0-9.\\-]", "");
        if (s.isEmpty() || s.equals("-") || s.equals(".")) {
            return null;
        }
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException ex) {
            // Kembalikan null untuk menghindari kegagalan seluruh DTO
            return null;
        }
    }
}
