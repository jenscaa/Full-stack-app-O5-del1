package full.stack.app.oving5del1.controller;

import full.stack.app.oving5del1.dto.CalculationDTO;
import full.stack.app.oving5del1.dto.UserDTO;
import full.stack.app.oving5del1.model.Calculation;
import full.stack.app.oving5del1.model.User;
import full.stack.app.oving5del1.repository.CalculationRepository;
import full.stack.app.oving5del1.repository.UserRepository;
import full.stack.app.oving5del1.service.CalculateService;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/oving5del1")
public class Controller {

  @Autowired
  UserRepository userRepository;
  @Autowired
  CalculationRepository calculationRepository;
  @Autowired
  private CalculateService calculateService;

  private final Logger logger = LoggerFactory.getLogger(Controller.class);

  @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> createUser(@RequestBody UserDTO userDTO) {
    try {
      userRepository.save(new User(userDTO.getUsername(),userDTO.getPassword()));
      logger.info("New user was created");
      return new ResponseEntity<>("User was created successfully.", HttpStatus.CREATED);
    } catch (Exception e) {
      logger.error("Error when attempting to create new user");
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }



  @PostMapping("/calculations")
  public ResponseEntity<Double> createCalculation(@RequestBody CalculationDTO calculationDTO) {
    try {
      String username = calculationDTO.getUsername();
      String expression = calculationDTO.getExpression();
      User user = userRepository.findByUsername(username);
      if (user == null) {
        logger.info("User don't exists in database");
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
      }

      Integer userId = userRepository.findIdByUsername(user.getUsername());
      double answer = calculateService.calculateExpression(expression);
      System.out.println(answer);
      calculationRepository.save(new Calculation(expression, expression + "=" + answer), userId);
      logger.info("Calculation added");
      return new ResponseEntity<>(answer, HttpStatus.CREATED);

    } catch (Exception e) {
      logger.info("Something bad happened here. IDK.");
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> logIn(@RequestBody UserDTO userDTO) {
    try {
      if(calculateService.authenticate(userDTO)) {
        logger.info("User authenticated");
        return new ResponseEntity<>("User was logged in successfully.", HttpStatus.ACCEPTED);
      }
      return new ResponseEntity<>("User was not logged in successfully.", HttpStatus.BAD_REQUEST);
    }catch (Exception e) {
      logger.error("Error occured during authentication");
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value = "/logOut")
  public ResponseEntity<String> logOut(@RequestBody String username) {
    try {
      if(calculateService.logOut(username)) {
        logger.info("User: " + username + " logged off");
        return new ResponseEntity<>("User was logged out successfully.", HttpStatus.ACCEPTED);
      }
      return new ResponseEntity<>("User was not logged out successfully.", HttpStatus.BAD_REQUEST);
    }catch (Exception e) {
      logger.error("Error occured during logging off");
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/users/{username}")
  public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) {
    User user = userRepository.findByUsername(username);

    if (user != null) {
      return new ResponseEntity<>(user, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers() {
    try {
      List<User> users = new ArrayList<User>();
      userRepository.findAllUsers().forEach(users::add);
      if (users.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(users, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("calculations/min10calculations")
  public ResponseEntity<List<Calculation>> getMin10CalculationsFromUser(@RequestBody String username) {
    try {
      List<Calculation> calculations = calculationRepository.find10CalculationsFromUsername(username);
      if (calculations.isEmpty()) {
        logger.info("No calculations is made");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      logger.info("Calculations picked");
      return new ResponseEntity<>(calculations, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Some bad shit happened here");
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
