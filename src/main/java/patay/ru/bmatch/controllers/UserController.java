package patay.ru.bmatch.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import patay.ru.bmatch.exceptions.ResourceNotFoundException;
import patay.ru.bmatch.jparepository.users.User;
import patay.ru.bmatch.jparepository.users.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> gerUserById(@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found for this id: " + userId));
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/create")
    public User createUser(@Validated @RequestBody User user) {
        return userRepository.save(user);
    }
}
