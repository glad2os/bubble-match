package patay.ru.bmatch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import patay.ru.bmatch.exceptions.ResourceNotFoundException;
import patay.ru.bmatch.jparepository.games.Game;
import patay.ru.bmatch.jparepository.games.GamePlayers;
import patay.ru.bmatch.jparepository.games.GameRepository;
import patay.ru.bmatch.jparepository.users.User;
import patay.ru.bmatch.jparepository.users.UserRepository;

import java.util.*;

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
    public Game createEmployee() {
        return gameRepository.save(new Game());
    }


    @PostMapping("/join/")
    public ResponseEntity<?> joinServer(@Validated @RequestBody GamePlayers gamePlayers) throws ResourceNotFoundException {
        User user = userRepository.findById(gamePlayers.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found for this id: " + gamePlayers.getUserId()));
        Game game = gameRepository.findById(gamePlayers.getGameId()).orElseThrow(() -> new ResourceNotFoundException("Game not found for this id: " + gamePlayers.getGameId()));

        List<User> players = game.getPlayers();
        players.add(user);

        gameRepository.save(game);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/leave/")
    public ResponseEntity<?> leaveServer(@Validated @RequestBody GamePlayers gamePlayers) throws ResourceNotFoundException {
        User user = userRepository.findById(gamePlayers.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found for this id: " + gamePlayers.getUserId()));
        Game game = gameRepository.findById(gamePlayers.getGameId()).orElseThrow(() -> new ResourceNotFoundException("Game not found for this id: " + gamePlayers.getGameId()));

        game.getPlayers().remove(user);

        gameRepository.save(game);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
