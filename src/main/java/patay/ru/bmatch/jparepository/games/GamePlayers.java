package patay.ru.bmatch.jparepository.games;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GamePlayers {
    private Long userId;
    private Long gameId;
}
