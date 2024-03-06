package full.stack.app.oving5del1.dto;

public class CalculationDTO {
  private String username;
  private String expression;

  public CalculationDTO() {

  }

  public CalculationDTO(String username, String expression) {
    this.username = username;
    this.expression = expression;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }
}
