package full.stack.app.oving5del1.model;

public class User {
  private String username;
  private String userPassword;

  public User() {}

  public User(String username, String userPassword) {
    this.username = username;
    this.userPassword = userPassword;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  @Override
  public String toString() {
    return "User: [username: " + username + ", password: " + userPassword + "]";
  }
}
