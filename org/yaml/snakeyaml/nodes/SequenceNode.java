package org.yaml.snakeyaml.nodes;

import java.util.List;
import org.yaml.snakeyaml.error.Mark;

public class SequenceNode extends CollectionNode<Node> {
  private final List<Node> value;
  
  public SequenceNode(Tag paramTag, boolean paramBoolean, List<Node> paramList, Mark paramMark1, Mark paramMark2, Boolean paramBoolean1) {
    super(paramTag, paramMark1, paramMark2, paramBoolean1);
    if (paramList == null)
      throw new NullPointerException("value in a Node is required."); 
    this.value = paramList;
    this.resolved = paramBoolean;
  }
  
  public SequenceNode(Tag paramTag, List<Node> paramList, Boolean paramBoolean) {
    this(paramTag, true, paramList, (Mark)null, (Mark)null, paramBoolean);
  }
  
  public NodeId getNodeId() {
    return NodeId.sequence;
  }
  
  public List<Node> getValue() {
    return this.value;
  }
  
  public void setListType(Class<? extends Object> paramClass) {
    for (Node node : this.value)
      node.setType(paramClass); 
  }
  
  public String toString() {
    return "<" + getClass().getName() + " (tag=" + getTag() + ", value=" + getValue() + ")>";
  }
}
