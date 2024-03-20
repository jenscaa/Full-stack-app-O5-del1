package full.stack.app.oving5del1.repository;

import full.stack.app.oving5del1.model.User;
import java.util.List;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public int save(User user) {
    return jdbcTemplate.update("INSERT INTO users (username, user_password) VALUES(?,?)",
        new Object[] { user.getUsername(), user.getUserPassword() });
  }

  public int update(User user) {
    return jdbcTemplate.update("UPDATE users SET usernaame=?, user_password=? WHERE id=?",
        new Object[] { user.getUsername(), user.getUserPassword() });
  }

  public User findUserById(int userId) {
    try {
      User user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id=?",
          BeanPropertyRowMapper.newInstance(User.class), userId);

      return user;
    } catch (IncorrectResultSizeDataAccessException e) {
      return null;
    }
  }

  public int findIdByUsername(String username) {
    try {
      Integer id = jdbcTemplate.queryForObject(
          "SELECT user_id FROM users WHERE username=?",
          Integer.class,
          username
      );

      return id != null ? id : 0;
    } catch (IncorrectResultSizeDataAccessException e) {
      return 0;
    }
  }

  public boolean isInDatabase(String username, String password) {
    List<User> userList = findAllUsers();
    for (User user : userList) {
      if (Objects.equals(user.getUsername(), username) &&
          Objects.equals(user.getUserPassword(), password)) {
        return true;
      }
    }
    return false;
  }

  public User findByUsername(String username) {
    try {
      User user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE username=?",
          BeanPropertyRowMapper.newInstance(User.class), username);

      return user;
    } catch (IncorrectResultSizeDataAccessException e) {
      return null;
    }
  }

  public int deleteUserById(int userId) {
    return jdbcTemplate.update("DELETE FROM users WHERE user_id=?", userId);
  }

  public int deleteUserByUsername(String username) {
    return jdbcTemplate.update("DELETE FROM users WHERE username=?", username);
  }

  public List<User> findAllUsers() {
    return jdbcTemplate.query("SELECT * from users", BeanPropertyRowMapper.newInstance(User.class));
  }

  public int deleteAllUsers() {
    return jdbcTemplate.update("DELETE from users");
  }
}