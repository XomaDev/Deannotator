package xyz.kumaraswamy.dennotator;

import xyz.kumaraswamy.deannotator.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Test {

  private static final File TEST_DIR = new File(System.getProperty("user.dir"), "/test/");

  public static void main(String[] args) throws IOException {
    File file = new File(TEST_DIR, "TestFile.java");
    String content = Files.readString(file.toPath());

    System.out.println(new Parser(content).parse());
  }
}
