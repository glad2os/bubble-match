package patay.ru.bmatch.gamelogic.area;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record Generator(Integer[] area) {

    public static final Integer x = 10;
    public static final Integer y = 10;

    public static Integer[] generateArea() {
        int areaLength = y * x;
        Integer[] area = new Integer[areaLength];

        int _tmpCounter = 1;

        for (int i = 0; i < areaLength / 2; i++) {
            area[i] = _tmpCounter;
            area[99-i] = _tmpCounter;
            _tmpCounter++;
        }
        List<Integer> list = new java.util.ArrayList<>(Arrays.stream(area).toList());

        Collections.shuffle(list);


        return  list.toArray(Integer[]::new);
    }
}



