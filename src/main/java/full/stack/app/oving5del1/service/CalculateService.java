package full.stack.app.oving5del1.service;

import full.stack.app.oving5del1.dto.UserDTO;
import full.stack.app.oving5del1.model.User;
import full.stack.app.oving5del1.repository.CalculationRepository;
import full.stack.app.oving5del1.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalculateService {

  @Autowired
  CalculationRepository calculationRepository;
  @Autowired
  UserRepository userRepository;
  private final Logger logger = LoggerFactory.getLogger(CalculateService.class);

  private final Set<String> activeUsers = new HashSet<>();

  public boolean authenticate(UserDTO userDTO) {
    User user = userRepository.findByUsername(userDTO.getUsername());
    if (user == null) {
      logger.info("User: " + userDTO.getUsername() + " does not exists in database");
      return false;
    }

    if (Objects.equals(user.getUsername(), userDTO.getUsername()) && Objects.equals(
        user.getUserPassword(), userDTO.getPassword()) && activeUsers.add(user.getUsername())) {
      logger.info("User: " + userDTO.getUsername() + " is now active");
      return true;
    }

    logger.info("User " + userDTO.getUsername() + " is already active!");
    return false;
  }

  public boolean logOut(String username) {
    User user = userRepository.findByUsername(username);
    if (user == null ) {
      logger.warn("The user does not exists and is yet trying to log off");
      return false;
    }

    if (!activeUsers.contains(username)) {
      logger.warn("The user is not active and is yet trying to log off");
      return false;
    }

    if (activeUsers.remove(username)) {
      logger.info("User: " + username + " is no longer in active list");
      return true;
    }
    return false;
  }

  private char findOperand(String operands) {
    int numberOfMinuses = 0;
    for (int i = 0; i < operands.length(); i++) {
      if (operands.charAt(i) == '-') {
        numberOfMinuses++;
      }
    }
    if (numberOfMinuses % 2 == 0) {
      return '+';
    }
    return '-';
  }

  public double calculateExpression(String expression) throws IllegalArgumentException {

    HashMap<Integer, Double> numbers = new HashMap<Integer, Double>();
    HashMap<Integer, String> operands = new HashMap<Integer, String>();
    HashMap<Integer, Character> stars = new HashMap<Integer, Character>();
    HashMap<Integer, String> doubleStars = new HashMap<Integer, String>();

    StringBuilder foundExpression = new StringBuilder();

    // Wrapping up and reorganize expression
    for (int i = 0; i < expression.length(); i++) {
      char ch = expression.charAt(i);

      // Wrap up stars
      if (ch == '*') {
        int firstIndex = i;
        int amountOfStars = 1;
        while (expression.charAt(i + 1) == '*' && i + 1 != expression.length()) {
          i++;
          ch = expression.charAt(i);
          amountOfStars++;
          if (amountOfStars > 2) {
            logger.error("Too many stars in expression!");
            throw new IllegalArgumentException("Too many stars in expression!");
          }
        }
        if (amountOfStars == 1) {
          stars.put(firstIndex, '*');
        } else {
          doubleStars.put(firstIndex, "**");
        }
      }

      // Wrap up operands
      if (ch == '+' || ch == '-') {
        int firstIndex = i;
        StringBuilder sb = new StringBuilder();
        sb.append(ch);
        while (i + 1 != expression.length() && (expression.charAt(i + 1) == '+' || expression.charAt(i + 1) == '-')) {
          i++;
          ch = expression.charAt(i);
          sb.append(ch);
        }
        operands.put(firstIndex, String.valueOf(sb));
      }

      // Wrap up numbers
      if (Character.isDigit(ch)) {
        int firstIndex = i;
        int decimalPoint = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(ch);
        while (i + 1 != expression.length() && (Character.isDigit(expression.charAt(i + 1)) || expression.charAt(i + 1) == '.')) {
          i++;
          ch = expression.charAt(i);

          if (ch == '.') {
            decimalPoint++;
          }
          if (decimalPoint > 1) {
            logger.error("Too many decimals in expression!");
            throw new IllegalArgumentException("Too many decimals in expression"
                + "!");
          }
          sb.append(ch);
        }
        numbers.put(firstIndex, Double.parseDouble(String.valueOf(sb)));
      }
    }

    // Combining signs and numbers
    // After this we don't have to care about signs
    for (int index : operands.keySet()) {
      if (findOperand(operands.get(index)) == '-') {
        numbers.replace(index + operands.get(index).length(), numbers.get(index + operands.get(index).length()) * -1);
      }
    }

    // Combining exponents and numbers
    List<Integer> indexList = new ArrayList<>();
    indexList.addAll(numbers.keySet());
    indexList.addAll(doubleStars.keySet());

    List<Integer> numberIndexList = new ArrayList<>(numbers.keySet());
    List<Integer> doubleStarsIndexList = new ArrayList<>(doubleStars.keySet());

    Collections.sort(indexList);
    Collections.sort(numberIndexList);
    Collections.sort(doubleStarsIndexList);

    for (int index : doubleStarsIndexList) {
      int closestNumberIndexLeft = 0;
      int closestNumberIndexRight = 0;
      for (int i = 0; i < numberIndexList.size(); i++) {
        if (indexList.get(i) < index) {
          closestNumberIndexLeft = indexList.get(i);
        } else {
          break;
        }
      }
      for (int i = numberIndexList.size(); i > 0; i--) {
        if (indexList.get(i) > index) {
          closestNumberIndexRight = indexList.get(i);
        } else {
          break;
        }
      }

      if (numbers.get(closestNumberIndexLeft) < 0) {
        numbers.replace(closestNumberIndexLeft, -Math.pow(Math.abs(numbers.get(closestNumberIndexLeft)), numbers.get(closestNumberIndexRight)));
      } else {
        numbers.replace(closestNumberIndexLeft, Math.pow(numbers.get(closestNumberIndexLeft), numbers.get(closestNumberIndexRight)));
      }
      numbers.remove(closestNumberIndexRight);
    }

    // Combining multiplication and numbers
    List<Integer> indexList2 = new ArrayList<>();
    indexList2.addAll(numbers.keySet());
    indexList2.addAll(stars.keySet());

    List<Integer> numberIndexList2 = new ArrayList<>(numbers.keySet());
    List<Integer> starsIndexList = new ArrayList<>(stars.keySet());

    Collections.sort(indexList2);
    Collections.sort(numberIndexList2);
    Collections.sort(starsIndexList);

    for (int index : starsIndexList) {
      int closestNumberIndexLeft = 0;
      int closestNumberIndexRight = 0;
      for (int i = 0; i < numberIndexList2.size(); i++) {
        if (numberIndexList2.get(i) < index) {
          closestNumberIndexLeft = numberIndexList2.get(i);
        } else {
          break;
        }
      }
      for (int i = numberIndexList2.size() - 1; i > 0; i--) {
        if (numberIndexList2.get(i) > index) {
          closestNumberIndexRight = numberIndexList2.get(i);
        } else {
          break;
        }
      }
      numbers.replace(closestNumberIndexRight, numbers.get(closestNumberIndexLeft) * numbers.get(closestNumberIndexRight));
      numbers.replace(closestNumberIndexLeft, 0.0);

    }

    double sum = 0;
    for (Double i : numbers.values()) {
      sum += i;
    }

    return sum;
  }

}
