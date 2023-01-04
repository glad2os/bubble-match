package patay.ru.bmatch.jparepository.games;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import patay.ru.bmatch.jparepository.users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
}
