package org.yaml.snakeyaml.nodes;

public class AnchorNode extends Node {
  private Node realNode;
  
  public AnchorNode(Node paramNode) {
    super(paramNode.getTag(), paramNode.getStartMark(), paramNode.getEndMark());
    this.realNode = paramNode;
  }
  
  public NodeId getNodeId() {
    return NodeId.anchor;
  }
  
  public Node getRealNode() {
    return this.realNode;
  }
}
