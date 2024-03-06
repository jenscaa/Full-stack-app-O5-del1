package full.stack.app.oving5del1.model;

public class Calculation {
  private String expression;

  private String answer;

  public Calculation() {

  }

  public Calculation(String expression) {
    this.expression = expression;
  }

  public Calculation(String expression, String answer) {
    this.expression = expression;
    this.answer = answer;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }
}
