package org.yaml.snakeyaml.scanner;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.BlockEndToken;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.tokens.BlockMappingStartToken;
import org.yaml.snakeyaml.tokens.BlockSequenceStartToken;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.tokens.DocumentEndToken;
import org.yaml.snakeyaml.tokens.DocumentStartToken;
import org.yaml.snakeyaml.tokens.FlowEntryToken;
import org.yaml.snakeyaml.tokens.FlowMappingEndToken;
import org.yaml.snakeyaml.tokens.FlowMappingStartToken;
import org.yaml.snakeyaml.tokens.FlowSequenceEndToken;
import org.yaml.snakeyaml.tokens.FlowSequenceStartToken;
import org.yaml.snakeyaml.tokens.KeyToken;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.tokens.StreamStartToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.tokens.ValueToken;
import org.yaml.snakeyaml.util.ArrayStack;
import org.yaml.snakeyaml.util.UriEncoder;

public final class ScannerImpl implements Scanner {
  private static final Pattern NOT_HEXA = Pattern.compile("[^0-9A-Fa-f]");
  
  public static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<Character, String>();
  
  public static final Map<Character, Integer> ESCAPE_CODES = new HashMap<Character, Integer>();
  
  private final StreamReader reader;
  
  static {
    ESCAPE_REPLACEMENTS.put(Character.valueOf('0'), "\000");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('a'), "\007");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('b'), "\b");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('t'), "\t");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('n'), "\n");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('v'), "\013");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('f'), "\f");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('r'), "\r");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('e'), "\033");
    ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), " ");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('"'), "\"");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\\'), "\\");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('N'), "");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('_'), " ");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('L'), " ");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('P'), " ");
    ESCAPE_CODES.put(Character.valueOf('x'), Integer.valueOf(2));
    ESCAPE_CODES.put(Character.valueOf('u'), Integer.valueOf(4));
    ESCAPE_CODES.put(Character.valueOf('U'), Integer.valueOf(8));
  }
  
  private boolean done = false;
  
  private int flowLevel = 0;
  
  private List<Token> tokens;
  
  private int tokensTaken = 0;
  
  private int indent = -1;
  
  private ArrayStack<Integer> indents;
  
  private boolean allowSimpleKey = true;
  
  private Map<Integer, SimpleKey> possibleSimpleKeys;
  
  public ScannerImpl(StreamReader paramStreamReader) {
    this.reader = paramStreamReader;
    this.tokens = new ArrayList<Token>(100);
    this.indents = new ArrayStack(10);
    this.possibleSimpleKeys = new LinkedHashMap<Integer, SimpleKey>();
    fetchStreamStart();
  }
  
  public boolean checkToken(Token.ID... paramVarArgs) {
    while (needMoreTokens())
      fetchMoreTokens(); 
    if (!this.tokens.isEmpty()) {
      if (paramVarArgs.length == 0)
        return true; 
      Token.ID iD = ((Token)this.tokens.get(0)).getTokenId();
      for (byte b = 0; b < paramVarArgs.length; b++) {
        if (iD == paramVarArgs[b])
          return true; 
      } 
    } 
    return false;
  }
  
  public Token peekToken() {
    while (needMoreTokens())
      fetchMoreTokens(); 
    return this.tokens.get(0);
  }
  
  public Token getToken() {
    if (!this.tokens.isEmpty()) {
      this.tokensTaken++;
      return this.tokens.remove(0);
    } 
    return null;
  }
  
  private boolean needMoreTokens() {
    if (this.done)
      return false; 
    if (this.tokens.isEmpty())
      return true; 
    stalePossibleSimpleKeys();
    return (nextPossibleSimpleKey() == this.tokensTaken);
  }
  
  private void fetchMoreTokens() {
    scanToNextToken();
    stalePossibleSimpleKeys();
    unwindIndent(this.reader.getColumn());
    int i = this.reader.peek();
    switch (i) {
      case 0:
        fetchStreamEnd();
        return;
      case 37:
        if (checkDirective()) {
          fetchDirective();
          return;
        } 
        break;
      case 45:
        if (checkDocumentStart()) {
          fetchDocumentStart();
          return;
        } 
        if (checkBlockEntry()) {
          fetchBlockEntry();
          return;
        } 
        break;
      case 46:
        if (checkDocumentEnd()) {
          fetchDocumentEnd();
          return;
        } 
        break;
      case 91:
        fetchFlowSequenceStart();
        return;
      case 123:
        fetchFlowMappingStart();
        return;
      case 93:
        fetchFlowSequenceEnd();
        return;
      case 125:
        fetchFlowMappingEnd();
        return;
      case 44:
        fetchFlowEntry();
        return;
      case 63:
        if (checkKey()) {
          fetchKey();
          return;
        } 
        break;
      case 58:
        if (checkValue()) {
          fetchValue();
          return;
        } 
        break;
      case 42:
        fetchAlias();
        return;
      case 38:
        fetchAnchor();
        return;
      case 33:
        fetchTag();
        return;
      case 124:
        if (this.flowLevel == 0) {
          fetchLiteral();
          return;
        } 
        break;
      case 62:
        if (this.flowLevel == 0) {
          fetchFolded();
          return;
        } 
        break;
      case 39:
        fetchSingle();
        return;
      case 34:
        fetchDouble();
        return;
    } 
    if (checkPlain()) {
      fetchPlain();
      return;
    } 
    String str1 = String.valueOf(Character.toChars(i));
    for (Character character : ESCAPE_REPLACEMENTS.keySet()) {
      String str = ESCAPE_REPLACEMENTS.get(character);
      if (str.equals(str1)) {
        str1 = "\\" + character;
        break;
      } 
    } 
    if (i == 9)
      str1 = str1 + "(TAB)"; 
    String str2 = String.format("found character '%s' that cannot start any token. (Do not use %s for indentation)", new Object[] { str1, str1 });
    throw new ScannerException("while scanning for the next token", null, str2, this.reader
        .getMark());
  }
  
  private int nextPossibleSimpleKey() {
    if (!this.possibleSimpleKeys.isEmpty())
      return ((SimpleKey)this.possibleSimpleKeys.values().iterator().next()).getTokenNumber(); 
    return -1;
  }
  
  private void stalePossibleSimpleKeys() {
    if (!this.possibleSimpleKeys.isEmpty()) {
      Iterator<SimpleKey> iterator = this.possibleSimpleKeys.values().iterator();
      while (iterator.hasNext()) {
        SimpleKey simpleKey = iterator.next();
        if (simpleKey.getLine() != this.reader.getLine() || this.reader
          .getIndex() - simpleKey.getIndex() > 1024) {
          if (simpleKey.isRequired())
            throw new ScannerException("while scanning a simple key", simpleKey.getMark(), "could not find expected ':'", this.reader
                .getMark()); 
          iterator.remove();
        } 
      } 
    } 
  }
  
  private void savePossibleSimpleKey() {
    boolean bool = (this.flowLevel == 0 && this.indent == this.reader.getColumn()) ? true : false;
    if (this.allowSimpleKey || !bool) {
      if (this.allowSimpleKey) {
        removePossibleSimpleKey();
        int i = this.tokensTaken + this.tokens.size();
        SimpleKey simpleKey = new SimpleKey(i, bool, this.reader.getIndex(), this.reader.getLine(), this.reader.getColumn(), this.reader.getMark());
        this.possibleSimpleKeys.put(Integer.valueOf(this.flowLevel), simpleKey);
      } 
      return;
    } 
    throw new YAMLException("A simple key is required only if it is the first token in the current line");
  }
  
  private void removePossibleSimpleKey() {
    SimpleKey simpleKey = this.possibleSimpleKeys.remove(Integer.valueOf(this.flowLevel));
    if (simpleKey != null && simpleKey.isRequired())
      throw new ScannerException("while scanning a simple key", simpleKey.getMark(), "could not find expected ':'", this.reader
          .getMark()); 
  }
  
  private void unwindIndent(int paramInt) {
    if (this.flowLevel != 0)
      return; 
    while (this.indent > paramInt) {
      Mark mark = this.reader.getMark();
      this.indent = ((Integer)this.indents.pop()).intValue();
      this.tokens.add(new BlockEndToken(mark, mark));
    } 
  }
  
  private boolean addIndent(int paramInt) {
    if (this.indent < paramInt) {
      this.indents.push(Integer.valueOf(this.indent));
      this.indent = paramInt;
      return true;
    } 
    return false;
  }
  
  private void fetchStreamStart() {
    Mark mark = this.reader.getMark();
    StreamStartToken streamStartToken = new StreamStartToken(mark, mark);
    this.tokens.add(streamStartToken);
  }
  
  private void fetchStreamEnd() {
    unwindIndent(-1);
    removePossibleSimpleKey();
    this.allowSimpleKey = false;
    this.possibleSimpleKeys.clear();
    Mark mark = this.reader.getMark();
    StreamEndToken streamEndToken = new StreamEndToken(mark, mark);
    this.tokens.add(streamEndToken);
    this.done = true;
  }
  
  private void fetchDirective() {
    unwindIndent(-1);
    removePossibleSimpleKey();
    this.allowSimpleKey = false;
    Token token = scanDirective();
    this.tokens.add(token);
  }
  
  private void fetchDocumentStart() {
    fetchDocumentIndicator(true);
  }
  
  private void fetchDocumentEnd() {
    fetchDocumentIndicator(false);
  }
  
  private void fetchDocumentIndicator(boolean paramBoolean) {
    DocumentEndToken documentEndToken;
    unwindIndent(-1);
    removePossibleSimpleKey();
    this.allowSimpleKey = false;
    Mark mark1 = this.reader.getMark();
    this.reader.forward(3);
    Mark mark2 = this.reader.getMark();
    if (paramBoolean) {
      DocumentStartToken documentStartToken = new DocumentStartToken(mark1, mark2);
    } else {
      documentEndToken = new DocumentEndToken(mark1, mark2);
    } 
    this.tokens.add(documentEndToken);
  }
  
  private void fetchFlowSequenceStart() {
    fetchFlowCollectionStart(false);
  }
  
  private void fetchFlowMappingStart() {
    fetchFlowCollectionStart(true);
  }
  
  private void fetchFlowCollectionStart(boolean paramBoolean) {
    FlowSequenceStartToken flowSequenceStartToken;
    savePossibleSimpleKey();
    this.flowLevel++;
    this.allowSimpleKey = true;
    Mark mark1 = this.reader.getMark();
    this.reader.forward(1);
    Mark mark2 = this.reader.getMark();
    if (paramBoolean) {
      FlowMappingStartToken flowMappingStartToken = new FlowMappingStartToken(mark1, mark2);
    } else {
      flowSequenceStartToken = new FlowSequenceStartToken(mark1, mark2);
    } 
    this.tokens.add(flowSequenceStartToken);
  }
  
  private void fetchFlowSequenceEnd() {
    fetchFlowCollectionEnd(false);
  }
  
  private void fetchFlowMappingEnd() {
    fetchFlowCollectionEnd(true);
  }
  
  private void fetchFlowCollectionEnd(boolean paramBoolean) {
    FlowSequenceEndToken flowSequenceEndToken;
    removePossibleSimpleKey();
    this.flowLevel--;
    this.allowSimpleKey = false;
    Mark mark1 = this.reader.getMark();
    this.reader.forward();
    Mark mark2 = this.reader.getMark();
    if (paramBoolean) {
      FlowMappingEndToken flowMappingEndToken = new FlowMappingEndToken(mark1, mark2);
    } else {
      flowSequenceEndToken = new FlowSequenceEndToken(mark1, mark2);
    } 
    this.tokens.add(flowSequenceEndToken);
  }
  
  private void fetchFlowEntry() {
    this.allowSimpleKey = true;
    removePossibleSimpleKey();
    Mark mark1 = this.reader.getMark();
    this.reader.forward();
    Mark mark2 = this.reader.getMark();
    FlowEntryToken flowEntryToken = new FlowEntryToken(mark1, mark2);
    this.tokens.add(flowEntryToken);
  }
  
  private void fetchBlockEntry() {
    if (this.flowLevel == 0) {
      if (!this.allowSimpleKey)
        throw new ScannerException(null, null, "sequence entries are not allowed here", this.reader
            .getMark()); 
      if (addIndent(this.reader.getColumn())) {
        Mark mark = this.reader.getMark();
        this.tokens.add(new BlockSequenceStartToken(mark, mark));
      } 
    } 
    this.allowSimpleKey = true;
    removePossibleSimpleKey();
    Mark mark1 = this.reader.getMark();
    this.reader.forward();
    Mark mark2 = this.reader.getMark();
    BlockEntryToken blockEntryToken = new BlockEntryToken(mark1, mark2);
    this.tokens.add(blockEntryToken);
  }
  
  private void fetchKey() {
    if (this.flowLevel == 0) {
      if (!this.allowSimpleKey)
        throw new ScannerException(null, null, "mapping keys are not allowed here", this.reader
            .getMark()); 
      if (addIndent(this.reader.getColumn())) {
        Mark mark = this.reader.getMark();
        this.tokens.add(new BlockMappingStartToken(mark, mark));
      } 
    } 
    this.allowSimpleKey = (this.flowLevel == 0);
    removePossibleSimpleKey();
    Mark mark1 = this.reader.getMark();
    this.reader.forward();
    Mark mark2 = this.reader.getMark();
    KeyToken keyToken = new KeyToken(mark1, mark2);
    this.tokens.add(keyToken);
  }
  
  private void fetchValue() {
    SimpleKey simpleKey = this.possibleSimpleKeys.remove(Integer.valueOf(this.flowLevel));
    if (simpleKey != null) {
      this.tokens.add(simpleKey.getTokenNumber() - this.tokensTaken, new KeyToken(simpleKey.getMark(), simpleKey
            .getMark()));
      if (this.flowLevel == 0 && 
        addIndent(simpleKey.getColumn()))
        this.tokens.add(simpleKey.getTokenNumber() - this.tokensTaken, new BlockMappingStartToken(simpleKey
              .getMark(), simpleKey.getMark())); 
      this.allowSimpleKey = false;
    } else {
      if (this.flowLevel == 0)
        if (!this.allowSimpleKey)
          throw new ScannerException(null, null, "mapping values are not allowed here", this.reader
              .getMark());  
      if (this.flowLevel == 0 && 
        addIndent(this.reader.getColumn())) {
        Mark mark = this.reader.getMark();
        this.tokens.add(new BlockMappingStartToken(mark, mark));
      } 
      this.allowSimpleKey = (this.flowLevel == 0);
      removePossibleSimpleKey();
    } 
    Mark mark1 = this.reader.getMark();
    this.reader.forward();
    Mark mark2 = this.reader.getMark();
    ValueToken valueToken = new ValueToken(mark1, mark2);
    this.tokens.add(valueToken);
  }
  
  private void fetchAlias() {
    savePossibleSimpleKey();
    this.allowSimpleKey = false;
    Token token = scanAnchor(false);
    this.tokens.add(token);
  }
  
  private void fetchAnchor() {
    savePossibleSimpleKey();
    this.allowSimpleKey = false;
    Token token = scanAnchor(true);
    this.tokens.add(token);
  }
  
  private void fetchTag() {
    savePossibleSimpleKey();
    this.allowSimpleKey = false;
    Token token = scanTag();
    this.tokens.add(token);
  }
  
  private void fetchLiteral() {
    fetchBlockScalar('|');
  }
  
  private void fetchFolded() {
    fetchBlockScalar('>');
  }
  
  private void fetchBlockScalar(char paramChar) {
    this.allowSimpleKey = true;
    removePossibleSimpleKey();
    Token token = scanBlockScalar(paramChar);
    this.tokens.add(token);
  }
  
  private void fetchSingle() {
    fetchFlowScalar('\'');
  }
  
  private void fetchDouble() {
    fetchFlowScalar('"');
  }
  
  private void fetchFlowScalar(char paramChar) {
    savePossibleSimpleKey();
    this.allowSimpleKey = false;
    Token token = scanFlowScalar(paramChar);
    this.tokens.add(token);
  }
  
  private void fetchPlain() {
    savePossibleSimpleKey();
    this.allowSimpleKey = false;
    Token token = scanPlain();
    this.tokens.add(token);
  }
  
  private boolean checkDirective() {
    return (this.reader.getColumn() == 0);
  }
  
  private boolean checkDocumentStart() {
    if (this.reader.getColumn() == 0 && 
      "---".equals(this.reader.prefix(3)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3)))
      return true; 
    return false;
  }
  
  private boolean checkDocumentEnd() {
    if (this.reader.getColumn() == 0 && 
      "...".equals(this.reader.prefix(3)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3)))
      return true; 
    return false;
  }
  
  private boolean checkBlockEntry() {
    return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
  }
  
  private boolean checkKey() {
    if (this.flowLevel != 0)
      return true; 
    return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
  }
  
  private boolean checkValue() {
    if (this.flowLevel != 0)
      return true; 
    return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
  }
  
  private boolean checkPlain() {
    int i = this.reader.peek();
    return (Constant.NULL_BL_T_LINEBR.hasNo(i, "-?:,[]{}#&*!|>'\"%@`") || (Constant.NULL_BL_T_LINEBR
      .hasNo(this.reader.peek(1)) && (i == 45 || (this.flowLevel == 0 && "?:"
      .indexOf(i) != -1))));
  }
  
  private void scanToNextToken() {
    if (this.reader.getIndex() == 0 && this.reader.peek() == 65279)
      this.reader.forward(); 
    boolean bool = false;
    while (!bool) {
      byte b = 0;
      while (this.reader.peek(b) == 32)
        b++; 
      if (b > 0)
        this.reader.forward(b); 
      if (this.reader.peek() == 35) {
        b = 0;
        while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(b)))
          b++; 
        if (b > 0)
          this.reader.forward(b); 
      } 
      if (scanLineBreak().length() != 0) {
        if (this.flowLevel == 0)
          this.allowSimpleKey = true; 
        continue;
      } 
      bool = true;
    } 
  }
  
  private Token scanDirective() {
    Mark mark2, mark1 = this.reader.getMark();
    this.reader.forward();
    String str = scanDirectiveName(mark1);
    List<Integer> list = null;
    if ("YAML".equals(str)) {
      list = scanYamlDirectiveValue(mark1);
      mark2 = this.reader.getMark();
    } else if ("TAG".equals(str)) {
      list = (List)scanTagDirectiveValue(mark1);
      mark2 = this.reader.getMark();
    } else {
      mark2 = this.reader.getMark();
      byte b = 0;
      while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(b)))
        b++; 
      if (b > 0)
        this.reader.forward(b); 
    } 
    scanDirectiveIgnoredLine(mark1);
    return (Token)new DirectiveToken(str, list, mark1, mark2);
  }
  
  private String scanDirectiveName(Mark paramMark) {
    byte b = 0;
    int i = this.reader.peek(b);
    while (Constant.ALPHA.has(i)) {
      b++;
      i = this.reader.peek(b);
    } 
    if (b == 0) {
      String str1 = String.valueOf(Character.toChars(i));
      throw new ScannerException("while scanning a directive", paramMark, "expected alphabetic or numeric character, but found " + str1 + "(" + i + ")", this.reader
          
          .getMark());
    } 
    String str = this.reader.prefixForward(b);
    i = this.reader.peek();
    if (Constant.NULL_BL_LINEBR.hasNo(i)) {
      String str1 = String.valueOf(Character.toChars(i));
      throw new ScannerException("while scanning a directive", paramMark, "expected alphabetic or numeric character, but found " + str1 + "(" + i + ")", this.reader
          
          .getMark());
    } 
    return str;
  }
  
  private List<Integer> scanYamlDirectiveValue(Mark paramMark) {
    while (this.reader.peek() == 32)
      this.reader.forward(); 
    Integer integer1 = scanYamlDirectiveNumber(paramMark);
    int i = this.reader.peek();
    if (i != 46) {
      String str = String.valueOf(Character.toChars(i));
      throw new ScannerException("while scanning a directive", paramMark, "expected a digit or '.', but found " + str + "(" + i + ")", this.reader
          
          .getMark());
    } 
    this.reader.forward();
    Integer integer2 = scanYamlDirectiveNumber(paramMark);
    i = this.reader.peek();
    if (Constant.NULL_BL_LINEBR.hasNo(i)) {
      String str = String.valueOf(Character.toChars(i));
      throw new ScannerException("while scanning a directive", paramMark, "expected a digit or ' ', but found " + str + "(" + i + ")", this.reader
          
          .getMark());
    } 
    ArrayList<Integer> arrayList = new ArrayList(2);
    arrayList.add(integer1);
    arrayList.add(integer2);
    return arrayList;
  }
  
  private Integer scanYamlDirectiveNumber(Mark paramMark) {
    int i = this.reader.peek();
    if (!Character.isDigit(i)) {
      String str = String.valueOf(Character.toChars(i));
      throw new ScannerException("while scanning a directive", paramMark, "expected a digit, but found " + str + "(" + i + ")", this.reader
          .getMark());
    } 
    byte b = 0;
    while (Character.isDigit(this.reader.peek(b)))
      b++; 
    return Integer.valueOf(Integer.parseInt(this.reader.prefixForward(b)));
  }
  
  private List<String> scanTagDirectiveValue(Mark paramMark) {
    while (this.reader.peek() == 32)
      this.reader.forward(); 
    String str1 = scanTagDirectiveHandle(paramMark);
    while (this.reader.peek() == 32)
      this.reader.forward(); 
    String str2 = scanTagDirectivePrefix(paramMark);
    ArrayList<String> arrayList = new ArrayList(2);
    arrayList.add(str1);
    arrayList.add(str2);
    return arrayList;
  }
  
  private String scanTagDirectiveHandle(Mark paramMark) {
    String str = scanTagHandle("directive", paramMark);
    int i = this.reader.peek();
    if (i != 32) {
      String str1 = String.valueOf(Character.toChars(i));
      throw new ScannerException("while scanning a directive", paramMark, "expected ' ', but found " + str1 + "(" + i + ")", this.reader
          .getMark());
    } 
    return str;
  }
  
  private String scanTagDirectivePrefix(Mark paramMark) {
    String str = scanTagUri("directive", paramMark);
    int i = this.reader.peek();
    if (Constant.NULL_BL_LINEBR.hasNo(i)) {
      String str1 = String.valueOf(Character.toChars(i));
      throw new ScannerException("while scanning a directive", paramMark, "expected ' ', but found " + str1 + "(" + i + ")", this.reader
          
          .getMark());
    } 
    return str;
  }
  
  private String scanDirectiveIgnoredLine(Mark paramMark) {
    while (this.reader.peek() == 32)
      this.reader.forward(); 
    if (this.reader.peek() == 35)
      while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek()))
        this.reader.forward();  
    int i = this.reader.peek();
    String str = scanLineBreak();
    if (str.length() == 0 && i != 0) {
      String str1 = String.valueOf(Character.toChars(i));
      throw new ScannerException("while scanning a directive", paramMark, "expected a comment or a line break, but found " + str1 + "(" + i + ")", this.reader
          
          .getMark());
    } 
    return str;
  }
  
  private Token scanAnchor(boolean paramBoolean) {
    AliasToken aliasToken;
    Mark mark1 = this.reader.getMark();
    int i = this.reader.peek();
    String str1 = (i == 42) ? "alias" : "anchor";
    this.reader.forward();
    byte b = 0;
    int j = this.reader.peek(b);
    while (Constant.ALPHA.has(j)) {
      b++;
      j = this.reader.peek(b);
    } 
    if (b == 0) {
      String str = String.valueOf(Character.toChars(j));
      throw new ScannerException("while scanning an " + str1, mark1, "expected alphabetic or numeric character, but found " + str + "(" + j + ")", this.reader
          
          .getMark());
    } 
    String str2 = this.reader.prefixForward(b);
    j = this.reader.peek();
    if (Constant.NULL_BL_T_LINEBR.hasNo(j, "?:,]}%@`")) {
      String str = String.valueOf(Character.toChars(j));
      throw new ScannerException("while scanning an " + str1, mark1, "expected alphabetic or numeric character, but found " + str + "(" + j + ")", this.reader
          
          .getMark());
    } 
    Mark mark2 = this.reader.getMark();
    if (paramBoolean) {
      AnchorToken anchorToken = new AnchorToken(str2, mark1, mark2);
    } else {
      aliasToken = new AliasToken(str2, mark1, mark2);
    } 
    return (Token)aliasToken;
  }
  
  private Token scanTag() {
    Mark mark1 = this.reader.getMark();
    int i = this.reader.peek(1);
    String str1 = null;
    String str2 = null;
    if (i == 60) {
      this.reader.forward(2);
      str2 = scanTagUri("tag", mark1);
      i = this.reader.peek();
      if (i != 62) {
        String str = String.valueOf(Character.toChars(i));
        throw new ScannerException("while scanning a tag", mark1, "expected '>', but found '" + str + "' (" + i + ")", this.reader
            
            .getMark());
      } 
      this.reader.forward();
    } else if (Constant.NULL_BL_T_LINEBR.has(i)) {
      str2 = "!";
      this.reader.forward();
    } else {
      byte b = 1;
      boolean bool = false;
      while (Constant.NULL_BL_LINEBR.hasNo(i)) {
        if (i == 33) {
          bool = true;
          break;
        } 
        b++;
        i = this.reader.peek(b);
      } 
      str1 = "!";
      if (bool) {
        str1 = scanTagHandle("tag", mark1);
      } else {
        str1 = "!";
        this.reader.forward();
      } 
      str2 = scanTagUri("tag", mark1);
    } 
    i = this.reader.peek();
    if (Constant.NULL_BL_LINEBR.hasNo(i)) {
      String str = String.valueOf(Character.toChars(i));
      throw new ScannerException("while scanning a tag", mark1, "expected ' ', but found '" + str + "' (" + i + ")", this.reader
          .getMark());
    } 
    TagTuple tagTuple = new TagTuple(str1, str2);
    Mark mark2 = this.reader.getMark();
    return (Token)new TagToken(tagTuple, mark1, mark2);
  }
  
  private Token scanBlockScalar(char paramChar) {
    boolean bool;
    Mark mark2;
    if (paramChar == '>') {
      bool = true;
    } else {
      bool = false;
    } 
    StringBuilder stringBuilder = new StringBuilder();
    Mark mark1 = this.reader.getMark();
    this.reader.forward();
    Chomping chomping = scanBlockScalarIndicators(mark1);
    int i = chomping.getIncrement();
    scanBlockScalarIgnoredLine(mark1);
    int j = this.indent + 1;
    if (j < 1)
      j = 1; 
    String str1 = null;
    int k = 0;
    int m = 0;
    if (i == -1) {
      Object[] arrayOfObject = scanBlockScalarIndentation();
      str1 = (String)arrayOfObject[0];
      k = ((Integer)arrayOfObject[1]).intValue();
      mark2 = (Mark)arrayOfObject[2];
      m = Math.max(j, k);
    } else {
      m = j + i - 1;
      Object[] arrayOfObject = scanBlockScalarBreaks(m);
      str1 = (String)arrayOfObject[0];
      mark2 = (Mark)arrayOfObject[1];
    } 
    String str2 = "";
    while (this.reader.getColumn() == m && this.reader.peek() != 0) {
      stringBuilder.append(str1);
      boolean bool1 = (" \t".indexOf(this.reader.peek()) == -1) ? true : false;
      byte b = 0;
      while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(b)))
        b++; 
      stringBuilder.append(this.reader.prefixForward(b));
      str2 = scanLineBreak();
      Object[] arrayOfObject = scanBlockScalarBreaks(m);
      str1 = (String)arrayOfObject[0];
      mark2 = (Mark)arrayOfObject[1];
      if (this.reader.getColumn() == m && this.reader.peek() != 0) {
        if (bool && "\n".equals(str2) && bool1 && " \t"
          .indexOf(this.reader.peek()) == -1) {
          if (str1.length() == 0)
            stringBuilder.append(" "); 
          continue;
        } 
        stringBuilder.append(str2);
      } 
    } 
    if (chomping.chompTailIsNotFalse())
      stringBuilder.append(str2); 
    if (chomping.chompTailIsTrue())
      stringBuilder.append(str1); 
    return (Token)new ScalarToken(stringBuilder.toString(), false, mark1, mark2, paramChar);
  }
  
  private Chomping scanBlockScalarIndicators(Mark paramMark) {
    Boolean bool = null;
    int i = -1;
    int j = this.reader.peek();
    if (j == 45 || j == 43) {
      if (j == 43) {
        bool = Boolean.TRUE;
      } else {
        bool = Boolean.FALSE;
      } 
      this.reader.forward();
      j = this.reader.peek();
      if (Character.isDigit(j)) {
        String str = String.valueOf(Character.toChars(j));
        i = Integer.parseInt(str);
        if (i == 0)
          throw new ScannerException("while scanning a block scalar", paramMark, "expected indentation indicator in the range 1-9, but found 0", this.reader
              
              .getMark()); 
        this.reader.forward();
      } 
    } else if (Character.isDigit(j)) {
      String str = String.valueOf(Character.toChars(j));
      i = Integer.parseInt(str);
      if (i == 0)
        throw new ScannerException("while scanning a block scalar", paramMark, "expected indentation indicator in the range 1-9, but found 0", this.reader
            
            .getMark()); 
      this.reader.forward();
      j = this.reader.peek();
      if (j == 45 || j == 43) {
        if (j == 43) {
          bool = Boolean.TRUE;
        } else {
          bool = Boolean.FALSE;
        } 
        this.reader.forward();
      } 
    } 
    j = this.reader.peek();
    if (Constant.NULL_BL_LINEBR.hasNo(j)) {
      String str = String.valueOf(Character.toChars(j));
      throw new ScannerException("while scanning a block scalar", paramMark, "expected chomping or indentation indicators, but found " + str + "(" + j + ")", this.reader
          
          .getMark());
    } 
    return new Chomping(bool, i);
  }
  
  private String scanBlockScalarIgnoredLine(Mark paramMark) {
    while (this.reader.peek() == 32)
      this.reader.forward(); 
    if (this.reader.peek() == 35)
      while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek()))
        this.reader.forward();  
    int i = this.reader.peek();
    String str = scanLineBreak();
    if (str.length() == 0 && i != 0) {
      String str1 = String.valueOf(Character.toChars(i));
      throw new ScannerException("while scanning a block scalar", paramMark, "expected a comment or a line break, but found " + str1 + "(" + i + ")", this.reader
          
          .getMark());
    } 
    return str;
  }
  
  private Object[] scanBlockScalarIndentation() {
    StringBuilder stringBuilder = new StringBuilder();
    int i = 0;
    Mark mark = this.reader.getMark();
    while (Constant.LINEBR.has(this.reader.peek(), " \r")) {
      if (this.reader.peek() != 32) {
        stringBuilder.append(scanLineBreak());
        mark = this.reader.getMark();
        continue;
      } 
      this.reader.forward();
      if (this.reader.getColumn() > i)
        i = this.reader.getColumn(); 
    } 
    return new Object[] { stringBuilder.toString(), Integer.valueOf(i), mark };
  }
  
  private Object[] scanBlockScalarBreaks(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    Mark mark = this.reader.getMark();
    int i = this.reader.getColumn();
    while (i < paramInt && this.reader.peek() == 32) {
      this.reader.forward();
      i++;
    } 
    String str = null;
    while ((str = scanLineBreak()).length() != 0) {
      stringBuilder.append(str);
      mark = this.reader.getMark();
      i = this.reader.getColumn();
      while (i < paramInt && this.reader.peek() == 32) {
        this.reader.forward();
        i++;
      } 
    } 
    return new Object[] { stringBuilder.toString(), mark };
  }
  
  private Token scanFlowScalar(char paramChar) {
    boolean bool;
    if (paramChar == '"') {
      bool = true;
    } else {
      bool = false;
    } 
    StringBuilder stringBuilder = new StringBuilder();
    Mark mark1 = this.reader.getMark();
    int i = this.reader.peek();
    this.reader.forward();
    stringBuilder.append(scanFlowScalarNonSpaces(bool, mark1));
    while (this.reader.peek() != i) {
      stringBuilder.append(scanFlowScalarSpaces(mark1));
      stringBuilder.append(scanFlowScalarNonSpaces(bool, mark1));
    } 
    this.reader.forward();
    Mark mark2 = this.reader.getMark();
    return (Token)new ScalarToken(stringBuilder.toString(), false, mark1, mark2, paramChar);
  }
  
  private String scanFlowScalarNonSpaces(boolean paramBoolean, Mark paramMark) {
    StringBuilder stringBuilder = new StringBuilder();
    while (true) {
      int i = 0;
      while (Constant.NULL_BL_T_LINEBR.hasNo(this.reader.peek(i), "'\"\\"))
        i++; 
      if (i != 0)
        stringBuilder.append(this.reader.prefixForward(i)); 
      int j = this.reader.peek();
      if (!paramBoolean && j == 39 && this.reader.peek(1) == 39) {
        stringBuilder.append("'");
        this.reader.forward(2);
        continue;
      } 
      if ((paramBoolean && j == 39) || (!paramBoolean && "\"\\".indexOf(j) != -1)) {
        stringBuilder.appendCodePoint(j);
        this.reader.forward();
        continue;
      } 
      if (paramBoolean && j == 92) {
        this.reader.forward();
        j = this.reader.peek();
        if (!Character.isSupplementaryCodePoint(j) && ESCAPE_REPLACEMENTS.containsKey(Character.valueOf((char)j))) {
          stringBuilder.append(ESCAPE_REPLACEMENTS.get(Character.valueOf((char)j)));
          this.reader.forward();
          continue;
        } 
        if (!Character.isSupplementaryCodePoint(j) && ESCAPE_CODES.containsKey(Character.valueOf((char)j))) {
          i = ((Integer)ESCAPE_CODES.get(Character.valueOf((char)j))).intValue();
          this.reader.forward();
          String str1 = this.reader.prefix(i);
          if (NOT_HEXA.matcher(str1).find())
            throw new ScannerException("while scanning a double-quoted scalar", paramMark, "expected escape sequence of " + i + " hexadecimal numbers, but found: " + str1, this.reader
                
                .getMark()); 
          int k = Integer.parseInt(str1, 16);
          String str2 = new String(Character.toChars(k));
          stringBuilder.append(str2);
          this.reader.forward(i);
          continue;
        } 
        if (scanLineBreak().length() != 0) {
          stringBuilder.append(scanFlowScalarBreaks(paramMark));
          continue;
        } 
        String str = String.valueOf(Character.toChars(j));
        throw new ScannerException("while scanning a double-quoted scalar", paramMark, "found unknown escape character " + str + "(" + j + ")", this.reader
            
            .getMark());
      } 
      break;
    } 
    return stringBuilder.toString();
  }
  
  private String scanFlowScalarSpaces(Mark paramMark) {
    StringBuilder stringBuilder = new StringBuilder();
    byte b = 0;
    while (" \t".indexOf(this.reader.peek(b)) != -1)
      b++; 
    String str1 = this.reader.prefixForward(b);
    int i = this.reader.peek();
    if (i == 0)
      throw new ScannerException("while scanning a quoted scalar", paramMark, "found unexpected end of stream", this.reader
          .getMark()); 
    String str2 = scanLineBreak();
    if (str2.length() != 0) {
      String str = scanFlowScalarBreaks(paramMark);
      if (!"\n".equals(str2)) {
        stringBuilder.append(str2);
      } else if (str.length() == 0) {
        stringBuilder.append(" ");
      } 
      stringBuilder.append(str);
    } else {
      stringBuilder.append(str1);
    } 
    return stringBuilder.toString();
  }
  
  private String scanFlowScalarBreaks(Mark paramMark) {
    StringBuilder stringBuilder = new StringBuilder();
    while (true) {
      String str1 = this.reader.prefix(3);
      if (("---".equals(str1) || "...".equals(str1)) && Constant.NULL_BL_T_LINEBR
        .has(this.reader.peek(3)))
        throw new ScannerException("while scanning a quoted scalar", paramMark, "found unexpected document separator", this.reader
            .getMark()); 
      while (" \t".indexOf(this.reader.peek()) != -1)
        this.reader.forward(); 
      String str2 = scanLineBreak();
      if (str2.length() != 0) {
        stringBuilder.append(str2);
        continue;
      } 
      break;
    } 
    return stringBuilder.toString();
  }
  
  private Token scanPlain() {
    StringBuilder stringBuilder = new StringBuilder();
    Mark mark1 = this.reader.getMark();
    Mark mark2 = mark1;
    int i = this.indent + 1;
    String str = "";
    do {
      int j;
      byte b = 0;
      if (this.reader.peek() == 35)
        break; 
      while (true) {
        j = this.reader.peek(b);
        if (Constant.NULL_BL_T_LINEBR.has(j) || (this.flowLevel == 0 && j == 58 && Constant.NULL_BL_T_LINEBR
          
          .has(this.reader.peek(b + 1))) || (this.flowLevel != 0 && ",:?[]{}"
          .indexOf(j) != -1))
          break; 
        b++;
      } 
      if (this.flowLevel != 0 && j == 58 && Constant.NULL_BL_T_LINEBR
        .hasNo(this.reader.peek(b + 1), ",[]{}")) {
        this.reader.forward(b);
        throw new ScannerException("while scanning a plain scalar", mark1, "found unexpected ':'", this.reader
            .getMark(), "Please check http://pyyaml.org/wiki/YAMLColonInFlowContext for details.");
      } 
      if (b == 0)
        break; 
      this.allowSimpleKey = false;
      stringBuilder.append(str);
      stringBuilder.append(this.reader.prefixForward(b));
      mark2 = this.reader.getMark();
      str = scanPlainSpaces();
    } while (str.length() != 0 && this.reader.peek() != 35 && (this.flowLevel != 0 || this.reader
      .getColumn() >= i));
    return (Token)new ScalarToken(stringBuilder.toString(), mark1, mark2, true);
  }
  
  private String scanPlainSpaces() {
    byte b = 0;
    while (this.reader.peek(b) == 32 || this.reader.peek(b) == 9)
      b++; 
    String str1 = this.reader.prefixForward(b);
    String str2 = scanLineBreak();
    if (str2.length() != 0) {
      this.allowSimpleKey = true;
      String str = this.reader.prefix(3);
      if ("---".equals(str) || ("...".equals(str) && Constant.NULL_BL_T_LINEBR
        .has(this.reader.peek(3))))
        return ""; 
      StringBuilder stringBuilder = new StringBuilder();
      while (true) {
        while (this.reader.peek() == 32)
          this.reader.forward(); 
        String str3 = scanLineBreak();
        if (str3.length() != 0) {
          stringBuilder.append(str3);
          str = this.reader.prefix(3);
          if ("---".equals(str) || ("...".equals(str) && Constant.NULL_BL_T_LINEBR
            .has(this.reader.peek(3))))
            return ""; 
          continue;
        } 
        break;
      } 
      if (!"\n".equals(str2))
        return str2 + stringBuilder; 
      if (stringBuilder.length() == 0)
        return " "; 
      return stringBuilder.toString();
    } 
    return str1;
  }
  
  private String scanTagHandle(String paramString, Mark paramMark) {
    int i = this.reader.peek();
    if (i != 33) {
      String str = String.valueOf(Character.toChars(i));
      throw new ScannerException("while scanning a " + paramString, paramMark, "expected '!', but found " + str + "(" + i + ")", this.reader
          .getMark());
    } 
    byte b = 1;
    i = this.reader.peek(b);
    if (i != 32) {
      while (Constant.ALPHA.has(i)) {
        b++;
        i = this.reader.peek(b);
      } 
      if (i != 33) {
        this.reader.forward(b);
        String str = String.valueOf(Character.toChars(i));
        throw new ScannerException("while scanning a " + paramString, paramMark, "expected '!', but found " + str + "(" + i + ")", this.reader
            .getMark());
      } 
      b++;
    } 
    return this.reader.prefixForward(b);
  }
  
  private String scanTagUri(String paramString, Mark paramMark) {
    StringBuilder stringBuilder = new StringBuilder();
    byte b = 0;
    int i = this.reader.peek(b);
    while (Constant.URI_CHARS.has(i)) {
      if (i == 37) {
        stringBuilder.append(this.reader.prefixForward(b));
        b = 0;
        stringBuilder.append(scanUriEscapes(paramString, paramMark));
      } else {
        b++;
      } 
      i = this.reader.peek(b);
    } 
    if (b != 0) {
      stringBuilder.append(this.reader.prefixForward(b));
      b = 0;
    } 
    if (stringBuilder.length() == 0) {
      String str = String.valueOf(Character.toChars(i));
      throw new ScannerException("while scanning a " + paramString, paramMark, "expected URI, but found " + str + "(" + i + ")", this.reader
          .getMark());
    } 
    return stringBuilder.toString();
  }
  
  private String scanUriEscapes(String paramString, Mark paramMark) {
    byte b = 1;
    while (this.reader.peek(b * 3) == 37)
      b++; 
    Mark mark = this.reader.getMark();
    ByteBuffer byteBuffer = ByteBuffer.allocate(b);
    while (this.reader.peek() == 37) {
      this.reader.forward();
      try {
        byte b1 = (byte)Integer.parseInt(this.reader.prefix(2), 16);
        byteBuffer.put(b1);
      } catch (NumberFormatException numberFormatException) {
        int i = this.reader.peek();
        String str1 = String.valueOf(Character.toChars(i));
        int j = this.reader.peek(1);
        String str2 = String.valueOf(Character.toChars(j));
        throw new ScannerException("while scanning a " + paramString, paramMark, "expected URI escape sequence of 2 hexadecimal numbers, but found " + str1 + "(" + i + ") and " + str2 + "(" + j + ")", this.reader
            
            .getMark());
      } 
      this.reader.forward(2);
    } 
    byteBuffer.flip();
    try {
      return UriEncoder.decode(byteBuffer);
    } catch (CharacterCodingException characterCodingException) {
      throw new ScannerException("while scanning a " + paramString, paramMark, "expected URI in UTF-8: " + characterCodingException
          .getMessage(), mark);
    } 
  }
  
  private String scanLineBreak() {
    int i = this.reader.peek();
    if (i == 13 || i == 10 || i == 133) {
      if (i == 13 && 10 == this.reader.peek(1)) {
        this.reader.forward(2);
      } else {
        this.reader.forward();
      } 
      return "\n";
    } 
    if (i == 8232 || i == 8233) {
      this.reader.forward();
      return String.valueOf(Character.toChars(i));
    } 
    return "";
  }
  
  private static class Chomping {
    private final Boolean value;
    
    private final int increment;
    
    public Chomping(Boolean param1Boolean, int param1Int) {
      this.value = param1Boolean;
      this.increment = param1Int;
    }
    
    public boolean chompTailIsNotFalse() {
      return (this.value == null || this.value.booleanValue());
    }
    
    public boolean chompTailIsTrue() {
      return (this.value != null && this.value.booleanValue());
    }
    
    public int getIncrement() {
      return this.increment;
    }
  }
}
