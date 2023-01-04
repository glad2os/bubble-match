package patay.ru.bmatch.jparepository.games;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AvailableGames {
    private Long id;
    private String status;
    private Integer registeredPlayers;
    private LocalDateTime created;
    private LocalDateTime expirationDate;
}
