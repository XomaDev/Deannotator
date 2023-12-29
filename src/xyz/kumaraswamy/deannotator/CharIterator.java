package xyz.kumaraswamy.deannotator;

public class CharIterator  {

  private final char[] chars;
  private final int lenChars;

  private int index = 0;

  public CharIterator(char[] chars) {
    this.chars = chars;
    lenChars = chars.length;
  }

  public boolean hasNext() {
    return index < lenChars;
  }

  public char next() {
    return chars[index++];
  }

  public char peek() {
    return chars[index];
  }
}
