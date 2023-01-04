package patay.ru.bmatch.jparepository.games;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameRepository  extends JpaRepository<Game, Long> {
    List<Game> findAllByExpirationDateGreaterThanAndStatusEquals(LocalDateTime expirationDate, String status);
}