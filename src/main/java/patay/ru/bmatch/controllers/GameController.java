package patay.ru.bmatch.controllers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import patay.ru.bmatch.exceptions.GameExpiredException;
import patay.ru.bmatch.exceptions.ResourceNotFoundException;
import patay.ru.bmatch.exceptions.UserAlreadyInGameException;
import patay.ru.bmatch.gamelogic.area.Generator;
import patay.ru.bmatch.jparepository.games.AvailableGames;
import patay.ru.bmatch.jparepository.games.Game;
import patay.ru.bmatch.jparepository.games.GamePlayers;
import patay.ru.bmatch.jparepository.games.GameRepository;
import patay.ru.bmatch.jparepository.users.Token;
import patay.ru.bmatch.jparepository.users.User;
import patay.ru.bmatch.jparepository.users.UserRepository;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@RestController
@RequestMapping("/api/v1/game/")
public class GameController {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final SecretKey key;

    public GameController(UserRepository userRepository, GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode("eiw8euDie8FahhooRee3aokai2lu1ia2caegu7kohloh6iex9ahFefei5shoogot2ofaetheexi0moh0Thahz4ea2iexooTh9omibieshaeQuouhohv2oegohm6ahdiepeat3oofeiceeshu6joow5Shoo5eechah6eeDohxei8Ehaic3gisai4oolaegair5ee2moh8uPiphirei6ieShaxeig3oorux9shu7via8mawooth5sooVohfiuthaif"));
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
            throw new GameExpiredException("Not able to connect to expired game");
        }

        if (!game.getStatus().equals("waiting")) {
            throw new GameExpiredException("The game is already on!");
        }

        List<User> players = game.getPlayers();

        if (players.contains(user)) {
            throw new UserAlreadyInGameException("The user is already in game!");
        }

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
                throw new GameExpiredException("Not able to connect to expired game");
        }

        if (!game.getStatus().equals("waiting")) {
            throw new GameExpiredException("The game is already on!");
        }

        List<User> players = game.getPlayers();
        if (!players.contains(user)) {
            throw new UserAlreadyInGameException("The user is not in the game!");
        }
        players.remove(user);

        gameRepository.save(game);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @MessageMapping("/join")
    @SendTo("/game/users")
    public String joinServer(Token token) throws ResourceNotFoundException {


        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.getToken());

        Optional<User> currentUser = userRepository.findById(Long.valueOf(claimsJws.getHeader().get("id").toString()));


        return "";
    }

    @MessageMapping("/connect")
    @SendTo("/game/users")
    public List<User> greeting(GamePlayers gamePlayers) throws ResourceNotFoundException {
        try {
            Game game = gameRepository.findById(gamePlayers.getGameId()).orElseThrow(() -> new ResourceNotFoundException("Game not found for this id: " + gamePlayers.getGameId()));

            // TODO: time validation

            if(game.getStatus() == "open"){
                return game.getPlayers();
            }

        } catch (RuntimeException e){

        }
        return null;
    }
}
