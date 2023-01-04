package patay.ru.bmatch.users;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    private Long id;
    private String username;

    public User(String username) {
        this.username = username;
    }

    public User() {

    }

    @Column(name = "username", nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
}
