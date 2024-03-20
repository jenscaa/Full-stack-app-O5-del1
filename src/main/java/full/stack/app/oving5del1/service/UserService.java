package full.stack.app.oving5del1.service;

import full.stack.app.oving5del1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  UserRepository userRepository;

  public boolean isCredentialValid(String username, String password) {
    return userRepository.isInDatabase(username, password);
  }

}
