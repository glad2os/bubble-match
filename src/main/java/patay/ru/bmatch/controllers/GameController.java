package patay.ru.bmatch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import patay.ru.bmatch.exceptions.GameExpired;
import patay.ru.bmatch.exceptions.ResourceNotFoundException;
import patay.ru.bmatch.jparepository.games.Game;
import patay.ru.bmatch.jparepository.games.GamePlayers;
import patay.ru.bmatch.jparepository.games.GameRepository;
import patay.ru.bmatch.jparepository.users.User;
import patay.ru.bmatch.jparepository.users.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/game/")
public class GameController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @GetMapping("/all")
    public List<Game> getAllEmployees() {
        return gameRepository.findAll();
    }

    @PostMapping("/new")
    public Game createEmployee(TimeZone timezone) {
        Game game = new Game();
        game.setStatus("waiting");
        game.setCreated(LocalDateTime.now());
        game.setExpirationDate(LocalDateTime.now().plusMinutes(5));

        return gameRepository.save(game);
    }


    @PostMapping("/join/")
    public ResponseEntity<?> joinServer(@Validated @RequestBody GamePlayers gamePlayers) throws ResourceNotFoundException, GameExpired {
        User user = userRepository.findById(gamePlayers.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found for this id: " + gamePlayers.getUserId()));
        Game game = gameRepository.findById(gamePlayers.getGameId()).orElseThrow(() -> new ResourceNotFoundException("Game not found for this id: " + gamePlayers.getGameId()));

        LocalDateTime expirationDate = game.getExpirationDate();
        LocalDateTime now = LocalDateTime.now();

        if (now.toEpochSecond(ZoneOffset.UTC) > expirationDate.toEpochSecond(ZoneOffset.UTC)) {
            throw new GameExpired("Not able to connect to expired game");
        }

        if (!game.getStatus().equals("waiting")) {
            throw new GameExpired("The game is already on!");
        }

        List<User> players = game.getPlayers();
        players.add(user);

        gameRepository.save(game);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/leave/")
    public ResponseEntity<?> leaveServer(@Validated @RequestBody GamePlayers gamePlayers) throws ResourceNotFoundException, GameExpired {
        User user = userRepository.findById(gamePlayers.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found for this id: " + gamePlayers.getUserId()));
        Game game = gameRepository.findById(gamePlayers.getGameId()).orElseThrow(() -> new ResourceNotFoundException("Game not found for this id: " + gamePlayers.getGameId()));

        LocalDateTime expirationDate = game.getExpirationDate();
        LocalDateTime now = LocalDateTime.now();

        if (now.toEpochSecond(ZoneOffset.UTC) > expirationDate.toEpochSecond(ZoneOffset.UTC)) {
            throw new GameExpired("Not able to connect to expired game");
        }

        if (!game.getStatus().equals("waiting")) {
            throw new GameExpired("The game is already on!");
        }

        game.getPlayers().remove(user);

        gameRepository.save(game);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @MessageMapping("/connect")
    @SendTo("/game/users")
    public List<User> greeting(GamePlayers gamePlayers) throws ResourceNotFoundException {
        Game game = gameRepository.findById(gamePlayers.getGameId()).orElseThrow(() -> new ResourceNotFoundException("Game not found for this id: " + gamePlayers.getGameId()));

        return game.getPlayers() ;
    }
}
