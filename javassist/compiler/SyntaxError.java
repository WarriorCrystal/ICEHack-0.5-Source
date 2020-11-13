package javassist.compiler;

public class SyntaxError extends CompileError {
  public SyntaxError(Lex paramLex) {
    super("syntax error near \"" + paramLex.getTextAround() + "\"", paramLex);
  }
}
