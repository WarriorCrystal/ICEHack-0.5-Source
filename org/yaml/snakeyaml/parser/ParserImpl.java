package org.yaml.snakeyaml.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.tokens.StreamStartToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.util.ArrayStack;

public class ParserImpl implements Parser {
  private static final Map<String, String> DEFAULT_TAGS = new HashMap<String, String>();
  
  protected final Scanner scanner;
  
  private Event currentEvent;
  
  private final ArrayStack<Production> states;
  
  private final ArrayStack<Mark> marks;
  
  private Production state;
  
  private VersionTagsTuple directives;
  
  static {
    DEFAULT_TAGS.put("!", "!");
    DEFAULT_TAGS.put("!!", "tag:yaml.org,2002:");
  }
  
  public ParserImpl(StreamReader paramStreamReader) {
    this((Scanner)new ScannerImpl(paramStreamReader));
  }
  
  public ParserImpl(Scanner paramScanner) {
    this.scanner = paramScanner;
    this.currentEvent = null;
    this.directives = new VersionTagsTuple(null, new HashMap<String, String>(DEFAULT_TAGS));
    this.states = new ArrayStack(100);
    this.marks = new ArrayStack(10);
    this.state = new ParseStreamStart();
  }
  
  public boolean checkEvent(Event.ID paramID) {
    peekEvent();
    return (this.currentEvent != null && this.currentEvent.is(paramID));
  }
  
  public Event peekEvent() {
    if (this.currentEvent == null && 
      this.state != null)
      this.currentEvent = this.state.produce(); 
    return this.currentEvent;
  }
  
  public Event getEvent() {
    peekEvent();
    Event event = this.currentEvent;
    this.currentEvent = null;
    return event;
  }
  
  private class ParseStreamStart implements Production {
    private ParseStreamStart() {}
    
    public Event produce() {
      StreamStartToken streamStartToken = (StreamStartToken)ParserImpl.this.scanner.getToken();
      StreamStartEvent streamStartEvent = new StreamStartEvent(streamStartToken.getStartMark(), streamStartToken.getEndMark());
      ParserImpl.this.state = new ParserImpl.ParseImplicitDocumentStart();
      return (Event)streamStartEvent;
    }
  }
  
  private class ParseImplicitDocumentStart implements Production {
    private ParseImplicitDocumentStart() {}
    
