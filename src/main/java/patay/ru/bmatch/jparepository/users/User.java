package patay.ru.bmatch.jparepository.users;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import patay.ru.bmatch.jparepository.games.Game;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "username", nullable = false)
    private String username;

    public User(String username) {
        this.username = username;
    }
}
