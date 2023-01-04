package patay.ru.bmatch.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import patay.ru.bmatch.users.User;
import patay.ru.bmatch.users.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public List<User> getAllEmployees() {
        return userRepository.findAll();
    }

    @PostMapping("/create")
    public User createEmployee(@Validated @RequestBody User user) {
        return userRepository.save(user);
    }
}
