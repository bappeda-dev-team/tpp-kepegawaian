package cc.kertaskerja.tppkepegawaian.jabatan.domain;

public enum JenisJabatan {
    JABATAN_PEMIMPIN_TINGGI_PRATAMA,
    JABATAN_PEMIMPIN_TINGGI,
    JABATAN_STRUKTURAL,
    JABATAN_FUNGSIONAL,
    JABATAN_ADMINISTRASI,
    JABATAN_ADMINISTRATOR,
    JABATAN_PENGAWAS,
    PELAKSANA,
    BELUM_DIATUR;

    public static JenisJabatan from(String value) {

        try {

            return JenisJabatan.valueOf(value);

        } catch (Exception e) {

            return BELUM_DIATUR;

        }

    }
}
