package cc.kertaskerja.tppkepegawaian.domain.jabatan;

import java.util.Comparator;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JabatanPriority;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithTppPajakResponse;

public final class JabatanUtils {

    private JabatanUtils() {
    }

    public static Comparator<Jabatan> sortByJenisPriority() {

        return Comparator

                .comparingInt((Jabatan j) -> JabatanPriority.getPriority(j.jenisJabatan()))

                .thenComparing(Jabatan::nip);

    }

    public static Comparator<JabatanWithTppPajakResponse> sortJabatanTppPajakByJenisPriority() {

        return Comparator

                .comparingInt((JabatanWithTppPajakResponse j) -> JabatanPriority.getPriority(j.jenisJabatan()))

                .thenComparing(JabatanWithTppPajakResponse::nip);

    }

}
