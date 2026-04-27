package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import java.util.Map;

public final class JabatanPriority {

    private JabatanPriority() {}

    private static final Map<String, Integer> PRIORITY_MAP = Map.of(

        "JABATAN_PEMIMPIN_TINGGI_PRATAMA", 0,
        "JABATAN_PEMIMPIN_TINGGI", 0,
        "JABATAN_STRUKTURAL", 1,
        "JABATAN_FUNGSIONAL", 2,
        "JABATAN_ADMINISTRASI", 3,
        "JABATAN_ADMINISTRATOR", 3,
        "JABATAN_PENGAWAS", 4,
        "PELAKSANA", 5,
        "BELUM_DIATUR", 10

    );

    public static int getPriority(String jenisJabatan) {

        return PRIORITY_MAP.getOrDefault(jenisJabatan, 99);

    }

}
