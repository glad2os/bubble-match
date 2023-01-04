package patay.ru.bmatch.jparepository.actions;

import org.springframework.data.jpa.repository.JpaRepository;
import patay.ru.bmatch.jparepository.users.User;

public interface ActionRepository extends JpaRepository<Action, Long> {

}
