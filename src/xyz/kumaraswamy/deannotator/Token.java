package xyz.kumaraswamy.deannotator;

public class Token {

  private final TokenType type;
  private final String value;

  public Token(TokenType type, String value) {
    this.type = type;
    this.value = value;
  }

  public boolean is(TokenType type) {
    return this.type == type;
  }

  public String getValue() {
    return value;
  }

  public boolean match(String name) {
    return value.matches(name);
  }
}
