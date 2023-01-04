package patay.ru.bmatch.jparepository.users;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "current_game", nullable = true)
    private Long current_game;

    @Column(name = "status", nullable = true)
    private String status;

    public User(String username) {
        this.username = username;
    }
}
