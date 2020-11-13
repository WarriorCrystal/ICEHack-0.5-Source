package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;

public abstract class AbstractConstruct implements Construct {
  public void construct2ndStep(Node paramNode, Object paramObject) {
    if (paramNode.isTwoStepsConstruction())
      throw new IllegalStateException("Not Implemented in " + getClass().getName()); 
    throw new YAMLException("Unexpected recursive structure for Node: " + paramNode);
  }
}
