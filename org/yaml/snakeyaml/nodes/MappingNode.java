package org.yaml.snakeyaml.nodes;

import java.util.List;
import org.yaml.snakeyaml.error.Mark;

public class MappingNode extends CollectionNode<NodeTuple> {
  private List<NodeTuple> value;
  
  private boolean merged = false;
  
  public MappingNode(Tag paramTag, boolean paramBoolean, List<NodeTuple> paramList, Mark paramMark1, Mark paramMark2, Boolean paramBoolean1) {
    super(paramTag, paramMark1, paramMark2, paramBoolean1);
    if (paramList == null)
      throw new NullPointerException("value in a Node is required."); 
    this.value = paramList;
    this.resolved = paramBoolean;
  }
  
  public MappingNode(Tag paramTag, List<NodeTuple> paramList, Boolean paramBoolean) {
    this(paramTag, true, paramList, (Mark)null, (Mark)null, paramBoolean);
  }
  
  public NodeId getNodeId() {
    return NodeId.mapping;
  }
  
  public List<NodeTuple> getValue() {
    return this.value;
  }
  
  public void setValue(List<NodeTuple> paramList) {
    this.value = paramList;
  }
  
  public void setOnlyKeyType(Class<? extends Object> paramClass) {
    for (NodeTuple nodeTuple : this.value)
      nodeTuple.getKeyNode().setType(paramClass); 
  }
  
  public void setTypes(Class<? extends Object> paramClass1, Class<? extends Object> paramClass2) {
    for (NodeTuple nodeTuple : this.value) {
      nodeTuple.getValueNode().setType(paramClass2);
      nodeTuple.getKeyNode().setType(paramClass1);
    } 
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (NodeTuple nodeTuple : getValue()) {
      stringBuilder.append("{ key=");
      stringBuilder.append(nodeTuple.getKeyNode());
      stringBuilder.append("; value=");
      if (nodeTuple.getValueNode() instanceof CollectionNode) {
        stringBuilder.append(System.identityHashCode(nodeTuple.getValueNode()));
      } else {
        stringBuilder.append(nodeTuple.toString());
      } 
      stringBuilder.append(" }");
    } 
    String str = stringBuilder.toString();
    return "<" + getClass().getName() + " (tag=" + getTag() + ", values=" + str + ")>";
  }
  
  public void setMerged(boolean paramBoolean) {
    this.merged = paramBoolean;
  }
  
  public boolean isMerged() {
    return this.merged;
  }
}
