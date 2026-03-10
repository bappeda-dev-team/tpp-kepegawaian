package cc.kertaskerja.tppkepegawaian.domain.periode;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class PeriodeUtils {

    private PeriodeUtils() {
    }

    public static <T extends HasPeriode & HasId, K> Map<K, T> latestPerKeyUntil(
            List<T> data,
            Integer bulan,
            Integer tahun,
            Function<T, K> keyExtractor) {

        Comparator<T> comparator =
                Comparator.comparingInt(T::tahun)
                        .thenComparingInt(T::bulan)
                        .thenComparing(HasId::id);

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

    public static <T extends HasPeriode & HasId, K> Map<K, T> latestPerKeyFlexible(
            List<T> data,
            Integer bulan,
            Integer tahun,
            Function<T, K> keyExtractor) {

        Map<K, List<T>> grouped = data.stream()
                .collect(Collectors.groupingBy(keyExtractor));

        Comparator<T> comparator = Comparator
                .comparingInt(T::tahun)
                .thenComparingInt(T::bulan)
                .thenComparing(HasId::id);

        Map<K, T> result = new HashMap<>();

        for (var entry : grouped.entrySet()) {

            List<T> items = entry.getValue();

            Optional<T> latestBefore = items.stream()
                    .filter(t -> t.tahun() < tahun ||
                            (t.tahun().equals(tahun) && t.bulan() <= bulan))
                    .max(comparator);

            if (latestBefore.isPresent()) {
                result.put(entry.getKey(), latestBefore.get());
            } else {
                T earliest = items.stream()
                        .min(comparator)
                        .orElseThrow();

                result.put(entry.getKey(), earliest);
            }
        }

        return result;
    }
}
