package patay.ru.bmatch.gamelogic.queue;

import patay.ru.bmatch.jparepository.users.User;

import java.util.List;

public class Queue {
    public Integer gameId;
    private final List<User> userList;
    public Integer currentPlayer = 0;

    public Queue(Integer gameId, List<User> userList, Integer currentPlayer) {
        this.gameId = gameId;
        this.userList = userList;
        this.currentPlayer = currentPlayer;
    }

    public void setNextPlayer() {
        currentPlayer = mod(currentPlayer+1, userList.size());
    }

    public Integer getCurrentPlayer() {
        return currentPlayer;
    }

    private int mod(int n, int m) {
        return ((n % m) + m) % m;
    }
}
