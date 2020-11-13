package org.yaml.snakeyaml.nodes;

public final class NodeTuple {
  private Node keyNode;
  
  private Node valueNode;
  
  public NodeTuple(Node paramNode1, Node paramNode2) {
    if (paramNode1 == null || paramNode2 == null)
      throw new NullPointerException("Nodes must be provided."); 
    this.keyNode = paramNode1;
    this.valueNode = paramNode2;
  }
  
  public Node getKeyNode() {
    return this.keyNode;
  }
  
  public Node getValueNode() {
    return this.valueNode;
  }
  
  public String toString() {
    return "<NodeTuple keyNode=" + this.keyNode.toString() + "; valueNode=" + this.valueNode.toString() + ">";
  }
}
