package patay.ru.bmatch.controllers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import patay.ru.bmatch.exceptions.RegexPasswordException;
import patay.ru.bmatch.exceptions.ResourceNotFoundException;
import patay.ru.bmatch.exceptions.UserNotFoundException;
import patay.ru.bmatch.jparepository.users.Token;
import patay.ru.bmatch.jparepository.users.User;
import patay.ru.bmatch.jparepository.users.UserRepository;

import javax.crypto.SecretKey;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users/")
public class UserController {
    private final UserRepository userRepository;
    private final SecretKey key;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode("eiw8euDie8FahhooRee3aokai2lu1ia2caegu7kohloh6iex9ahFefei5shoogot2ofaetheexi0moh0Thahz4ea2iexooTh9omibieshaeQuouhohv2oegohm6ahdiepeat3oofeiceeshu6joow5Shoo5eechah6eeDohxei8Ehaic3gisai4oolaegair5ee2moh8uPiphirei6ieShaxeig3oorux9shu7via8mawooth5sooVohfiuthaif"));
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

    @PostMapping("/token")
    public ResponseEntity<User> validateUserByToken(@Validated @RequestBody Token token) throws JwtException {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.getToken());

        User user = new User();
        user.setId(Long.valueOf(claimsJws.getHeader().get("id").toString()));
        user.setUsername((String) claimsJws.getBody().get("sub"));

        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/validate")
    public ResponseEntity<Token> getTokenByUser(@Validated @RequestBody User user) throws RuntimeException {
        User userByUsernameAndAndPassword = userRepository.findUserByUsernameAndAndPassword(user.getUsername(), user.getPassword());

        System.out.println(userByUsernameAndAndPassword);

        if (userByUsernameAndAndPassword == null) {
            throw new UserNotFoundException("User not found!");
        }

        String jws = Jwts.builder().setHeaderParam("id", userByUsernameAndAndPassword.getId())
                .setSubject(userByUsernameAndAndPassword.getUsername())
                .signWith(key).compact();

        Token token = new Token();
        token.setToken(jws);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping("/create")
    public User createUser(@Validated @RequestBody User user) {
        if (!passwordValidates(user.getPassword())) {
            throw new RegexPasswordException("Password should contains: At least one digit [0-9], At least one lowercase character [a-z], At least one uppercase character [A-Z]," + "At least one special character, At least 8 characters in length, but no more than 32. ");
        }

        return userRepository.save(user);
    }

    public boolean passwordValidates(String pass) {
        int count = 0;

        if (8 <= pass.length() && pass.length() <= 32) {
            if (pass.matches(".*\\d.*")) count++;
            if (pass.matches(".*[a-z].*")) count++;
            if (pass.matches(".*[A-Z].*")) count++;
            if (pass.matches(".*[*.!@#$%^&(){}\\[]:\";'<>,.?/~`_+-=|\\].*")) count++;
        }

        return count >= 3;
    }
}
