package patay.ru.bmatch.gamelogic.area;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record Generator(Integer[] area) {

    public static List<Integer> generateArea() {
        return IntStream.range(0, 50).flatMap(i -> IntStream.of(i, i)).boxed().collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
            Collections.shuffle(list);
            return list;
        }));
    }
}



