package xyz.kumaraswamy.deannotator;

public class Parser {

  private static final String ANNOTATIONS_PACKAGE = "com.google.appinventor.components.annotations.";

  private static final String[] KNOWN_ANNOTATIONS = {
      "ActionElement", "ActivityElement", "CategoryElement", "DataElement", "GrantUriPermissionElement",
      "IntentFilterElement", "MetaDataElement", "package-info", "PathPermissionElement", "ProviderElement",
      "ReceiverElement", "ServiceElement", "Asset", "DesignerComponent", "IsColor",
      "PermissionConstraint", "PropertyCategory", "SimpleBroadcastReceiver",
      "SimpleObject", "SimplePropertyCopier", "UsesActivities", "UsesActivityMetadata",
      "UsesApplicationMetadata", "UsesAssets", "UsesBroadcastReceivers", "UsesContentProviders", "UsesLibraries",
      "UsesNativeLibraries", "UsesPermissions", "UsesQueries", "UsesServices"
      // excludes @SimpleFunction, @SimpleEvent, @DesignerProperty, @Options
  };

  private final Reader reader;

  public Parser(String source) {
    reader = new Reader(source);
  }

  public String parse() {
    StringBuilder result = new StringBuilder();

    while (reader.hasNext()) {
      Token token = reader.readBlock(NAME_TERMINATOR);

      // this is for skipping comments
      if (token.is(TokenType.CHAR) && token.match("/") && reader.hasNext()) {
        char peek = reader.peekChar();
        if (peek == '/') {
          // single line comment
          reader.readUntil(NEW_LINE_TERMINATOR);
          continue;
        }
        if (peek == '*') {
          readBlockComment();
          continue;
        }
      }

      // code block for handling import statements, skips
      // the import statement if it is of the annotation class
      if (token.is(TokenType.NAME)) {
        String name = token.getValue();
        if (name.equals("import")) {
          // read all characters until newline
          String pkgImport = reader.readUntil(NEW_LINE_TERMINATOR);
          if (!pkgImport.trim().startsWith(ANNOTATIONS_PACKAGE)) {
            // we need to skip this annotation import line
            result.append(name);
            result.append(pkgImport);
          }
          continue;
        }
      }

      // code block responsible for skipping annotations
      if (token.is(TokenType.CHAR) && token.match("@")) {
        String annotation = reader.readUntil(ANNOTATION_SYMBOL_TERMINATOR);
        if (isKnownAnnotation(annotation)) {
          // skip this annotation
          if (reader.hasNext() && reader.peekChar() == '(') {
            // the annotation has some content :)
            // inside it, we'll need to skip read until then
            skipAnnotation();
          }
          continue;
        }
        // leave it as it is!
        result.append("@");
        result.append(annotation);
        continue;
      }
      result.append(token.getValue());
    }
    return beautify(result);
  }

  private static String beautify(StringBuilder result) {
    String output = result.toString();
    while (output.contains("\n\n\n")) {
      output = output.replaceAll("\n\n\n", "\n\n");
    }
    return output;
  }

  private void readBlockComment() {
    while (reader.hasNext()) {
      char c = reader.nextChar();
      if (c == '*' && reader.hasNext() && reader.peekChar() == '/') {
        // end of block comment
        reader.nextChar();
        break;
      }
    }
  }

  private void skipAnnotation() {
    int openBrackets = 0; // all types of brackets

    while (reader.hasNext()) {
      char c = reader.nextChar();
      if (c == '\"') {
        // this reads the text string
        char lastChar = '\"';
        while (reader.hasNext()) {
          char quoteChar = reader.nextChar();
          if (lastChar == '\\' && quoteChar == '\"') {
            reader.nextChar();
          } else if (quoteChar == '\"') {
            // we need to end it here!
            break;
          }
          lastChar = quoteChar;
        }
      } else if (
          c == '(' || c == '{' || c == '['
      ) {
        openBrackets++;
      } else if (
          c == ')' || c == '}' || c == ']'
      ) {
        openBrackets--;
        if (openBrackets == 0) {
          // we are done :)
          break;
        }
      }
    }
  }

  private boolean isKnownAnnotation(String annotation) {
    for (String knownAnnotation : KNOWN_ANNOTATIONS) {
      if (knownAnnotation.equals(annotation)) {
        return true;
      }
    }
    return false;
  }

  private static final Terminator NAME_TERMINATOR = ch -> ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
  private static final Terminator NEW_LINE_TERMINATOR = c -> c == '\n';

  private static final Terminator ANNOTATION_SYMBOL_TERMINATOR = c -> c == '(' || Character.isWhitespace(c);
}
