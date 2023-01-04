package patay.ru.bmatch.gamelogic.area;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AreaBubbles {

    public Integer id;
    public Integer[] area;
    private final Integer[] GENERATED_AREA;
    public Integer countOpened = 0;

    public AreaBubbles(Integer id) {
        this.id = id;
        GENERATED_AREA = Generator.generateArea();
    }

    public Integer get(Integer number) {
        return GENERATED_AREA[number];
    }

    public void open(int number, int number2) {
        area[number] = GENERATED_AREA[number];
        area[number2] = GENERATED_AREA[number2];
        countOpened++;
    }

}
