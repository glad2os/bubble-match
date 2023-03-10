package patay.ru.bmatch.jparepository.games;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import patay.ru.bmatch.jparepository.users.User;

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

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<User> players = new ArrayList<>();

    @Column(name = "status")
    private String status;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "expirationDate", nullable = false)
    private LocalDateTime expirationDate;

    @ElementCollection
    @JsonIgnore
    public List<Integer> area;
}