    public Event produce() {
      if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Directive, Token.ID.DocumentStart, Token.ID.StreamEnd })) {
        ParserImpl.this.directives = new VersionTagsTuple(null, ParserImpl.DEFAULT_TAGS);
        Token token = ParserImpl.this.scanner.peekToken();
        Mark mark1 = token.getStartMark();
        Mark mark2 = mark1;
        DocumentStartEvent documentStartEvent = new DocumentStartEvent(mark1, mark2, false, null, null);
        ParserImpl.this.states.push(new ParserImpl.ParseDocumentEnd());
        ParserImpl.this.state = new ParserImpl.ParseBlockNode();
        return (Event)documentStartEvent;
      } 
      ParserImpl.ParseDocumentStart parseDocumentStart = new ParserImpl.ParseDocumentStart();
      return parseDocumentStart.produce();
    }
  }
  
  private class ParseDocumentStart implements Production {
    private ParseDocumentStart() {}
    
    public Event produce() {
      StreamEndEvent streamEndEvent;
      while (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.DocumentEnd }))
        ParserImpl.this.scanner.getToken(); 
      if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.StreamEnd })) {
        Token token = ParserImpl.this.scanner.peekToken();
        Mark mark1 = token.getStartMark();
        VersionTagsTuple versionTagsTuple = ParserImpl.this.processDirectives();
        if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.DocumentStart }))
          throw new ParserException(null, null, "expected '<document start>', but found " + ParserImpl.this.scanner
              .peekToken().getTokenId(), ParserImpl.this.scanner.peekToken().getStartMark()); 
        token = ParserImpl.this.scanner.getToken();
        Mark mark2 = token.getEndMark();
        DocumentStartEvent documentStartEvent = new DocumentStartEvent(mark1, mark2, true, versionTagsTuple.getVersion(), versionTagsTuple.getTags());
        ParserImpl.this.states.push(new ParserImpl.ParseDocumentEnd());
        ParserImpl.this.state = new ParserImpl.ParseDocumentContent();
      } else {
        StreamEndToken streamEndToken = (StreamEndToken)ParserImpl.this.scanner.getToken();
        streamEndEvent = new StreamEndEvent(streamEndToken.getStartMark(), streamEndToken.getEndMark());
        if (!ParserImpl.this.states.isEmpty())
          throw new YAMLException("Unexpected end of stream. States left: " + ParserImpl.this.states); 
        if (!ParserImpl.this.marks.isEmpty())
          throw new YAMLException("Unexpected end of stream. Marks left: " + ParserImpl.this.marks); 
        ParserImpl.this.state = null;
      } 
      return (Event)streamEndEvent;
    }
  }
  
  private class ParseDocumentEnd implements Production {
    private ParseDocumentEnd() {}
    
    public Event produce() {
      Token token = ParserImpl.this.scanner.peekToken();
      Mark mark1 = token.getStartMark();
      Mark mark2 = mark1;
      boolean bool = false;
      if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.DocumentEnd })) {
        token = ParserImpl.this.scanner.getToken();
        mark2 = token.getEndMark();
        bool = true;
      } 
      DocumentEndEvent documentEndEvent = new DocumentEndEvent(mark1, mark2, bool);
      ParserImpl.this.state = new ParserImpl.ParseDocumentStart();
      return (Event)documentEndEvent;
    }
  }
  
  private class ParseDocumentContent implements Production {
    private ParseDocumentContent() {}
    
    public Event produce() {
      if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Directive, Token.ID.DocumentStart, Token.ID.DocumentEnd, Token.ID.StreamEnd })) {
        Event event = ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
        ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
        return event;
      } 
      ParserImpl.ParseBlockNode parseBlockNode = new ParserImpl.ParseBlockNode();
      return parseBlockNode.produce();
    }
  }
  
  private VersionTagsTuple processDirectives() {
    DumperOptions.Version version = null;
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    while (this.scanner.checkToken(new Token.ID[] { Token.ID.Directive })) {
      DirectiveToken directiveToken = (DirectiveToken)this.scanner.getToken();
      if (directiveToken.getName().equals("YAML")) {
        if (version != null)
          throw new ParserException(null, null, "found duplicate YAML directive", directiveToken
              .getStartMark()); 
        List<Integer> list = directiveToken.getValue();
        Integer integer1 = list.get(0);
        if (integer1.intValue() != 1)
          throw new ParserException(null, null, "found incompatible YAML document (version 1.* is required)", directiveToken
              
              .getStartMark()); 
        Integer integer2 = list.get(1);
        switch (integer2.intValue()) {
          case 0:
            version = DumperOptions.Version.V1_0;
            continue;
        } 
        version = DumperOptions.Version.V1_1;
        continue;
      } 
      if (directiveToken.getName().equals("TAG")) {
        List<String> list = directiveToken.getValue();
        String str1 = list.get(0);
        String str2 = list.get(1);
        if (hashMap.containsKey(str1))
          throw new ParserException(null, null, "duplicate tag handle " + str1, directiveToken
              .getStartMark()); 
        hashMap.put(str1, str2);
      } 
    } 
    if (version != null || !hashMap.isEmpty()) {
      for (String str : DEFAULT_TAGS.keySet()) {
        if (!hashMap.containsKey(str))
          hashMap.put(str, DEFAULT_TAGS.get(str)); 
      } 
      this.directives = new VersionTagsTuple(version, (Map)hashMap);
    } 
    return this.directives;
  }
  
  private class ParseBlockNode implements Production {
    private ParseBlockNode() {}
    
    public Event produce() {
      return ParserImpl.this.parseNode(true, false);
    }
  }
  
  private Event parseFlowNode() {
    return parseNode(false, false);
  }
  
  private Event parseBlockNodeOrIndentlessSequence() {
    return parseNode(true, true);
  }
  
  private Event parseNode(boolean paramBoolean1, boolean paramBoolean2) {
    ScalarEvent scalarEvent;
    Mark mark1 = null;
    Mark mark2 = null;
    Mark mark3 = null;
    if (this.scanner.checkToken(new Token.ID[] { Token.ID.Alias })) {
      AliasToken aliasToken = (AliasToken)this.scanner.getToken();
      AliasEvent aliasEvent = new AliasEvent(aliasToken.getValue(), aliasToken.getStartMark(), aliasToken.getEndMark());
      this.state = (Production)this.states.pop();
    } else {
      String str1 = null;
      TagTuple tagTuple = null;
      if (this.scanner.checkToken(new Token.ID[] { Token.ID.Anchor })) {
        AnchorToken anchorToken = (AnchorToken)this.scanner.getToken();
        mark1 = anchorToken.getStartMark();
        mark2 = anchorToken.getEndMark();
        str1 = anchorToken.getValue();
        if (this.scanner.checkToken(new Token.ID[] { Token.ID.Tag })) {
          TagToken tagToken = (TagToken)this.scanner.getToken();
          mark3 = tagToken.getStartMark();
          mark2 = tagToken.getEndMark();
          tagTuple = tagToken.getValue();
        } 
      } else {
        TagToken tagToken = (TagToken)this.scanner.getToken();
        mark1 = tagToken.getStartMark();
        mark3 = mark1;
        mark2 = tagToken.getEndMark();
        tagTuple = tagToken.getValue();
        if (this.scanner.checkToken(new Token.ID[] { Token.ID.Tag }) && this.scanner.checkToken(new Token.ID[] { Token.ID.Anchor })) {
          AnchorToken anchorToken = (AnchorToken)this.scanner.getToken();
          mark2 = anchorToken.getEndMark();
          str1 = anchorToken.getValue();
        } 
      } 
      String str2 = null;
      if (tagTuple != null) {
        String str3 = tagTuple.getHandle();
        String str4 = tagTuple.getSuffix();
        if (str3 != null) {
          if (!this.directives.getTags().containsKey(str3))
            throw new ParserException("while parsing a node", mark1, "found undefined tag handle " + str3, mark3); 
          str2 = (String)this.directives.getTags().get(str3) + str4;
        } else {
          str2 = str4;
        } 
      } 
      if (mark1 == null) {
        mark1 = this.scanner.peekToken().getStartMark();
        mark2 = mark1;
      } 
      SequenceStartEvent sequenceStartEvent = null;
      boolean bool = (str2 == null || str2.equals("!")) ? true : false;
      if (paramBoolean2 && this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry })) {
        mark2 = this.scanner.peekToken().getEndMark();
        sequenceStartEvent = new SequenceStartEvent(str1, str2, bool, mark1, mark2, Boolean.FALSE);
        this.state = new ParseIndentlessSequenceEntry();
      } else if (this.scanner.checkToken(new Token.ID[] { Token.ID.Scalar })) {
        ImplicitTuple implicitTuple;
        ScalarToken scalarToken = (ScalarToken)this.scanner.getToken();
        mark2 = scalarToken.getEndMark();
        if ((scalarToken.getPlain() && str2 == null) || "!".equals(str2)) {
          implicitTuple = new ImplicitTuple(true, false);
        } else if (str2 == null) {
          implicitTuple = new ImplicitTuple(false, true);
        } else {
          implicitTuple = new ImplicitTuple(false, false);
        } 
        scalarEvent = new ScalarEvent(str1, str2, implicitTuple, scalarToken.getValue(), mark1, mark2, Character.valueOf(scalarToken.getStyle()));
        this.state = (Production)this.states.pop();
      } else if (this.scanner.checkToken(new Token.ID[] { Token.ID.FlowSequenceStart })) {
        mark2 = this.scanner.peekToken().getEndMark();
        sequenceStartEvent = new SequenceStartEvent(str1, str2, bool, mark1, mark2, Boolean.TRUE);
        this.state = new ParseFlowSequenceFirstEntry();
      } else if (this.scanner.checkToken(new Token.ID[] { Token.ID.FlowMappingStart })) {
        mark2 = this.scanner.peekToken().getEndMark();
        MappingStartEvent mappingStartEvent = new MappingStartEvent(str1, str2, bool, mark1, mark2, Boolean.TRUE);
        this.state = new ParseFlowMappingFirstKey();
      } else if (paramBoolean1 && this.scanner.checkToken(new Token.ID[] { Token.ID.BlockSequenceStart })) {
        mark2 = this.scanner.peekToken().getStartMark();
        sequenceStartEvent = new SequenceStartEvent(str1, str2, bool, mark1, mark2, Boolean.FALSE);
        this.state = new ParseBlockSequenceFirstEntry();
      } else if (paramBoolean1 && this.scanner.checkToken(new Token.ID[] { Token.ID.BlockMappingStart })) {
        mark2 = this.scanner.peekToken().getStartMark();
        MappingStartEvent mappingStartEvent = new MappingStartEvent(str1, str2, bool, mark1, mark2, Boolean.FALSE);
        this.state = new ParseBlockMappingFirstKey();
      } else if (str1 != null || str2 != null) {
        scalarEvent = new ScalarEvent(str1, str2, new ImplicitTuple(bool, false), "", mark1, mark2, Character.valueOf(false));
        this.state = (Production)this.states.pop();
      } else {
        String str;
        if (paramBoolean1) {
          str = "block";
        } else {
          str = "flow";
        } 
        Token token = this.scanner.peekToken();
        throw new ParserException("while parsing a " + str + " node", mark1, "expected the node content, but found " + token
            .getTokenId(), token
            .getStartMark());
      } 
    } 
    return (Event)scalarEvent;
  }
  
  private class ParseBlockSequenceFirstEntry implements Production {
    private ParseBlockSequenceFirstEntry() {}
    
    public Event produce() {
      Token token = ParserImpl.this.scanner.getToken();
      ParserImpl.this.marks.push(token.getStartMark());
      return (new ParserImpl.ParseBlockSequenceEntry()).produce();
    }
  }
  
  private class ParseBlockSequenceEntry implements Production {
    private ParseBlockSequenceEntry() {}
    
    public Event produce() {
      if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry })) {
        BlockEntryToken blockEntryToken = (BlockEntryToken)ParserImpl.this.scanner.getToken();
        if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry, Token.ID.BlockEnd })) {
          ParserImpl.this.states.push(new ParseBlockSequenceEntry());
          return (new ParserImpl.ParseBlockNode()).produce();
        } 
        ParserImpl.this.state = new ParseBlockSequenceEntry();
        return ParserImpl.this.processEmptyScalar(blockEntryToken.getEndMark());
      } 
      if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEnd })) {
        Token token1 = ParserImpl.this.scanner.peekToken();
        throw new ParserException("while parsing a block collection", (Mark)ParserImpl.this.marks.pop(), "expected <block end>, but found " + token1
            .getTokenId(), token1
            .getStartMark());
      } 
      Token token = ParserImpl.this.scanner.getToken();
      SequenceEndEvent sequenceEndEvent = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
      ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
      ParserImpl.this.marks.pop();
      return (Event)sequenceEndEvent;
    }
  }
  
  private class ParseIndentlessSequenceEntry implements Production {
    private ParseIndentlessSequenceEntry() {}
    
    public Event produce() {
      if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry })) {
        Token token1 = ParserImpl.this.scanner.getToken();
        if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEntry, Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd })) {
          ParserImpl.this.states.push(new ParseIndentlessSequenceEntry());
          return (new ParserImpl.ParseBlockNode()).produce();
        } 
        ParserImpl.this.state = new ParseIndentlessSequenceEntry();
        return ParserImpl.this.processEmptyScalar(token1.getEndMark());
      } 
      Token token = ParserImpl.this.scanner.peekToken();
      SequenceEndEvent sequenceEndEvent = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
      ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
      return (Event)sequenceEndEvent;
    }
  }
  
  private class ParseBlockMappingFirstKey implements Production {
    private ParseBlockMappingFirstKey() {}
    
    public Event produce() {
      Token token = ParserImpl.this.scanner.getToken();
      ParserImpl.this.marks.push(token.getStartMark());
      return (new ParserImpl.ParseBlockMappingKey()).produce();
    }
  }
  
  private class ParseBlockMappingKey implements Production {
    private ParseBlockMappingKey() {}
    
    public Event produce() {
      if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key })) {
        Token token1 = ParserImpl.this.scanner.getToken();
        if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd })) {
          ParserImpl.this.states.push(new ParserImpl.ParseBlockMappingValue());
          return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
        } 
        ParserImpl.this.state = new ParserImpl.ParseBlockMappingValue();
        return ParserImpl.this.processEmptyScalar(token1.getEndMark());
      } 
      if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.BlockEnd })) {
        Token token1 = ParserImpl.this.scanner.peekToken();
        throw new ParserException("while parsing a block mapping", (Mark)ParserImpl.this.marks.pop(), "expected <block end>, but found " + token1
            .getTokenId(), token1
            .getStartMark());
      } 
      Token token = ParserImpl.this.scanner.getToken();
      MappingEndEvent mappingEndEvent = new MappingEndEvent(token.getStartMark(), token.getEndMark());
      ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
      ParserImpl.this.marks.pop();
      return (Event)mappingEndEvent;
    }
  }
  
  private class ParseBlockMappingValue implements Production {
    private ParseBlockMappingValue() {}
    
    public Event produce() {
      if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value })) {
        Token token1 = ParserImpl.this.scanner.getToken();
        if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd })) {
          ParserImpl.this.states.push(new ParserImpl.ParseBlockMappingKey());
          return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
        } 
        ParserImpl.this.state = new ParserImpl.ParseBlockMappingKey();
        return ParserImpl.this.processEmptyScalar(token1.getEndMark());
      } 
      ParserImpl.this.state = new ParserImpl.ParseBlockMappingKey();
      Token token = ParserImpl.this.scanner.peekToken();
      return ParserImpl.this.processEmptyScalar(token.getStartMark());
    }
  }
  
  private class ParseFlowSequenceFirstEntry implements Production {
    private ParseFlowSequenceFirstEntry() {}
    
    public Event produce() {
      Token token = ParserImpl.this.scanner.getToken();
      ParserImpl.this.marks.push(token.getStartMark());
      return (new ParserImpl.ParseFlowSequenceEntry(true)).produce();
    }
  }
  
  private class ParseFlowSequenceEntry implements Production {
    private boolean first = false;
    
    public ParseFlowSequenceEntry(boolean param1Boolean) {
      this.first = param1Boolean;
    }
    
    public Event produce() {
      if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowSequenceEnd })) {
        if (!this.first)
          if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowEntry })) {
            ParserImpl.this.scanner.getToken();
          } else {
            Token token1 = ParserImpl.this.scanner.peekToken();
            throw new ParserException("while parsing a flow sequence", (Mark)ParserImpl.this.marks.pop(), "expected ',' or ']', but got " + token1
                .getTokenId(), token1
                .getStartMark());
          }  
        if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key })) {
          Token token1 = ParserImpl.this.scanner.peekToken();
          MappingStartEvent mappingStartEvent = new MappingStartEvent(null, null, true, token1.getStartMark(), token1.getEndMark(), Boolean.TRUE);
          ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntryMappingKey();
          return (Event)mappingStartEvent;
        } 
        if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowSequenceEnd })) {
          ParserImpl.this.states.push(new ParseFlowSequenceEntry(false));
          return ParserImpl.this.parseFlowNode();
        } 
      } 
      Token token = ParserImpl.this.scanner.getToken();
      SequenceEndEvent sequenceEndEvent = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
      ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
      ParserImpl.this.marks.pop();
      return (Event)sequenceEndEvent;
    }
  }
  
  private class ParseFlowSequenceEntryMappingKey implements Production {
    private ParseFlowSequenceEntryMappingKey() {}
    
    public Event produce() {
      Token token = ParserImpl.this.scanner.getToken();
      if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowSequenceEnd })) {
        ParserImpl.this.states.push(new ParserImpl.ParseFlowSequenceEntryMappingValue());
        return ParserImpl.this.parseFlowNode();
      } 
      ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntryMappingValue();
      return ParserImpl.this.processEmptyScalar(token.getEndMark());
    }
  }
  
  private class ParseFlowSequenceEntryMappingValue implements Production {
    private ParseFlowSequenceEntryMappingValue() {}
    
    public Event produce() {
      if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value })) {
        Token token1 = ParserImpl.this.scanner.getToken();
        if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowEntry, Token.ID.FlowSequenceEnd })) {
          ParserImpl.this.states.push(new ParserImpl.ParseFlowSequenceEntryMappingEnd());
          return ParserImpl.this.parseFlowNode();
        } 
        ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntryMappingEnd();
        return ParserImpl.this.processEmptyScalar(token1.getEndMark());
      } 
      ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntryMappingEnd();
      Token token = ParserImpl.this.scanner.peekToken();
      return ParserImpl.this.processEmptyScalar(token.getStartMark());
    }
  }
  
  private class ParseFlowSequenceEntryMappingEnd implements Production {
    private ParseFlowSequenceEntryMappingEnd() {}
    
    public Event produce() {
      ParserImpl.this.state = new ParserImpl.ParseFlowSequenceEntry(false);
      Token token = ParserImpl.this.scanner.peekToken();
      return (Event)new MappingEndEvent(token.getStartMark(), token.getEndMark());
    }
  }
  
  private class ParseFlowMappingFirstKey implements Production {
    private ParseFlowMappingFirstKey() {}
    
    public Event produce() {
      Token token = ParserImpl.this.scanner.getToken();
      ParserImpl.this.marks.push(token.getStartMark());
      return (new ParserImpl.ParseFlowMappingKey(true)).produce();
    }
  }
  
  private class ParseFlowMappingKey implements Production {
    private boolean first = false;
    
    public ParseFlowMappingKey(boolean param1Boolean) {
      this.first = param1Boolean;
    }
    
    public Event produce() {
      if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowMappingEnd })) {
        if (!this.first)
          if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowEntry })) {
            ParserImpl.this.scanner.getToken();
          } else {
            Token token1 = ParserImpl.this.scanner.peekToken();
            throw new ParserException("while parsing a flow mapping", (Mark)ParserImpl.this.marks.pop(), "expected ',' or '}', but got " + token1
                .getTokenId(), token1
                .getStartMark());
          }  
        if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Key })) {
          Token token1 = ParserImpl.this.scanner.getToken();
          if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowMappingEnd })) {
            ParserImpl.this.states.push(new ParserImpl.ParseFlowMappingValue());
            return ParserImpl.this.parseFlowNode();
          } 
          ParserImpl.this.state = new ParserImpl.ParseFlowMappingValue();
          return ParserImpl.this.processEmptyScalar(token1.getEndMark());
        } 
        if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowMappingEnd })) {
          ParserImpl.this.states.push(new ParserImpl.ParseFlowMappingEmptyValue());
          return ParserImpl.this.parseFlowNode();
        } 
      } 
      Token token = ParserImpl.this.scanner.getToken();
      MappingEndEvent mappingEndEvent = new MappingEndEvent(token.getStartMark(), token.getEndMark());
      ParserImpl.this.state = (Production)ParserImpl.this.states.pop();
      ParserImpl.this.marks.pop();
      return (Event)mappingEndEvent;
    }
  }
  
  private class ParseFlowMappingValue implements Production {
    private ParseFlowMappingValue() {}
    
    public Event produce() {
      if (ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.Value })) {
        Token token1 = ParserImpl.this.scanner.getToken();
        if (!ParserImpl.this.scanner.checkToken(new Token.ID[] { Token.ID.FlowEntry, Token.ID.FlowMappingEnd })) {
          ParserImpl.this.states.push(new ParserImpl.ParseFlowMappingKey(false));
          return ParserImpl.this.parseFlowNode();
        } 
        ParserImpl.this.state = new ParserImpl.ParseFlowMappingKey(false);
        return ParserImpl.this.processEmptyScalar(token1.getEndMark());
      } 
      ParserImpl.this.state = new ParserImpl.ParseFlowMappingKey(false);
      Token token = ParserImpl.this.scanner.peekToken();
      return ParserImpl.this.processEmptyScalar(token.getStartMark());
    }
  }
  
  private class ParseFlowMappingEmptyValue implements Production {
    private ParseFlowMappingEmptyValue() {}
    
    public Event produce() {
      ParserImpl.this.state = new ParserImpl.ParseFlowMappingKey(false);
      return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
    }
  }
  
  private Event processEmptyScalar(Mark paramMark) {
    return (Event)new ScalarEvent(null, null, new ImplicitTuple(true, false), "", paramMark, paramMark, Character.valueOf(false));
  }
}
