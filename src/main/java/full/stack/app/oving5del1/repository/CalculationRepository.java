package full.stack.app.oving5del1.repository;

import full.stack.app.oving5del1.model.Calculation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CalculationRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public int save(Calculation calculation, int userId) {
    return jdbcTemplate.update("INSERT INTO calculations (user_id , expression, answer  ) VALUES(?,?,?)",
        userId, calculation.getExpression(), calculation.getAnswer());
  }

  public List<Calculation> findAllCalculations() {
    return jdbcTemplate.query("SELECT * from calculations", BeanPropertyRowMapper.newInstance(Calculation.class));
  }

  public List<Calculation> find10CalculationsFromUsername(String username) {
    return jdbcTemplate.query(
        "SELECT c.expression, c.answer from calculations c "
            + "JOIN users u ON c.user_id = u.user_id "
            + "WHERE u.username=? "
            + "ORDER BY c.calculation_id DESC "
            + "LIMIT 10"
        , BeanPropertyRowMapper.newInstance(Calculation.class), username);
  }

  public int deleteAllCalculations() {
    return jdbcTemplate.update("DELETE from calculations");
  }
}
