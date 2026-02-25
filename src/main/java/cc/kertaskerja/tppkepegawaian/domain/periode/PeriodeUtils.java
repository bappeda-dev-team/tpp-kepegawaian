package cc.kertaskerja.tppkepegawaian.domain.periode;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class PeriodeUtils {

    private PeriodeUtils() {
    }

    public static <T extends HasPeriode, K> Map<K, T> latestPerKeyUntil(
            List<T> data,
            Integer bulan,
            Integer tahun,
            Function<T, K> keyExtractor) {

        Comparator<T> comparator = Comparator
                .comparing((T t) -> t.bulan())
                .thenComparing(HasPeriode::tahun);

        return data.stream()
                // filter <= target periode
                .filter(t -> t.tahun() < tahun ||
                        (t.tahun().equals(tahun) && t.bulan() <= bulan))
                // ambil latest per key
                .collect(Collectors.toMap(
                        keyExtractor,
                        Function.identity(),
                        (t1, t2) -> comparator.compare(t1, t2) > 0 ? t1 : t2));
    }
}
