package patay.ru.bmatch.jparepository.games;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import patay.ru.bmatch.jparepository.users.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue
    private Long id;

    public Game(Long id) {
        this.id = id;
    }

    @OneToMany(
            cascade = CascadeType.ALL
    )
    private List<User> players = new ArrayList<>();;

    @Column(name = "status")
    private String status;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "expirationDate", nullable = false)
    private LocalDateTime expirationDate;

}
