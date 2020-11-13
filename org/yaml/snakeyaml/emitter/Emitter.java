package org.yaml.snakeyaml.emitter;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.Constant;
import org.yaml.snakeyaml.util.ArrayStack;

public final class Emitter implements Emitable {
  private static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<Character, String>();
  
  public static final int MIN_INDENT = 1;
  
  public static final int MAX_INDENT = 10;
  
  private static final char[] SPACE = new char[] { ' ' };
  
  static {
    ESCAPE_REPLACEMENTS.put(Character.valueOf(false), "0");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\007'), "a");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\b'), "b");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\t'), "t");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\n'), "n");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\013'), "v");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\f'), "f");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\r'), "r");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\033'), "e");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('"'), "\"");
    ESCAPE_REPLACEMENTS.put(Character.valueOf('\\'), "\\");
    ESCAPE_REPLACEMENTS.put(Character.valueOf(''), "N");
    ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), "_");
    ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), "L");
    ESCAPE_REPLACEMENTS.put(Character.valueOf(' '), "P");
  }
  
  private static final Map<String, String> DEFAULT_TAG_PREFIXES = new LinkedHashMap<String, String>();
  
  private final Writer stream;
  
  private final ArrayStack<EmitterState> states;
  
  private EmitterState state;
  
  private final Queue<Event> events;
  
  private Event event;
  
  private final ArrayStack<Integer> indents;
  
  private Integer indent;
  
  private int flowLevel;
  
  private boolean rootContext;
  
  private boolean mappingContext;
  
  private boolean simpleKeyContext;
  
  private int column;
  
  private boolean whitespace;
  
  private boolean indention;
  
  private boolean openEnded;
  
  private Boolean canonical;
  
  private Boolean prettyFlow;
  
  private boolean allowUnicode;
  
  private int bestIndent;
  
  private int indicatorIndent;
  
  private int bestWidth;
  
  private char[] bestLineBreak;
  
  private boolean splitLines;
  
  private Map<String, String> tagPrefixes;
  
  private String preparedAnchor;
  
  private String preparedTag;
  
  private ScalarAnalysis analysis;
  
  private Character style;
  
  static {
    DEFAULT_TAG_PREFIXES.put("!", "!");
    DEFAULT_TAG_PREFIXES.put("tag:yaml.org,2002:", "!!");
  }
  
  public Emitter(Writer paramWriter, DumperOptions paramDumperOptions) {
    this.stream = paramWriter;
    this.states = new ArrayStack(100);
    this.state = new ExpectStreamStart();
    this.events = new ArrayBlockingQueue<Event>(100);
    this.event = null;
    this.indents = new ArrayStack(10);
    this.indent = null;
    this.flowLevel = 0;
    this.mappingContext = false;
    this.simpleKeyContext = false;
    this.column = 0;
    this.whitespace = true;
    this.indention = true;
    this.openEnded = false;
    this.canonical = Boolean.valueOf(paramDumperOptions.isCanonical());
    this.prettyFlow = Boolean.valueOf(paramDumperOptions.isPrettyFlow());
    this.allowUnicode = paramDumperOptions.isAllowUnicode();
    this.bestIndent = 2;
    if (paramDumperOptions.getIndent() > 1 && paramDumperOptions.getIndent() < 10)
      this.bestIndent = paramDumperOptions.getIndent(); 
    this.indicatorIndent = paramDumperOptions.getIndicatorIndent();
    this.bestWidth = 80;
    if (paramDumperOptions.getWidth() > this.bestIndent * 2)
      this.bestWidth = paramDumperOptions.getWidth(); 
    this.bestLineBreak = paramDumperOptions.getLineBreak().getString().toCharArray();
    this.splitLines = paramDumperOptions.getSplitLines();
    this.tagPrefixes = new LinkedHashMap<String, String>();
    this.preparedAnchor = null;
    this.preparedTag = null;
    this.analysis = null;
    this.style = null;
  }
  
  public void emit(Event paramEvent) throws IOException {
    this.events.add(paramEvent);
    while (!needMoreEvents()) {
      this.event = this.events.poll();
      this.state.expect();
      this.event = null;
    } 
  }
  
  private boolean needMoreEvents() {
    if (this.events.isEmpty())
      return true; 
    Event event = this.events.peek();
    if (event instanceof DocumentStartEvent)
      return needEvents(1); 
    if (event instanceof SequenceStartEvent)
      return needEvents(2); 
    if (event instanceof MappingStartEvent)
      return needEvents(3); 
    return false;
  }
  
  private boolean needEvents(int paramInt) {
    byte b = 0;
    Iterator<Event> iterator = this.events.iterator();
    iterator.next();
    while (iterator.hasNext()) {
      Event event = iterator.next();
      if (event instanceof DocumentStartEvent || event instanceof CollectionStartEvent) {
        b++;
      } else if (event instanceof DocumentEndEvent || event instanceof org.yaml.snakeyaml.events.CollectionEndEvent) {
        b--;
      } else if (event instanceof org.yaml.snakeyaml.events.StreamEndEvent) {
        b = -1;
      } 
      if (b < 0)
        return false; 
    } 
    return (this.events.size() < paramInt + 1);
  }
  
  private void increaseIndent(boolean paramBoolean1, boolean paramBoolean2) {
    this.indents.push(this.indent);
    if (this.indent == null) {
      if (paramBoolean1) {
        this.indent = Integer.valueOf(this.bestIndent);
      } else {
        this.indent = Integer.valueOf(0);
      } 
    } else if (!paramBoolean2) {
      Emitter emitter = this;
      emitter.indent = Integer.valueOf(emitter.indent.intValue() + this.bestIndent);
    } 
  }
  
  private class ExpectStreamStart implements EmitterState {
    private ExpectStreamStart() {}
    
    public void expect() throws IOException {
      if (Emitter.this.event instanceof org.yaml.snakeyaml.events.StreamStartEvent) {
        Emitter.this.writeStreamStart();
        Emitter.this.state = new Emitter.ExpectFirstDocumentStart();
      } else {
        throw new EmitterException("expected StreamStartEvent, but got " + Emitter.this.event);
      } 
    }
  }
  
  private class ExpectNothing implements EmitterState {
    private ExpectNothing() {}
    
    public void expect() throws IOException {
      throw new EmitterException("expecting nothing, but got " + Emitter.this.event);
    }
  }
  
  private class ExpectFirstDocumentStart implements EmitterState {
    private ExpectFirstDocumentStart() {}
    
    public void expect() throws IOException {
      (new Emitter.ExpectDocumentStart(true)).expect();
    }
  }
  
  private class ExpectDocumentStart implements EmitterState {
    private boolean first;
    
    public ExpectDocumentStart(boolean param1Boolean) {
      this.first = param1Boolean;
    }
    
    public void expect() throws IOException {
      if (Emitter.this.event instanceof DocumentStartEvent) {
        DocumentStartEvent documentStartEvent = (DocumentStartEvent)Emitter.this.event;
        if ((documentStartEvent.getVersion() != null || documentStartEvent.getTags() != null) && Emitter.this.openEnded) {
          Emitter.this.writeIndicator("...", true, false, false);
          Emitter.this.writeIndent();
        } 
        if (documentStartEvent.getVersion() != null) {
          String str = Emitter.this.prepareVersion(documentStartEvent.getVersion());
          Emitter.this.writeVersionDirective(str);
        } 
        Emitter.this.tagPrefixes = (Map)new LinkedHashMap<Object, Object>(Emitter.DEFAULT_TAG_PREFIXES);
        if (documentStartEvent.getTags() != null) {
          TreeSet treeSet = new TreeSet(documentStartEvent.getTags().keySet());
          for (String str1 : treeSet) {
            String str2 = (String)documentStartEvent.getTags().get(str1);
            Emitter.this.tagPrefixes.put(str2, str1);
            String str3 = Emitter.this.prepareTagHandle(str1);
            String str4 = Emitter.this.prepareTagPrefix(str2);
            Emitter.this.writeTagDirective(str3, str4);
          } 
        } 
        boolean bool = (this.first && !documentStartEvent.getExplicit() && !Emitter.this.canonical.booleanValue() && documentStartEvent.getVersion() == null && (documentStartEvent.getTags() == null || documentStartEvent.getTags().isEmpty()) && !Emitter.this.checkEmptyDocument()) ? true : false;
        if (!bool) {
          Emitter.this.writeIndent();
          Emitter.this.writeIndicator("---", true, false, false);
          if (Emitter.this.canonical.booleanValue())
            Emitter.this.writeIndent(); 
        } 
        Emitter.this.state = new Emitter.ExpectDocumentRoot();
      } else if (Emitter.this.event instanceof org.yaml.snakeyaml.events.StreamEndEvent) {
        Emitter.this.writeStreamEnd();
        Emitter.this.state = new Emitter.ExpectNothing();
      } else {
        throw new EmitterException("expected DocumentStartEvent, but got " + Emitter.this.event);
      } 
    }
  }
  
  private class ExpectDocumentEnd implements EmitterState {
    private ExpectDocumentEnd() {}
    
    public void expect() throws IOException {
      if (Emitter.this.event instanceof DocumentEndEvent) {
        Emitter.this.writeIndent();
        if (((DocumentEndEvent)Emitter.this.event).getExplicit()) {
          Emitter.this.writeIndicator("...", true, false, false);
          Emitter.this.writeIndent();
        } 
        Emitter.this.flushStream();
        Emitter.this.state = new Emitter.ExpectDocumentStart(false);
      } else {
        throw new EmitterException("expected DocumentEndEvent, but got " + Emitter.this.event);
      } 
    }
  }
  
  private class ExpectDocumentRoot implements EmitterState {
    private ExpectDocumentRoot() {}
    
    public void expect() throws IOException {
      Emitter.this.states.push(new Emitter.ExpectDocumentEnd());
      Emitter.this.expectNode(true, false, false);
    }
  }
  
  private void expectNode(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) throws IOException {
    this.rootContext = paramBoolean1;
    this.mappingContext = paramBoolean2;
    this.simpleKeyContext = paramBoolean3;
    if (this.event instanceof org.yaml.snakeyaml.events.AliasEvent) {
      expectAlias();
    } else if (this.event instanceof ScalarEvent || this.event instanceof CollectionStartEvent) {
      processAnchor("&");
      processTag();
      if (this.event instanceof ScalarEvent) {
        expectScalar();
      } else if (this.event instanceof SequenceStartEvent) {
        if (this.flowLevel != 0 || this.canonical.booleanValue() || ((SequenceStartEvent)this.event).getFlowStyle().booleanValue() || 
          checkEmptySequence()) {
          expectFlowSequence();
        } else {
          expectBlockSequence();
        } 
      } else if (this.flowLevel != 0 || this.canonical.booleanValue() || ((MappingStartEvent)this.event).getFlowStyle().booleanValue() || 
        checkEmptyMapping()) {
        expectFlowMapping();
      } else {
        expectBlockMapping();
      } 
    } else {
      throw new EmitterException("expected NodeEvent, but got " + this.event);
    } 
  }
  
  private void expectAlias() throws IOException {
    if (((NodeEvent)this.event).getAnchor() == null)
      throw new EmitterException("anchor is not specified for alias"); 
    processAnchor("*");
    this.state = (EmitterState)this.states.pop();
  }
  
  private void expectScalar() throws IOException {
    increaseIndent(true, false);
    processScalar();
    this.indent = (Integer)this.indents.pop();
    this.state = (EmitterState)this.states.pop();
  }
  
  private void expectFlowSequence() throws IOException {
    writeIndicator("[", true, true, false);
    this.flowLevel++;
    increaseIndent(true, false);
    if (this.prettyFlow.booleanValue())
      writeIndent(); 
    this.state = new ExpectFirstFlowSequenceItem();
  }
  
  private class ExpectFirstFlowSequenceItem implements EmitterState {
    private ExpectFirstFlowSequenceItem() {}
    
    public void expect() throws IOException {
      if (Emitter.this.event instanceof org.yaml.snakeyaml.events.SequenceEndEvent) {
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
        Emitter.this.flowLevel--;
        Emitter.this.writeIndicator("]", false, false, false);
        Emitter.this.state = (EmitterState)Emitter.this.states.pop();
      } else {
        if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue())
          Emitter.this.writeIndent(); 
        Emitter.this.states.push(new Emitter.ExpectFlowSequenceItem());
        Emitter.this.expectNode(false, false, false);
      } 
    }
  }
  
  private class ExpectFlowSequenceItem implements EmitterState {
    private ExpectFlowSequenceItem() {}
    
    public void expect() throws IOException {
      if (Emitter.this.event instanceof org.yaml.snakeyaml.events.SequenceEndEvent) {
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
        Emitter.this.flowLevel--;
        if (Emitter.this.canonical.booleanValue()) {
          Emitter.this.writeIndicator(",", false, false, false);
          Emitter.this.writeIndent();
        } 
        Emitter.this.writeIndicator("]", false, false, false);
        if (Emitter.this.prettyFlow.booleanValue())
          Emitter.this.writeIndent(); 
        Emitter.this.state = (EmitterState)Emitter.this.states.pop();
      } else {
        Emitter.this.writeIndicator(",", false, false, false);
        if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue())
          Emitter.this.writeIndent(); 
        Emitter.this.states.push(new ExpectFlowSequenceItem());
        Emitter.this.expectNode(false, false, false);
      } 
    }
  }
  
  private void expectFlowMapping() throws IOException {
    writeIndicator("{", true, true, false);
    this.flowLevel++;
    increaseIndent(true, false);
    if (this.prettyFlow.booleanValue())
      writeIndent(); 
    this.state = new ExpectFirstFlowMappingKey();
  }
  
  private class ExpectFirstFlowMappingKey implements EmitterState {
    private ExpectFirstFlowMappingKey() {}
    
    public void expect() throws IOException {
      if (Emitter.this.event instanceof org.yaml.snakeyaml.events.MappingEndEvent) {
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
        Emitter.this.flowLevel--;
        Emitter.this.writeIndicator("}", false, false, false);
        Emitter.this.state = (EmitterState)Emitter.this.states.pop();
      } else {
        if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue())
          Emitter.this.writeIndent(); 
        if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
          Emitter.this.states.push(new Emitter.ExpectFlowMappingSimpleValue());
          Emitter.this.expectNode(false, true, true);
        } else {
          Emitter.this.writeIndicator("?", true, false, false);
          Emitter.this.states.push(new Emitter.ExpectFlowMappingValue());
          Emitter.this.expectNode(false, true, false);
        } 
      } 
    }
  }
  
  private class ExpectFlowMappingKey implements EmitterState {
    private ExpectFlowMappingKey() {}
    
    public void expect() throws IOException {
      if (Emitter.this.event instanceof org.yaml.snakeyaml.events.MappingEndEvent) {
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
        Emitter.this.flowLevel--;
        if (Emitter.this.canonical.booleanValue()) {
          Emitter.this.writeIndicator(",", false, false, false);
          Emitter.this.writeIndent();
        } 
        if (Emitter.this.prettyFlow.booleanValue())
          Emitter.this.writeIndent(); 
        Emitter.this.writeIndicator("}", false, false, false);
        Emitter.this.state = (EmitterState)Emitter.this.states.pop();
      } else {
        Emitter.this.writeIndicator(",", false, false, false);
        if (Emitter.this.canonical.booleanValue() || (Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines) || Emitter.this.prettyFlow.booleanValue())
          Emitter.this.writeIndent(); 
        if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
          Emitter.this.states.push(new Emitter.ExpectFlowMappingSimpleValue());
          Emitter.this.expectNode(false, true, true);
        } else {
          Emitter.this.writeIndicator("?", true, false, false);
          Emitter.this.states.push(new Emitter.ExpectFlowMappingValue());
          Emitter.this.expectNode(false, true, false);
        } 
      } 
    }
  }
  
  private class ExpectFlowMappingSimpleValue implements EmitterState {
    private ExpectFlowMappingSimpleValue() {}
    
    public void expect() throws IOException {
      Emitter.this.writeIndicator(":", false, false, false);
      Emitter.this.states.push(new Emitter.ExpectFlowMappingKey());
      Emitter.this.expectNode(false, true, false);
    }
  }
  
  private class ExpectFlowMappingValue implements EmitterState {
    private ExpectFlowMappingValue() {}
    
    public void expect() throws IOException {
      if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.prettyFlow.booleanValue())
        Emitter.this.writeIndent(); 
      Emitter.this.writeIndicator(":", true, false, false);
      Emitter.this.states.push(new Emitter.ExpectFlowMappingKey());
      Emitter.this.expectNode(false, true, false);
    }
  }
  
  private void expectBlockSequence() throws IOException {
    boolean bool = (this.mappingContext && !this.indention) ? true : false;
    increaseIndent(false, bool);
    this.state = new ExpectFirstBlockSequenceItem();
  }
  
  private class ExpectFirstBlockSequenceItem implements EmitterState {
    private ExpectFirstBlockSequenceItem() {}
    
    public void expect() throws IOException {
      (new Emitter.ExpectBlockSequenceItem(true)).expect();
    }
  }
  
  private class ExpectBlockSequenceItem implements EmitterState {
    private boolean first;
    
    public ExpectBlockSequenceItem(boolean param1Boolean) {
      this.first = param1Boolean;
    }
    
    public void expect() throws IOException {
      if (!this.first && Emitter.this.event instanceof org.yaml.snakeyaml.events.SequenceEndEvent) {
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
        Emitter.this.state = (EmitterState)Emitter.this.states.pop();
      } else {
        Emitter.this.writeIndent();
        Emitter.this.writeWhitespace(Emitter.this.indicatorIndent);
        Emitter.this.writeIndicator("-", true, false, true);
        Emitter.this.states.push(new ExpectBlockSequenceItem(false));
        Emitter.this.expectNode(false, false, false);
      } 
    }
  }
  
  private void expectBlockMapping() throws IOException {
    increaseIndent(false, false);
    this.state = new ExpectFirstBlockMappingKey();
  }
  
  private class ExpectFirstBlockMappingKey implements EmitterState {
    private ExpectFirstBlockMappingKey() {}
    
    public void expect() throws IOException {
      (new Emitter.ExpectBlockMappingKey(true)).expect();
    }
  }
  
  private class ExpectBlockMappingKey implements EmitterState {
    private boolean first;
    
    public ExpectBlockMappingKey(boolean param1Boolean) {
      this.first = param1Boolean;
    }
    
    public void expect() throws IOException {
      if (!this.first && Emitter.this.event instanceof org.yaml.snakeyaml.events.MappingEndEvent) {
        Emitter.this.indent = (Integer)Emitter.this.indents.pop();
        Emitter.this.state = (EmitterState)Emitter.this.states.pop();
      } else {
        Emitter.this.writeIndent();
        if (Emitter.this.checkSimpleKey()) {
          Emitter.this.states.push(new Emitter.ExpectBlockMappingSimpleValue());
          Emitter.this.expectNode(false, true, true);
        } else {
          Emitter.this.writeIndicator("?", true, false, true);
          Emitter.this.states.push(new Emitter.ExpectBlockMappingValue());
          Emitter.this.expectNode(false, true, false);
        } 
      } 
    }
  }
  
  private class ExpectBlockMappingSimpleValue implements EmitterState {
    private ExpectBlockMappingSimpleValue() {}
    
    public void expect() throws IOException {
      Emitter.this.writeIndicator(":", false, false, false);
      Emitter.this.states.push(new Emitter.ExpectBlockMappingKey(false));
      Emitter.this.expectNode(false, true, false);
    }
  }
  
  private class ExpectBlockMappingValue implements EmitterState {
    private ExpectBlockMappingValue() {}
    
    public void expect() throws IOException {
      Emitter.this.writeIndent();
      Emitter.this.writeIndicator(":", true, false, true);
      Emitter.this.states.push(new Emitter.ExpectBlockMappingKey(false));
      Emitter.this.expectNode(false, true, false);
    }
  }
  
  private boolean checkEmptySequence() {
    return (this.event instanceof SequenceStartEvent && !this.events.isEmpty() && this.events.peek() instanceof org.yaml.snakeyaml.events.SequenceEndEvent);
  }
  
  private boolean checkEmptyMapping() {
    return (this.event instanceof MappingStartEvent && !this.events.isEmpty() && this.events.peek() instanceof org.yaml.snakeyaml.events.MappingEndEvent);
  }
  
  private boolean checkEmptyDocument() {
    if (!(this.event instanceof DocumentStartEvent) || this.events.isEmpty())
      return false; 
    Event event = this.events.peek();
    if (event instanceof ScalarEvent) {
      ScalarEvent scalarEvent = (ScalarEvent)event;
      return (scalarEvent.getAnchor() == null && scalarEvent.getTag() == null && scalarEvent.getImplicit() != null && scalarEvent
        .getValue().length() == 0);
    } 
    return false;
  }
  
  private boolean checkSimpleKey() {
    int i = 0;
    if (this.event instanceof NodeEvent && ((NodeEvent)this.event).getAnchor() != null) {
      if (this.preparedAnchor == null)
        this.preparedAnchor = prepareAnchor(((NodeEvent)this.event).getAnchor()); 
      i += this.preparedAnchor.length();
    } 
    String str = null;
    if (this.event instanceof ScalarEvent) {
      str = ((ScalarEvent)this.event).getTag();
    } else if (this.event instanceof CollectionStartEvent) {
      str = ((CollectionStartEvent)this.event).getTag();
    } 
    if (str != null) {
      if (this.preparedTag == null)
        this.preparedTag = prepareTag(str); 
      i += this.preparedTag.length();
    } 
    if (this.event instanceof ScalarEvent) {
      if (this.analysis == null)
        this.analysis = analyzeScalar(((ScalarEvent)this.event).getValue()); 
      i += this.analysis.scalar.length();
    } 
    return (i < 128 && (this.event instanceof org.yaml.snakeyaml.events.AliasEvent || (this.event instanceof ScalarEvent && !this.analysis.empty && !this.analysis.multiline) || 
      
      checkEmptySequence() || checkEmptyMapping()));
  }
  
  private void processAnchor(String paramString) throws IOException {
    NodeEvent nodeEvent = (NodeEvent)this.event;
    if (nodeEvent.getAnchor() == null) {
      this.preparedAnchor = null;
      return;
    } 
    if (this.preparedAnchor == null)
      this.preparedAnchor = prepareAnchor(nodeEvent.getAnchor()); 
    writeIndicator(paramString + this.preparedAnchor, true, false, false);
    this.preparedAnchor = null;
  }
  
  private void processTag() throws IOException {
    String str = null;
    if (this.event instanceof ScalarEvent) {
      ScalarEvent scalarEvent = (ScalarEvent)this.event;
      str = scalarEvent.getTag();
      if (this.style == null)
        this.style = chooseScalarStyle(); 
      if ((!this.canonical.booleanValue() || str == null) && ((this.style == null && scalarEvent.getImplicit()
        .canOmitTagInPlainScalar()) || (this.style != null && scalarEvent.getImplicit()
        .canOmitTagInNonPlainScalar()))) {
        this.preparedTag = null;
        return;
      } 
      if (scalarEvent.getImplicit().canOmitTagInPlainScalar() && str == null) {
        str = "!";
        this.preparedTag = null;
      } 
    } else {
      CollectionStartEvent collectionStartEvent = (CollectionStartEvent)this.event;
      str = collectionStartEvent.getTag();
      if ((!this.canonical.booleanValue() || str == null) && collectionStartEvent.getImplicit()) {
        this.preparedTag = null;
        return;
      } 
    } 
    if (str == null)
      throw new EmitterException("tag is not specified"); 
    if (this.preparedTag == null)
      this.preparedTag = prepareTag(str); 
    writeIndicator(this.preparedTag, true, false, false);
    this.preparedTag = null;
  }
  
  private Character chooseScalarStyle() {
    ScalarEvent scalarEvent = (ScalarEvent)this.event;
    if (this.analysis == null)
      this.analysis = analyzeScalar(scalarEvent.getValue()); 
    if ((scalarEvent.getStyle() != null && scalarEvent.getStyle().charValue() == '"') || this.canonical.booleanValue())
      return Character.valueOf('"'); 
    if (scalarEvent.getStyle() == null && scalarEvent.getImplicit().canOmitTagInPlainScalar() && (
      !this.simpleKeyContext || (!this.analysis.empty && !this.analysis.multiline)) && ((this.flowLevel != 0 && this.analysis.allowFlowPlain) || (this.flowLevel == 0 && this.analysis.allowBlockPlain)))
      return null; 
    if (scalarEvent.getStyle() != null && (scalarEvent.getStyle().charValue() == '|' || scalarEvent.getStyle().charValue() == '>') && 
      this.flowLevel == 0 && !this.simpleKeyContext && this.analysis.allowBlock)
      return scalarEvent.getStyle(); 
    if ((scalarEvent.getStyle() == null || scalarEvent.getStyle().charValue() == '\'') && 
      this.analysis.allowSingleQuoted && (!this.simpleKeyContext || !this.analysis.multiline))
      return Character.valueOf('\''); 
    return Character.valueOf('"');
  }
  
  private void processScalar() throws IOException {
    ScalarEvent scalarEvent = (ScalarEvent)this.event;
    if (this.analysis == null)
      this.analysis = analyzeScalar(scalarEvent.getValue()); 
    if (this.style == null)
      this.style = chooseScalarStyle(); 
    boolean bool = (!this.simpleKeyContext && this.splitLines) ? true : false;
    if (this.style == null) {
      writePlain(this.analysis.scalar, bool);
    } else {
      switch (this.style.charValue()) {
        case '"':
          writeDoubleQuoted(this.analysis.scalar, bool);
          break;
        case '\'':
          writeSingleQuoted(this.analysis.scalar, bool);
          break;
        case '>':
          writeFolded(this.analysis.scalar, bool);
          break;
        case '|':
          writeLiteral(this.analysis.scalar);
          break;
        default:
          throw new YAMLException("Unexpected style: " + this.style);
      } 
    } 
    this.analysis = null;
    this.style = null;
  }
  
  private String prepareVersion(DumperOptions.Version paramVersion) {
    if (paramVersion.major() != 1)
      throw new EmitterException("unsupported YAML version: " + paramVersion); 
    return paramVersion.getRepresentation();
  }
  
  private static final Pattern HANDLE_FORMAT = Pattern.compile("^![-_\\w]*!$");
  
  private String prepareTagHandle(String paramString) {
    if (paramString.length() == 0)
      throw new EmitterException("tag handle must not be empty"); 
    if (paramString.charAt(0) != '!' || paramString.charAt(paramString.length() - 1) != '!')
      throw new EmitterException("tag handle must start and end with '!': " + paramString); 
    if (!"!".equals(paramString) && !HANDLE_FORMAT.matcher(paramString).matches())
      throw new EmitterException("invalid character in the tag handle: " + paramString); 
    return paramString;
  }
  
  private String prepareTagPrefix(String paramString) {
    if (paramString.length() == 0)
      throw new EmitterException("tag prefix must not be empty"); 
    StringBuilder stringBuilder = new StringBuilder();
    byte b1 = 0;
    byte b2 = 0;
    if (paramString.charAt(0) == '!')
      b2 = 1; 
    while (b2 < paramString.length())
      b2++; 
    if (b1 < b2)
      stringBuilder.append(paramString.substring(b1, b2)); 
    return stringBuilder.toString();
  }
  
  private String prepareTag(String paramString) {
    if (paramString.length() == 0)
      throw new EmitterException("tag must not be empty"); 
    if ("!".equals(paramString))
      return paramString; 
    String str1 = null;
    String str2 = paramString;
    for (String str : this.tagPrefixes.keySet()) {
      if (paramString.startsWith(str) && ("!".equals(str) || str.length() < paramString.length()))
        str1 = str; 
    } 
    if (str1 != null) {
      str2 = paramString.substring(str1.length());
      str1 = this.tagPrefixes.get(str1);
    } 
    int i = str2.length();
    String str3 = (i > 0) ? str2.substring(0, i) : "";
    if (str1 != null)
      return str1 + str3; 
    return "!<" + str3 + ">";
  }
  
  private static final Pattern ANCHOR_FORMAT = Pattern.compile("^[-_\\w]*$");
  
  static String prepareAnchor(String paramString) {
    if (paramString.length() == 0)
      throw new EmitterException("anchor must not be empty"); 
    if (!ANCHOR_FORMAT.matcher(paramString).matches())
      throw new EmitterException("invalid character in the anchor: " + paramString); 
    return paramString;
  }
  
  private ScalarAnalysis analyzeScalar(String paramString) {
    if (paramString.length() == 0)
      return new ScalarAnalysis(paramString, true, false, false, true, true, false); 
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    boolean bool5 = false;
    boolean bool6 = false;
    boolean bool7 = false;
    boolean bool8 = false;
    boolean bool9 = false;
    boolean bool10 = false;
    if (paramString.startsWith("---") || paramString.startsWith("...")) {
      bool1 = true;
      bool2 = true;
    } 
    boolean bool11 = true;
    boolean bool12 = (paramString.length() == 1 || Constant.NULL_BL_T_LINEBR.has(paramString.codePointAt(1))) ? true : false;
    boolean bool13 = false;
    boolean bool14 = false;
    int i = 0;
    while (i < paramString.length()) {
      int j = paramString.codePointAt(i);
      if (i == 0) {
        if ("#,[]{}&*!|>'\"%@`".indexOf(j) != -1) {
          bool2 = true;
          bool1 = true;
        } 
        if (j == 63 || j == 58) {
          bool2 = true;
          if (bool12)
            bool1 = true; 
        } 
        if (j == 45 && bool12) {
          bool2 = true;
          bool1 = true;
        } 
      } else {
        if (",?[]{}".indexOf(j) != -1)
          bool2 = true; 
        if (j == 58) {
          bool2 = true;
          if (bool12)
            bool1 = true; 
        } 
        if (j == 35 && bool11) {
          bool2 = true;
          bool1 = true;
        } 
      } 
      boolean bool = Constant.LINEBR.has(j);
      if (bool)
        bool3 = true; 
      if (j != 10 && (32 > j || j > 126))
        if (j == 133 || (j >= 160 && j <= 55295) || (j >= 57344 && j <= 65533) || (j >= 65536 && j <= 1114111)) {
          if (!this.allowUnicode)
            bool4 = true; 
        } else {
          bool4 = true;
        }  
      if (j == 32) {
        if (i == 0)
          bool5 = true; 
        if (i == paramString.length() - 1)
          bool7 = true; 
        if (bool14)
          bool9 = true; 
        bool13 = true;
        bool14 = false;
      } else if (bool) {
        if (i == 0)
          bool6 = true; 
        if (i == paramString.length() - 1)
          bool8 = true; 
        if (bool13)
          bool10 = true; 
        bool13 = false;
        bool14 = true;
      } else {
        bool13 = false;
        bool14 = false;
      } 
      i += Character.charCount(j);
      bool11 = (Constant.NULL_BL_T.has(j) || bool) ? true : false;
      bool12 = true;
      if (i + 1 < paramString.length()) {
        int k = i + Character.charCount(paramString.codePointAt(i));
        if (k < paramString.length())
          bool12 = (Constant.NULL_BL_T.has(paramString.codePointAt(k)) || bool) ? true : false; 
      } 
    } 
    boolean bool15 = true;
    boolean bool16 = true;
    boolean bool17 = true;
    boolean bool18 = true;
    if (bool5 || bool6 || bool7 || bool8)
      bool15 = bool16 = false; 
    if (bool7)
      bool18 = false; 
    if (bool9)
      bool15 = bool16 = bool17 = false; 
    if (bool10 || bool4)
      bool15 = bool16 = bool17 = bool18 = false; 
    if (bool3)
      bool15 = false; 
    if (bool2)
      bool15 = false; 
    if (bool1)
      bool16 = false; 
    return new ScalarAnalysis(paramString, false, bool3, bool15, bool16, bool17, bool18);
  }
  
  void flushStream() throws IOException {
    this.stream.flush();
  }
  
  void writeStreamStart() {}
  
  void writeStreamEnd() throws IOException {
    flushStream();
  }
  
  void writeIndicator(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) throws IOException {
    if (!this.whitespace && paramBoolean1) {
      this.column++;
      this.stream.write(SPACE);
    } 
    this.whitespace = paramBoolean2;
    this.indention = (this.indention && paramBoolean3);
    this.column += paramString.length();
    this.openEnded = false;
    this.stream.write(paramString);
  }
  
  void writeIndent() throws IOException {
    byte b;
    if (this.indent != null) {
      b = this.indent.intValue();
    } else {
      b = 0;
    } 
    if (!this.indention || this.column > b || (this.column == b && !this.whitespace))
      writeLineBreak(null); 
    writeWhitespace(b - this.column);
  }
  
  private void writeWhitespace(int paramInt) throws IOException {
    if (paramInt <= 0)
      return; 
    this.whitespace = true;
    char[] arrayOfChar = new char[paramInt];
    for (byte b = 0; b < arrayOfChar.length; b++)
      arrayOfChar[b] = ' '; 
    this.column += paramInt;
    this.stream.write(arrayOfChar);
  }
  
  private void writeLineBreak(String paramString) throws IOException {
    this.whitespace = true;
    this.indention = true;
    this.column = 0;
    if (paramString == null) {
      this.stream.write(this.bestLineBreak);
    } else {
      this.stream.write(paramString);
    } 
  }
  
  void writeVersionDirective(String paramString) throws IOException {
    this.stream.write("%YAML ");
    this.stream.write(paramString);
    writeLineBreak(null);
  }
  
  void writeTagDirective(String paramString1, String paramString2) throws IOException {
    this.stream.write("%TAG ");
    this.stream.write(paramString1);
    this.stream.write(SPACE);
    this.stream.write(paramString2);
    writeLineBreak(null);
  }
  
  private void writeSingleQuoted(String paramString, boolean paramBoolean) throws IOException {
    writeIndicator("'", true, false, false);
    boolean bool = false;
    boolean bool1 = false;
    int i = 0;
    byte b = 0;
    while (b <= paramString.length()) {
      char c = Character.MIN_VALUE;
      if (b < paramString.length())
        c = paramString.charAt(b); 
      if (bool) {
        if (c == '\000' || c != ' ') {
          if (i + 1 == b && this.column > this.bestWidth && paramBoolean && i != 0 && b != paramString
            .length()) {
            writeIndent();
          } else {
            int j = b - i;
            this.column += j;
            this.stream.write(paramString, i, j);
          } 
          i = b;
        } 
      } else if (bool1) {
        if (c == '\000' || Constant.LINEBR.hasNo(c)) {
          if (paramString.charAt(i) == '\n')
            writeLineBreak(null); 
          String str = paramString.substring(i, b);
          for (char c1 : str.toCharArray()) {
            if (c1 == '\n') {
              writeLineBreak(null);
            } else {
              writeLineBreak(String.valueOf(c1));
            } 
          } 
          writeIndent();
          i = b;
        } 
      } else if (Constant.LINEBR.has(c, "\000 '") && 
        i < b) {
        int j = b - i;
        this.column += j;
        this.stream.write(paramString, i, j);
        i = b;
      } 
      if (c == '\'') {
        this.column += 2;
        this.stream.write("''");
        i = b + 1;
      } 
      if (c != '\000') {
        bool = (c == ' ') ? true : false;
        bool1 = Constant.LINEBR.has(c);
      } 
      b++;
    } 
    writeIndicator("'", false, false, false);
  }
  
  private void writeDoubleQuoted(String paramString, boolean paramBoolean) throws IOException {
    writeIndicator("\"", true, false, false);
    int i = 0;
    byte b = 0;
    while (b <= paramString.length()) {
      Character character = null;
      if (b < paramString.length())
        character = Character.valueOf(paramString.charAt(b)); 
      if (character == null || "\"\\  ﻿".indexOf(character.charValue()) != -1 || ' ' > character
        .charValue() || character.charValue() > '~') {
        if (i < b) {
          int j = b - i;
          this.column += j;
          this.stream.write(paramString, i, j);
          i = b;
        } 
        if (character != null) {
          String str;
          if (ESCAPE_REPLACEMENTS.containsKey(character)) {
            str = "\\" + (String)ESCAPE_REPLACEMENTS.get(character);
          } else if (!this.allowUnicode || !StreamReader.isPrintable(character.charValue())) {
            if (character.charValue() <= 'ÿ') {
              String str1 = "0" + Integer.toString(character.charValue(), 16);
              str = "\\x" + str1.substring(str1.length() - 2);
            } else if (character.charValue() >= '?' && character.charValue() <= '?') {
              if (b + 1 < paramString.length()) {
                Character character1 = Character.valueOf(paramString.charAt(++b));
                String str1 = "000" + Long.toHexString(Character.toCodePoint(character.charValue(), character1.charValue()));
                str = "\\U" + str1.substring(str1.length() - 8);
              } else {
                String str1 = "000" + Integer.toString(character.charValue(), 16);
                str = "\\u" + str1.substring(str1.length() - 4);
              } 
            } else {
              String str1 = "000" + Integer.toString(character.charValue(), 16);
              str = "\\u" + str1.substring(str1.length() - 4);
            } 
          } else {
            str = String.valueOf(character);
          } 
          this.column += str.length();
          this.stream.write(str);
          i = b + 1;
        } 
      } 
      if (0 < b && b < paramString.length() - 1 && (character.charValue() == ' ' || i >= b) && this.column + b - i > this.bestWidth && paramBoolean) {
        String str;
        if (i >= b) {
          str = "\\";
        } else {
          str = paramString.substring(i, b) + "\\";
        } 
        if (i < b)
          i = b; 
        this.column += str.length();
        this.stream.write(str);
        writeIndent();
        this.whitespace = false;
        this.indention = false;
        if (paramString.charAt(i) == ' ') {
          str = "\\";
          this.column += str.length();
          this.stream.write(str);
        } 
      } 
      b++;
    } 
    writeIndicator("\"", false, false, false);
  }
  
  private String determineBlockHints(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    if (Constant.LINEBR.has(paramString.charAt(0), " "))
      stringBuilder.append(this.bestIndent); 
    char c = paramString.charAt(paramString.length() - 1);
    if (Constant.LINEBR.hasNo(c)) {
      stringBuilder.append("-");
    } else if (paramString.length() == 1 || Constant.LINEBR.has(paramString.charAt(paramString.length() - 2))) {
      stringBuilder.append("+");
    } 
    return stringBuilder.toString();
  }
  
  void writeFolded(String paramString, boolean paramBoolean) throws IOException {
    String str = determineBlockHints(paramString);
    writeIndicator(">" + str, true, false, false);
    if (str.length() > 0 && str.charAt(str.length() - 1) == '+')
      this.openEnded = true; 
    writeLineBreak(null);
    boolean bool1 = true;
    boolean bool2 = false;
    boolean bool = true;
    byte b1 = 0, b2 = 0;
    while (b2 <= paramString.length()) {
      char c = Character.MIN_VALUE;
      if (b2 < paramString.length())
        c = paramString.charAt(b2); 
      if (bool) {
        if (c == '\000' || Constant.LINEBR.hasNo(c)) {
          if (!bool1 && c != '\000' && c != ' ' && paramString.charAt(b1) == '\n')
            writeLineBreak(null); 
          bool1 = (c == ' ') ? true : false;
          String str1 = paramString.substring(b1, b2);
          for (char c1 : str1.toCharArray()) {
            if (c1 == '\n') {
              writeLineBreak(null);
            } else {
              writeLineBreak(String.valueOf(c1));
            } 
          } 
          if (c != '\000')
            writeIndent(); 
          b1 = b2;
        } 
      } else if (bool2) {
        if (c != ' ') {
          if (b1 + 1 == b2 && this.column > this.bestWidth && paramBoolean) {
            writeIndent();
          } else {
            int i = b2 - b1;
            this.column += i;
            this.stream.write(paramString, b1, i);
          } 
          b1 = b2;
        } 
      } else if (Constant.LINEBR.has(c, "\000 ")) {
        int i = b2 - b1;
        this.column += i;
        this.stream.write(paramString, b1, i);
        if (c == '\000')
          writeLineBreak(null); 
        b1 = b2;
      } 
      if (c != '\000') {
        bool = Constant.LINEBR.has(c);
        bool2 = (c == ' ') ? true : false;
      } 
      b2++;
    } 
  }
  
  void writeLiteral(String paramString) throws IOException {
    String str = determineBlockHints(paramString);
    writeIndicator("|" + str, true, false, false);
    if (str.length() > 0 && str.charAt(str.length() - 1) == '+')
      this.openEnded = true; 
    writeLineBreak(null);
    boolean bool = true;
    byte b1 = 0, b2 = 0;
    while (b2 <= paramString.length()) {
      char c = Character.MIN_VALUE;
      if (b2 < paramString.length())
        c = paramString.charAt(b2); 
      if (bool) {
        if (c == '\000' || Constant.LINEBR.hasNo(c)) {
          String str1 = paramString.substring(b1, b2);
          for (char c1 : str1.toCharArray()) {
            if (c1 == '\n') {
              writeLineBreak(null);
            } else {
              writeLineBreak(String.valueOf(c1));
            } 
          } 
          if (c != '\000')
            writeIndent(); 
          b1 = b2;
        } 
      } else if (c == '\000' || Constant.LINEBR.has(c)) {
        this.stream.write(paramString, b1, b2 - b1);
        if (c == '\000')
          writeLineBreak(null); 
        b1 = b2;
      } 
      if (c != '\000')
        bool = Constant.LINEBR.has(c); 
      b2++;
    } 
  }
  
  void writePlain(String paramString, boolean paramBoolean) throws IOException {
    if (this.rootContext)
      this.openEnded = true; 
    if (paramString.length() == 0)
      return; 
    if (!this.whitespace) {
      this.column++;
      this.stream.write(SPACE);
    } 
    this.whitespace = false;
    this.indention = false;
    boolean bool = false;
    boolean bool1 = false;
    byte b1 = 0, b2 = 0;
    while (b2 <= paramString.length()) {
      char c = Character.MIN_VALUE;
      if (b2 < paramString.length())
        c = paramString.charAt(b2); 
      if (bool) {
        if (c != ' ') {
          if (b1 + 1 == b2 && this.column > this.bestWidth && paramBoolean) {
            writeIndent();
            this.whitespace = false;
            this.indention = false;
          } else {
            int i = b2 - b1;
            this.column += i;
            this.stream.write(paramString, b1, i);
          } 
          b1 = b2;
        } 
      } else if (bool1) {
        if (Constant.LINEBR.hasNo(c)) {
          if (paramString.charAt(b1) == '\n')
            writeLineBreak(null); 
          String str = paramString.substring(b1, b2);
          for (char c1 : str.toCharArray()) {
            if (c1 == '\n') {
              writeLineBreak(null);
            } else {
              writeLineBreak(String.valueOf(c1));
            } 
          } 
          writeIndent();
          this.whitespace = false;
          this.indention = false;
          b1 = b2;
        } 
      } else if (Constant.LINEBR.has(c, "\000 ")) {
        int i = b2 - b1;
        this.column += i;
        this.stream.write(paramString, b1, i);
        b1 = b2;
      } 
      if (c != '\000') {
        bool = (c == ' ') ? true : false;
        bool1 = Constant.LINEBR.has(c);
      } 
      b2++;
    } 
  }
}
