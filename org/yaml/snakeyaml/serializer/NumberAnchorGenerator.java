package org.yaml.snakeyaml.serializer;

import java.text.NumberFormat;
import org.yaml.snakeyaml.nodes.Node;

public class NumberAnchorGenerator implements AnchorGenerator {
  private int lastAnchorId = 0;
  
  public NumberAnchorGenerator(int paramInt) {
    this.lastAnchorId = paramInt;
  }
  
  public String nextAnchor(Node paramNode) {
    this.lastAnchorId++;
    NumberFormat numberFormat = NumberFormat.getNumberInstance();
    numberFormat.setMinimumIntegerDigits(3);
    numberFormat.setMaximumFractionDigits(0);
    numberFormat.setGroupingUsed(false);
    String str = numberFormat.format(this.lastAnchorId);
    return "id" + str;
  }
}
