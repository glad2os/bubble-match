package patay.ru.bmatch.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import patay.ru.bmatch.exceptions.GameExpiredException;
import patay.ru.bmatch.exceptions.ResourceNotFoundException;
import patay.ru.bmatch.gamelogic.area.Generator;
import patay.ru.bmatch.jparepository.games.AvailableGames;
import patay.ru.bmatch.jparepository.games.Game;
import patay.ru.bmatch.jparepository.games.GamePlayers;
import patay.ru.bmatch.jparepository.games.GameRepository;
import patay.ru.bmatch.jparepository.users.User;
import patay.ru.bmatch.jparepository.users.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/game/")
public class GameController {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public GameController(UserRepository userRepository, GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }


    @GetMapping("/all")
    public List<Game> getAllEmployees() {
        return gameRepository.findAll();
    }

    @GetMapping("/search")
    public List<AvailableGames> searchAvailableGames(TimeZone timezone) {
        List<AvailableGames> availableGames = new ArrayList<>();

        var waiting = gameRepository.findAllByExpirationDateGreaterThanAndStatusEquals(LocalDateTime.now(), "waiting").stream().limit(10);
        waiting.forEach(game -> {

            AvailableGames availableGame = new AvailableGames();
            availableGame.setId(game.getId());
            availableGame.setStatus(game.getStatus());
            availableGame.setExpirationDate(game.getExpirationDate());
            availableGame.setCreated(game.getCreated());
            availableGame.setRegisteredPlayers(game.getPlayers().size());

            availableGames.add(availableGame);
        });

        return availableGames;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> gerUserById(@PathVariable(value = "id") Long gameId) throws ResourceNotFoundException {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new ResourceNotFoundException("Game not found for this id: " + gameId));
        return ResponseEntity.ok().body(game);
    }

    @PostMapping("/new")
    public ResponseEntity<Game> createEmployee(TimeZone timezone) {
        Game game = new Game();
        game.setStatus("waiting");
        game.setCreated(LocalDateTime.now());
        game.setExpirationDate(LocalDateTime.now().plusMinutes(5));
        game.setArea(Generator.generateArea());
        gameRepository.save(game);

        return new ResponseEntity<>(game, HttpStatus.CREATED);
    }


    @PostMapping("/join/")
    public ResponseEntity<?> joinServer(@Validated @RequestBody GamePlayers gamePlayers) throws ResourceNotFoundException, GameExpiredException {
        User user = userRepository.findById(gamePlayers.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found for this id: " + gamePlayers.getUserId()));
        Game game = gameRepository.findById(gamePlayers.getGameId()).orElseThrow(() -> new ResourceNotFoundException("Game not found for this id: " + gamePlayers.getGameId()));

        LocalDateTime expirationDate = game.getExpirationDate();
        LocalDateTime now = LocalDateTime.now();

        if (now.toEpochSecond(ZoneOffset.UTC) > expirationDate.toEpochSecond(ZoneOffset.UTC)) {
         //   throw new GameExpiredException("Not able to connect to expired game");
        }

        if (!game.getStatus().equals("waiting")) {
            throw new GameExpiredException("The game is already on!");
        }

        List<User> players = game.getPlayers();
        players.add(user);

        gameRepository.save(game);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/leave/")
    public ResponseEntity<?> leaveServer(@Validated @RequestBody GamePlayers gamePlayers) throws ResourceNotFoundException, GameExpiredException {
        User user = userRepository.findById(gamePlayers.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found for this id: " + gamePlayers.getUserId()));
        Game game = gameRepository.findById(gamePlayers.getGameId()).orElseThrow(() -> new ResourceNotFoundException("Game not found for this id: " + gamePlayers.getGameId()));

        LocalDateTime expirationDate = game.getExpirationDate();
        LocalDateTime now = LocalDateTime.now();

        if (now.toEpochSecond(ZoneOffset.UTC) > expirationDate.toEpochSecond(ZoneOffset.UTC)) {
        //    throw new GameExpiredException("Not able to connect to expired game");
        }

        if (!game.getStatus().equals("waiting")) {
            throw new GameExpiredException("The game is already on!");
        }

        game.getPlayers().remove(user);

        gameRepository.save(game);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @MessageMapping("/connect")
    @SendTo("/game/users")
    public List<User> greeting(GamePlayers gamePlayers) throws ResourceNotFoundException {
        Game game = gameRepository.findById(gamePlayers.getGameId()).orElseThrow(() -> new ResourceNotFoundException("Game not found for this id: " + gamePlayers.getGameId()));

        return game.getPlayers();
    }
}
