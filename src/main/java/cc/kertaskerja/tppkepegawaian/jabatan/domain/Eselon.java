package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Eselon {

    ESELON_I("ESELON_I"),
    ESELON_II("ESELON_II"),
    ESELON_III("ESELON_III"),
    ESELON_IV("ESELON_IV"),
    II_A("II_A"),
    II_B("II_B"),
    III_A("III_A"),
    III_B("III_B"),
    IV_A("IV_A"),
    IV_B("IV_B"),
    EKS_IV_A("EKS_IV_A"),
    PELAKSANA("PELAKSANA"),
    JF("JF"),
    NON_ESELON("NON_ESELON"),
    NON_STRUKTURAL_A("NON_STRUKTURAL_(A)");

    private final String value;

    Eselon(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static Eselon fromValue(String value) {
        for (Eselon e : values()) {
            if (e.value.equalsIgnoreCase(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown Eselon value: " + value);
    }
}
