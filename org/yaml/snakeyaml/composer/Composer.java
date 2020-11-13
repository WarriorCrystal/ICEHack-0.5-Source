package org.yaml.snakeyaml.composer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.resolver.Resolver;

public class Composer {
  protected final Parser parser;
  
  private final Resolver resolver;
  
  private final Map<String, Node> anchors;
  
  private final Set<Node> recursiveNodes;
  
  public Composer(Parser paramParser, Resolver paramResolver) {
    this.parser = paramParser;
    this.resolver = paramResolver;
    this.anchors = new HashMap<String, Node>();
    this.recursiveNodes = new HashSet<Node>();
  }
  
  public boolean checkNode() {
    if (this.parser.checkEvent(Event.ID.StreamStart))
      this.parser.getEvent(); 
    return !this.parser.checkEvent(Event.ID.StreamEnd);
  }
  
  public Node getNode() {
    if (!this.parser.checkEvent(Event.ID.StreamEnd))
      return composeDocument(); 
    return null;
  }
  
  public Node getSingleNode() {
    this.parser.getEvent();
    Node node = null;
    if (!this.parser.checkEvent(Event.ID.StreamEnd))
      node = composeDocument(); 
    if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
      Event event = this.parser.getEvent();
      throw new ComposerException("expected a single document in the stream", node
          .getStartMark(), "but found another document", event.getStartMark());
    } 
    this.parser.getEvent();
    return node;
  }
  
  private Node composeDocument() {
    this.parser.getEvent();
    Node node = composeNode(null);
    this.parser.getEvent();
    this.anchors.clear();
    this.recursiveNodes.clear();
    return node;
  }
  
  private Node composeNode(Node paramNode) {
    this.recursiveNodes.add(paramNode);
    Node node = null;
    if (this.parser.checkEvent(Event.ID.Alias)) {
      AliasEvent aliasEvent = (AliasEvent)this.parser.getEvent();
      String str = aliasEvent.getAnchor();
      if (!this.anchors.containsKey(str))
        throw new ComposerException(null, null, "found undefined alias " + str, aliasEvent
            .getStartMark()); 
      node = this.anchors.get(str);
      if (this.recursiveNodes.remove(node))
        node.setTwoStepsConstruction(true); 
    } else {
      NodeEvent nodeEvent = (NodeEvent)this.parser.peekEvent();
      String str = null;
      str = nodeEvent.getAnchor();
      if (this.parser.checkEvent(Event.ID.Scalar)) {
        node = composeScalarNode(str);
      } else if (this.parser.checkEvent(Event.ID.SequenceStart)) {
        node = composeSequenceNode(str);
      } else {
        node = composeMappingNode(str);
      } 
    } 
    this.recursiveNodes.remove(paramNode);
    return node;
  }
  
  protected Node composeScalarNode(String paramString) {
    Tag tag;
    ScalarEvent scalarEvent = (ScalarEvent)this.parser.getEvent();
    String str = scalarEvent.getTag();
    boolean bool = false;
    if (str == null || str.equals("!")) {
      tag = this.resolver.resolve(NodeId.scalar, scalarEvent.getValue(), scalarEvent
          .getImplicit().canOmitTagInPlainScalar());
      bool = true;
    } else {
      tag = new Tag(str);
    } 
    ScalarNode scalarNode = new ScalarNode(tag, bool, scalarEvent.getValue(), scalarEvent.getStartMark(), scalarEvent.getEndMark(), scalarEvent.getStyle());
    if (paramString != null) {
      scalarNode.setAnchor(paramString);
      this.anchors.put(paramString, scalarNode);
    } 
    return (Node)scalarNode;
  }
  
  protected Node composeSequenceNode(String paramString) {
    Tag tag;
    SequenceStartEvent sequenceStartEvent = (SequenceStartEvent)this.parser.getEvent();
    String str = sequenceStartEvent.getTag();
    boolean bool = false;
    if (str == null || str.equals("!")) {
      tag = this.resolver.resolve(NodeId.sequence, null, sequenceStartEvent.getImplicit());
      bool = true;
    } else {
      tag = new Tag(str);
    } 
    ArrayList<Node> arrayList = new ArrayList();
    SequenceNode sequenceNode = new SequenceNode(tag, bool, arrayList, sequenceStartEvent.getStartMark(), null, sequenceStartEvent.getFlowStyle());
    if (paramString != null) {
      sequenceNode.setAnchor(paramString);
      this.anchors.put(paramString, sequenceNode);
    } 
    while (!this.parser.checkEvent(Event.ID.SequenceEnd))
      arrayList.add(composeNode((Node)sequenceNode)); 
    Event event = this.parser.getEvent();
    sequenceNode.setEndMark(event.getEndMark());
    return (Node)sequenceNode;
  }
  
  protected Node composeMappingNode(String paramString) {
    Tag tag;
    MappingStartEvent mappingStartEvent = (MappingStartEvent)this.parser.getEvent();
    String str = mappingStartEvent.getTag();
    boolean bool = false;
    if (str == null || str.equals("!")) {
      tag = this.resolver.resolve(NodeId.mapping, null, mappingStartEvent.getImplicit());
      bool = true;
    } else {
      tag = new Tag(str);
    } 
    ArrayList<NodeTuple> arrayList = new ArrayList();
    MappingNode mappingNode = new MappingNode(tag, bool, arrayList, mappingStartEvent.getStartMark(), null, mappingStartEvent.getFlowStyle());
    if (paramString != null) {
      mappingNode.setAnchor(paramString);
      this.anchors.put(paramString, mappingNode);
    } 
    while (!this.parser.checkEvent(Event.ID.MappingEnd))
      composeMappingChildren(arrayList, mappingNode); 
    Event event = this.parser.getEvent();
    mappingNode.setEndMark(event.getEndMark());
    return (Node)mappingNode;
  }
  
  protected void composeMappingChildren(List<NodeTuple> paramList, MappingNode paramMappingNode) {
    Node node1 = composeKeyNode(paramMappingNode);
    if (node1.getTag().equals(Tag.MERGE))
      paramMappingNode.setMerged(true); 
    Node node2 = composeValueNode(paramMappingNode);
    paramList.add(new NodeTuple(node1, node2));
  }
  
  protected Node composeKeyNode(MappingNode paramMappingNode) {
    return composeNode((Node)paramMappingNode);
  }
  
  protected Node composeValueNode(MappingNode paramMappingNode) {
    return composeNode((Node)paramMappingNode);
  }
}
