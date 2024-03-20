package full.stack.app.oving5del1.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import full.stack.app.oving5del1.dto.UserDTO;
import full.stack.app.oving5del1.model.User;
import full.stack.app.oving5del1.repository.UserRepository;
import full.stack.app.oving5del1.service.CalculateService;
import full.stack.app.oving5del1.service.UserService;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/token")
@CrossOrigin
public class TokenController {

  @Autowired
  UserService userService;
  @Autowired
  CalculateService calculateService;
  @Autowired
  UserRepository userRepository;
  private final Logger logger = LoggerFactory.getLogger(UserService.class);
  public final static String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
  private static final Duration JWT_TOKEN_VALIDITY = Duration.ofMinutes(5);

  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> login(@RequestBody UserDTO userDTO) {
    try {
      if (calculateService.authenticate(userDTO)) {
        String token = generateToken(userDTO.getUsername());
        logger.info("Token created!");
        return new ResponseEntity<>(token, HttpStatus.CREATED);
      }
      logger.error("Failed to generate token");
      return new ResponseEntity<>("Failed to generate token", HttpStatus.BAD_REQUEST);

    } catch (Exception e) {
      logger.error("Error occurred during authentication");
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> registerNewUser(@RequestBody UserDTO userDTO) {
    try {
      if (calculateService.activeUsers.add(userDTO.getUsername())) {
        userRepository.save(new User(userDTO.getUsername(),userDTO.getPassword()));
        logger.info("New user was created: " + userDTO.getUsername());
        String token = generateToken(userDTO.getUsername());
        logger.info("Token was generated for user: " + userDTO.getUsername());
        return new ResponseEntity<>(token, HttpStatus.CREATED);
      }
      else return new ResponseEntity<>("Failed to register or login", HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      logger.error("Error when attempting to create new user");
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private String generateToken(final String userId) {
    final Instant now = Instant.now();
    final Algorithm hmac512 = Algorithm.HMAC512(SECRET_KEY);
    // final JWTVerifier verifier = JWT.require(hmac512).build();
    return JWT.create()
        .withSubject(userId)
        .withIssuer("jens christian aanestad")
        .withIssuedAt(now)
        .withExpiresAt(now.plusMillis(JWT_TOKEN_VALIDITY.toMillis()))
        .sign(hmac512);
  }
}
