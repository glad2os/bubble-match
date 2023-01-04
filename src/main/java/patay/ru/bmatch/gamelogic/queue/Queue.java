package patay.ru.bmatch.gamelogic.queue;

import patay.ru.bmatch.jparepository.users.User;

import java.util.List;

public class Queue {
    public Integer gameId;
    public List<User> userList;
    public Integer nextPlayer = 0;

    public Queue(Integer gameId, List<User> userList, Integer nextPlayer) {
        this.gameId = gameId;
        this.userList = userList;
        this.nextPlayer = nextPlayer;
    }

    public void setNextPlayer() {

    }
}
