package org.yaml.snakeyaml.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitable;
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
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.resolver.Resolver;

public final class Serializer {
  private final Emitable emitter;
  
  private final Resolver resolver;
  
  private boolean explicitStart;
  
  private boolean explicitEnd;
  
  private DumperOptions.Version useVersion;
  
  private Map<String, String> useTags;
  
  private Set<Node> serializedNodes;
  
  private Map<Node, String> anchors;
  
  private AnchorGenerator anchorGenerator;
  
  private Boolean closed;
  
  private Tag explicitRoot;
  
  public Serializer(Emitable paramEmitable, Resolver paramResolver, DumperOptions paramDumperOptions, Tag paramTag) {
    this.emitter = paramEmitable;
    this.resolver = paramResolver;
    this.explicitStart = paramDumperOptions.isExplicitStart();
    this.explicitEnd = paramDumperOptions.isExplicitEnd();
    if (paramDumperOptions.getVersion() != null)
      this.useVersion = paramDumperOptions.getVersion(); 
    this.useTags = paramDumperOptions.getTags();
    this.serializedNodes = new HashSet<Node>();
    this.anchors = new HashMap<Node, String>();
    this.anchorGenerator = paramDumperOptions.getAnchorGenerator();
    this.closed = null;
    this.explicitRoot = paramTag;
  }
  
  public void open() throws IOException {
    if (this.closed == null) {
      this.emitter.emit((Event)new StreamStartEvent(null, null));
      this.closed = Boolean.FALSE;
    } else {
      if (Boolean.TRUE.equals(this.closed))
        throw new SerializerException("serializer is closed"); 
      throw new SerializerException("serializer is already opened");
    } 
  }
  
  public void close() throws IOException {
    if (this.closed == null)
      throw new SerializerException("serializer is not opened"); 
    if (!Boolean.TRUE.equals(this.closed)) {
      this.emitter.emit((Event)new StreamEndEvent(null, null));
      this.closed = Boolean.TRUE;
    } 
  }
  
  public void serialize(Node paramNode) throws IOException {
    if (this.closed == null)
      throw new SerializerException("serializer is not opened"); 
    if (this.closed.booleanValue())
      throw new SerializerException("serializer is closed"); 
    this.emitter.emit((Event)new DocumentStartEvent(null, null, this.explicitStart, this.useVersion, this.useTags));
    anchorNode(paramNode);
    if (this.explicitRoot != null)
      paramNode.setTag(this.explicitRoot); 
    serializeNode(paramNode, null);
    this.emitter.emit((Event)new DocumentEndEvent(null, null, this.explicitEnd));
    this.serializedNodes.clear();
    this.anchors.clear();
  }
  
  private void anchorNode(Node paramNode) {
    if (paramNode.getNodeId() == NodeId.anchor)
      paramNode = ((AnchorNode)paramNode).getRealNode(); 
    if (this.anchors.containsKey(paramNode)) {
      String str = this.anchors.get(paramNode);
      if (null == str) {
        str = this.anchorGenerator.nextAnchor(paramNode);
        this.anchors.put(paramNode, str);
      } 
    } else {
      SequenceNode sequenceNode;
      List list1;
      MappingNode mappingNode;
      List list2;
      this.anchors.put(paramNode, null);
      switch (paramNode.getNodeId()) {
        case sequence:
          sequenceNode = (SequenceNode)paramNode;
          list1 = sequenceNode.getValue();
          for (Node node : list1)
            anchorNode(node); 
          break;
        case mapping:
          mappingNode = (MappingNode)paramNode;
          list2 = mappingNode.getValue();
          for (NodeTuple nodeTuple : list2) {
            Node node1 = nodeTuple.getKeyNode();
            Node node2 = nodeTuple.getValueNode();
            anchorNode(node1);
            anchorNode(node2);
          } 
          break;
      } 
    } 
  }
  
  private void serializeNode(Node paramNode1, Node paramNode2) throws IOException {
    if (paramNode1.getNodeId() == NodeId.anchor)
      paramNode1 = ((AnchorNode)paramNode1).getRealNode(); 
    String str = this.anchors.get(paramNode1);
    if (this.serializedNodes.contains(paramNode1)) {
      this.emitter.emit((Event)new AliasEvent(str, null, null));
    } else {
      ScalarNode scalarNode;
      Tag tag1, tag2;
      ImplicitTuple implicitTuple;
      ScalarEvent scalarEvent;
      SequenceNode sequenceNode;
      boolean bool1;
      List list1;
      this.serializedNodes.add(paramNode1);
      switch (paramNode1.getNodeId()) {
        case scalar:
          scalarNode = (ScalarNode)paramNode1;
          tag1 = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(), true);
          tag2 = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(), false);
          implicitTuple = new ImplicitTuple(paramNode1.getTag().equals(tag1), paramNode1.getTag().equals(tag2));
          scalarEvent = new ScalarEvent(str, paramNode1.getTag().getValue(), implicitTuple, scalarNode.getValue(), null, null, scalarNode.getStyle());
          this.emitter.emit((Event)scalarEvent);
          return;
        case sequence:
          sequenceNode = (SequenceNode)paramNode1;
          bool1 = paramNode1.getTag().equals(this.resolver.resolve(NodeId.sequence, null, true));
          this.emitter.emit((Event)new SequenceStartEvent(str, paramNode1.getTag().getValue(), bool1, null, null, sequenceNode
                .getFlowStyle()));
          list1 = sequenceNode.getValue();
          for (Node node : list1)
            serializeNode(node, paramNode1); 
          this.emitter.emit((Event)new SequenceEndEvent(null, null));
          return;
      } 
      Tag tag3 = this.resolver.resolve(NodeId.mapping, null, true);
      boolean bool2 = paramNode1.getTag().equals(tag3);
      this.emitter.emit((Event)new MappingStartEvent(str, paramNode1.getTag().getValue(), bool2, null, null, ((CollectionNode)paramNode1)
            .getFlowStyle()));
      MappingNode mappingNode = (MappingNode)paramNode1;
      List list2 = mappingNode.getValue();
      for (NodeTuple nodeTuple : list2) {
        Node node1 = nodeTuple.getKeyNode();
        Node node2 = nodeTuple.getValueNode();
        serializeNode(node1, (Node)mappingNode);
        serializeNode(node2, (Node)mappingNode);
      } 
      this.emitter.emit((Event)new MappingEndEvent(null, null));
    } 
  }
}
