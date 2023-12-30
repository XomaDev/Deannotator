package xyz.kumaraswamy.deannotator;

public class Reader {

  private final CharIterator iterator;
  private final String text;

  public Reader(String text) {
    iterator = new CharIterator(text.toCharArray());
    this.text = text;
  }

  public Token readBlock(Terminator terminator) {
    StringBuilder name = new StringBuilder();
    while (iterator.hasNext() && terminator.match(iterator.peek())) {
      name.append(iterator.next());
    }
    if (name.length() > 0) {
      return new Token(TokenType.NAME, name.toString());
    }
    char c = iterator.next();
    return new Token(Character.isWhitespace(c)
        ? TokenType.SKIP
        : TokenType.CHAR, String.valueOf(c));
  }

  public String readUntil(Terminator terminator) {
    StringBuilder read = new StringBuilder();
    while (iterator.hasNext() && !terminator.match(iterator.peek())) {
      read.append(iterator.next());
    }
    return read.toString();
  }

  public char peekChar() {
    return iterator.peek();
  }

  public char nextChar() {
    return iterator.next();
  }

  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public String toString() {
    return "<itr>" + text + "</itr>";
  }
}
