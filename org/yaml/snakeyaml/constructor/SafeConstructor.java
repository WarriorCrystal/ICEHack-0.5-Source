package org.yaml.snakeyaml.constructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class SafeConstructor extends BaseConstructor {
  public static final ConstructUndefined undefinedConstructor = new ConstructUndefined();
  
  public SafeConstructor() {
    this.yamlConstructors.put(Tag.NULL, new ConstructYamlNull());
    this.yamlConstructors.put(Tag.BOOL, new ConstructYamlBool());
    this.yamlConstructors.put(Tag.INT, new ConstructYamlInt());
    this.yamlConstructors.put(Tag.FLOAT, new ConstructYamlFloat());
    this.yamlConstructors.put(Tag.BINARY, new ConstructYamlBinary());
    this.yamlConstructors.put(Tag.TIMESTAMP, new ConstructYamlTimestamp());
    this.yamlConstructors.put(Tag.OMAP, new ConstructYamlOmap());
    this.yamlConstructors.put(Tag.PAIRS, new ConstructYamlPairs());
    this.yamlConstructors.put(Tag.SET, new ConstructYamlSet());
    this.yamlConstructors.put(Tag.STR, new ConstructYamlStr());
    this.yamlConstructors.put(Tag.SEQ, new ConstructYamlSeq());
    this.yamlConstructors.put(Tag.MAP, new ConstructYamlMap());
    this.yamlConstructors.put(null, undefinedConstructor);
    this.yamlClassConstructors.put(NodeId.scalar, undefinedConstructor);
    this.yamlClassConstructors.put(NodeId.sequence, undefinedConstructor);
    this.yamlClassConstructors.put(NodeId.mapping, undefinedConstructor);
  }
  
  protected void flattenMapping(MappingNode paramMappingNode) {
    processDuplicateKeys(paramMappingNode);
    if (paramMappingNode.isMerged())
      paramMappingNode.setValue(mergeNode(paramMappingNode, true, new HashMap<Object, Integer>(), new ArrayList<NodeTuple>())); 
  }
  
  protected void processDuplicateKeys(MappingNode paramMappingNode) {
    List list = paramMappingNode.getValue();
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>(list.size());
    TreeSet<Integer> treeSet = new TreeSet();
    int i = 0;
    for (NodeTuple nodeTuple : list) {
      Node node = nodeTuple.getKeyNode();
      if (!node.getTag().equals(Tag.MERGE)) {
        Object object = constructObject(node);
        if (object != null)
          try {
            object.hashCode();
          } catch (Exception exception) {
            throw new ConstructorException("while constructing a mapping", paramMappingNode
                .getStartMark(), "found unacceptable key " + object, nodeTuple
                .getKeyNode().getStartMark(), exception);
          }  
        Integer integer = (Integer)hashMap.put(object, Integer.valueOf(i));
        if (integer != null) {
          if (!isAllowDuplicateKeys())
            throw new IllegalStateException("duplicate key: " + object); 
          treeSet.add(integer);
        } 
      } 
      i++;
    } 
    Iterator<Integer> iterator = treeSet.descendingIterator();
    while (iterator.hasNext())
      list.remove(((Integer)iterator.next()).intValue()); 
  }
  
  private List<NodeTuple> mergeNode(MappingNode paramMappingNode, boolean paramBoolean, Map<Object, Integer> paramMap, List<NodeTuple> paramList) {
    Iterator<NodeTuple> iterator = paramMappingNode.getValue().iterator();
    while (iterator.hasNext()) {
      NodeTuple nodeTuple = iterator.next();
      Node node1 = nodeTuple.getKeyNode();
      Node node2 = nodeTuple.getValueNode();
      if (node1.getTag().equals(Tag.MERGE)) {
        MappingNode mappingNode;
        SequenceNode sequenceNode;
        List list;
        iterator.remove();
        switch (node2.getNodeId()) {
          case mapping:
            mappingNode = (MappingNode)node2;
            mergeNode(mappingNode, false, paramMap, paramList);
            continue;
          case sequence:
            sequenceNode = (SequenceNode)node2;
            list = sequenceNode.getValue();
            for (Node node : list) {
              if (!(node instanceof MappingNode))
                throw new ConstructorException("while constructing a mapping", paramMappingNode
                    .getStartMark(), "expected a mapping for merging, but found " + node
                    
                    .getNodeId(), node
                    .getStartMark()); 
              MappingNode mappingNode1 = (MappingNode)node;
              mergeNode(mappingNode1, false, paramMap, paramList);
            } 
            continue;
        } 
        throw new ConstructorException("while constructing a mapping", paramMappingNode
            .getStartMark(), "expected a mapping or list of mappings for merging, but found " + node2
            
            .getNodeId(), node2
            .getStartMark());
      } 
      Object object = constructObject(node1);
      if (!paramMap.containsKey(object)) {
        paramList.add(nodeTuple);
        paramMap.put(object, Integer.valueOf(paramList.size() - 1));
        continue;
      } 
      if (paramBoolean)
        paramList.set(((Integer)paramMap.get(object)).intValue(), nodeTuple); 
    } 
    return paramList;
  }
  
  protected void constructMapping2ndStep(MappingNode paramMappingNode, Map<Object, Object> paramMap) {
    flattenMapping(paramMappingNode);
    super.constructMapping2ndStep(paramMappingNode, paramMap);
  }
  
  protected void constructSet2ndStep(MappingNode paramMappingNode, Set<Object> paramSet) {
    flattenMapping(paramMappingNode);
    super.constructSet2ndStep(paramMappingNode, paramSet);
  }
  
  public class ConstructYamlNull extends AbstractConstruct {
    public Object construct(Node param1Node) {
      SafeConstructor.this.constructScalar((ScalarNode)param1Node);
      return null;
    }
  }
  
  private static final Map<String, Boolean> BOOL_VALUES = new HashMap<String, Boolean>();
  
  static {
    BOOL_VALUES.put("yes", Boolean.TRUE);
    BOOL_VALUES.put("no", Boolean.FALSE);
    BOOL_VALUES.put("true", Boolean.TRUE);
    BOOL_VALUES.put("false", Boolean.FALSE);
    BOOL_VALUES.put("on", Boolean.TRUE);
    BOOL_VALUES.put("off", Boolean.FALSE);
  }
  
  public class ConstructYamlBool extends AbstractConstruct {
    public Object construct(Node param1Node) {
      String str = (String)SafeConstructor.this.constructScalar((ScalarNode)param1Node);
      return SafeConstructor.BOOL_VALUES.get(str.toLowerCase());
    }
  }
  
  public class ConstructYamlInt extends AbstractConstruct {
    public Object construct(Node param1Node) {
      String str = SafeConstructor.this.constructScalar((ScalarNode)param1Node).toString().replaceAll("_", "");
      byte b = 1;
      char c = str.charAt(0);
      if (c == '-') {
        b = -1;
        str = str.substring(1);
      } else if (c == '+') {
        str = str.substring(1);
      } 
      byte b1 = 10;
      if ("0".equals(str))
        return Integer.valueOf(0); 
      if (str.startsWith("0b")) {
        str = str.substring(2);
        b1 = 2;
      } else if (str.startsWith("0x")) {
        str = str.substring(2);
        b1 = 16;
      } else if (str.startsWith("0")) {
        str = str.substring(1);
        b1 = 8;
      } else {
        if (str.indexOf(':') != -1) {
          String[] arrayOfString = str.split(":");
          int i = 1;
          int j = 0;
          byte b2;
          int k;
          for (b2 = 0, k = arrayOfString.length; b2 < k; b2++) {
            j = (int)(j + Long.parseLong(arrayOfString[k - b2 - 1]) * i);
            i *= 60;
          } 
          return SafeConstructor.this.createNumber(b, String.valueOf(j), 10);
        } 
        return SafeConstructor.this.createNumber(b, str, 10);
      } 
      return SafeConstructor.this.createNumber(b, str, b1);
    }
  }
  
  private Number createNumber(int paramInt1, String paramString, int paramInt2) {
    BigInteger bigInteger;
    if (paramInt1 < 0)
      paramString = "-" + paramString; 
    try {
      Integer integer = Integer.valueOf(paramString, paramInt2);
    } catch (NumberFormatException numberFormatException) {
      try {
        Long long_ = Long.valueOf(paramString, paramInt2);
      } catch (NumberFormatException numberFormatException1) {
        bigInteger = new BigInteger(paramString, paramInt2);
      } 
    } 
    return bigInteger;
  }
  
  public class ConstructYamlFloat extends AbstractConstruct {
    public Object construct(Node param1Node) {
      String str1 = SafeConstructor.this.constructScalar((ScalarNode)param1Node).toString().replaceAll("_", "");
      byte b = 1;
      char c = str1.charAt(0);
      if (c == '-') {
        b = -1;
        str1 = str1.substring(1);
      } else if (c == '+') {
        str1 = str1.substring(1);
      } 
      String str2 = str1.toLowerCase();
      if (".inf".equals(str2))
        return new Double((b == -1) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY); 
      if (".nan".equals(str2))
        return new Double(Double.NaN); 
      if (str1.indexOf(':') != -1) {
        String[] arrayOfString = str1.split(":");
        int i = 1;
        double d = 0.0D;
        byte b1;
        int j;
        for (b1 = 0, j = arrayOfString.length; b1 < j; b1++) {
          d += Double.parseDouble(arrayOfString[j - b1 - 1]) * i;
          i *= 60;
        } 
        return new Double(b * d);
      } 
      Double double_ = Double.valueOf(str1);
      return new Double(double_.doubleValue() * b);
    }
  }
  
  public class ConstructYamlBinary extends AbstractConstruct {
    public Object construct(Node param1Node) {
      String str = SafeConstructor.this.constructScalar((ScalarNode)param1Node).toString().replaceAll("\\s", "");
      return Base64Coder.decode(str.toCharArray());
    }
  }
  
  private static final Pattern TIMESTAMP_REGEXP = Pattern.compile("^([0-9][0-9][0-9][0-9])-([0-9][0-9]?)-([0-9][0-9]?)(?:(?:[Tt]|[ \t]+)([0-9][0-9]?):([0-9][0-9]):([0-9][0-9])(?:\\.([0-9]*))?(?:[ \t]*(?:Z|([-+][0-9][0-9]?)(?::([0-9][0-9])?)?))?)?$");
  
  private static final Pattern YMD_REGEXP = Pattern.compile("^([0-9][0-9][0-9][0-9])-([0-9][0-9]?)-([0-9][0-9]?)$");
  
  public static class ConstructYamlTimestamp extends AbstractConstruct {
    private Calendar calendar;
    
    public Calendar getCalendar() {
      return this.calendar;
    }
    
    public Object construct(Node param1Node) {
      TimeZone timeZone;
      ScalarNode scalarNode = (ScalarNode)param1Node;
      String str1 = scalarNode.getValue();
      Matcher matcher = SafeConstructor.YMD_REGEXP.matcher(str1);
      if (matcher.matches()) {
        String str11 = matcher.group(1);
        String str12 = matcher.group(2);
        String str13 = matcher.group(3);
        this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        this.calendar.clear();
        this.calendar.set(1, Integer.parseInt(str11));
        this.calendar.set(2, Integer.parseInt(str12) - 1);
        this.calendar.set(5, Integer.parseInt(str13));
        return this.calendar.getTime();
      } 
      matcher = SafeConstructor.TIMESTAMP_REGEXP.matcher(str1);
      if (!matcher.matches())
        throw new YAMLException("Unexpected timestamp: " + str1); 
      String str2 = matcher.group(1);
      String str3 = matcher.group(2);
      String str4 = matcher.group(3);
      String str5 = matcher.group(4);
      String str6 = matcher.group(5);
      String str7 = matcher.group(6);
      String str8 = matcher.group(7);
      if (str8 != null)
        str7 = str7 + "." + str8; 
      double d = Double.parseDouble(str7);
      int i = (int)Math.round(Math.floor(d));
      int j = (int)Math.round((d - i) * 1000.0D);
      String str9 = matcher.group(8);
      String str10 = matcher.group(9);
      if (str9 != null) {
        String str = (str10 != null) ? (":" + str10) : "00";
        timeZone = TimeZone.getTimeZone("GMT" + str9 + str);
      } else {
        timeZone = TimeZone.getTimeZone("UTC");
      } 
      this.calendar = Calendar.getInstance(timeZone);
      this.calendar.set(1, Integer.parseInt(str2));
      this.calendar.set(2, Integer.parseInt(str3) - 1);
      this.calendar.set(5, Integer.parseInt(str4));
      this.calendar.set(11, Integer.parseInt(str5));
      this.calendar.set(12, Integer.parseInt(str6));
      this.calendar.set(13, i);
      this.calendar.set(14, j);
      return this.calendar.getTime();
    }
  }
  
  public class ConstructYamlOmap extends AbstractConstruct {
    public Object construct(Node param1Node) {
      LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<Object, Object>();
      if (!(param1Node instanceof SequenceNode))
        throw new ConstructorException("while constructing an ordered map", param1Node
            .getStartMark(), "expected a sequence, but found " + param1Node.getNodeId(), param1Node
            .getStartMark()); 
      SequenceNode sequenceNode = (SequenceNode)param1Node;
      for (Node node1 : sequenceNode.getValue()) {
        if (!(node1 instanceof MappingNode))
          throw new ConstructorException("while constructing an ordered map", param1Node
              .getStartMark(), "expected a mapping of length 1, but found " + node1
              .getNodeId(), node1
              .getStartMark()); 
        MappingNode mappingNode = (MappingNode)node1;
        if (mappingNode.getValue().size() != 1)
          throw new ConstructorException("while constructing an ordered map", param1Node
              .getStartMark(), "expected a single mapping item, but found " + mappingNode
              .getValue().size() + " items", mappingNode
              .getStartMark()); 
        Node node2 = ((NodeTuple)mappingNode.getValue().get(0)).getKeyNode();
        Node node3 = ((NodeTuple)mappingNode.getValue().get(0)).getValueNode();
        Object object1 = SafeConstructor.this.constructObject(node2);
        Object object2 = SafeConstructor.this.constructObject(node3);
        linkedHashMap.put(object1, object2);
      } 
      return linkedHashMap;
    }
  }
  
  public class ConstructYamlPairs extends AbstractConstruct {
    public Object construct(Node param1Node) {
      if (!(param1Node instanceof SequenceNode))
        throw new ConstructorException("while constructing pairs", param1Node.getStartMark(), "expected a sequence, but found " + param1Node
            .getNodeId(), param1Node.getStartMark()); 
      SequenceNode sequenceNode = (SequenceNode)param1Node;
      ArrayList<Object[]> arrayList = new ArrayList(sequenceNode.getValue().size());
      for (Node node1 : sequenceNode.getValue()) {
        if (!(node1 instanceof MappingNode))
          throw new ConstructorException("while constructingpairs", param1Node.getStartMark(), "expected a mapping of length 1, but found " + node1
              .getNodeId(), node1
              .getStartMark()); 
        MappingNode mappingNode = (MappingNode)node1;
        if (mappingNode.getValue().size() != 1)
          throw new ConstructorException("while constructing pairs", param1Node.getStartMark(), "expected a single mapping item, but found " + mappingNode
              .getValue().size() + " items", mappingNode
              
              .getStartMark()); 
        Node node2 = ((NodeTuple)mappingNode.getValue().get(0)).getKeyNode();
        Node node3 = ((NodeTuple)mappingNode.getValue().get(0)).getValueNode();
        Object object1 = SafeConstructor.this.constructObject(node2);
        Object object2 = SafeConstructor.this.constructObject(node3);
        arrayList.add(new Object[] { object1, object2 });
      } 
      return arrayList;
    }
  }
  
  public class ConstructYamlSet implements Construct {
    public Object construct(Node param1Node) {
      if (param1Node.isTwoStepsConstruction())
        return SafeConstructor.this.constructedObjects.containsKey(param1Node) ? SafeConstructor.this.constructedObjects.get(param1Node) : SafeConstructor.this
          .createDefaultSet(); 
      return SafeConstructor.this.constructSet((MappingNode)param1Node);
    }
    
    public void construct2ndStep(Node param1Node, Object param1Object) {
      if (param1Node.isTwoStepsConstruction()) {
        SafeConstructor.this.constructSet2ndStep((MappingNode)param1Node, (Set<Object>)param1Object);
      } else {
        throw new YAMLException("Unexpected recursive set structure. Node: " + param1Node);
      } 
    }
  }
  
  public class ConstructYamlStr extends AbstractConstruct {
    public Object construct(Node param1Node) {
      return SafeConstructor.this.constructScalar((ScalarNode)param1Node);
    }
  }
  
  public class ConstructYamlSeq implements Construct {
    public Object construct(Node param1Node) {
      SequenceNode sequenceNode = (SequenceNode)param1Node;
      if (param1Node.isTwoStepsConstruction())
        return SafeConstructor.this.newList(sequenceNode); 
      return SafeConstructor.this.constructSequence(sequenceNode);
    }
    
    public void construct2ndStep(Node param1Node, Object param1Object) {
      if (param1Node.isTwoStepsConstruction()) {
        SafeConstructor.this.constructSequenceStep2((SequenceNode)param1Node, (List)param1Object);
      } else {
        throw new YAMLException("Unexpected recursive sequence structure. Node: " + param1Node);
      } 
    }
  }
  
  public class ConstructYamlMap implements Construct {
    public Object construct(Node param1Node) {
      if (param1Node.isTwoStepsConstruction())
        return SafeConstructor.this.createDefaultMap(); 
      return SafeConstructor.this.constructMapping((MappingNode)param1Node);
    }
    
    public void construct2ndStep(Node param1Node, Object param1Object) {
      if (param1Node.isTwoStepsConstruction()) {
        SafeConstructor.this.constructMapping2ndStep((MappingNode)param1Node, (Map<Object, Object>)param1Object);
      } else {
        throw new YAMLException("Unexpected recursive mapping structure. Node: " + param1Node);
      } 
    }
  }
  
  public static final class ConstructUndefined extends AbstractConstruct {
    public Object construct(Node param1Node) {
      throw new ConstructorException(null, null, "could not determine a constructor for the tag " + param1Node
          .getTag(), param1Node
          .getStartMark());
    }
  }
}
