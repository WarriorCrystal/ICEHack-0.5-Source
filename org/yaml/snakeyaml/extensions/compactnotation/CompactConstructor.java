package org.yaml.snakeyaml.extensions.compactnotation;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class CompactConstructor extends Constructor {
  private static final Pattern GUESS_COMPACT = Pattern.compile("\\p{Alpha}.*\\s*\\((?:,?\\s*(?:(?:\\w*)|(?:\\p{Alpha}\\w*\\s*=.+))\\s*)+\\)");
  
  private static final Pattern FIRST_PATTERN = Pattern.compile("(\\p{Alpha}.*)(\\s*)\\((.*?)\\)");
  
  private static final Pattern PROPERTY_NAME_PATTERN = Pattern.compile("\\s*(\\p{Alpha}\\w*)\\s*=(.+)");
  
  private Construct compactConstruct;
  
  protected Object constructCompactFormat(ScalarNode paramScalarNode, CompactData paramCompactData) {
    try {
      Object object = createInstance(paramScalarNode, paramCompactData);
      HashMap<String, String> hashMap = new HashMap<String, String>(paramCompactData.getProperties());
      setProperties(object, (Map)hashMap);
      return object;
    } catch (Exception exception) {
      throw new YAMLException(exception);
    } 
  }
  
  protected Object createInstance(ScalarNode paramScalarNode, CompactData paramCompactData) throws Exception {
    Class clazz = getClassForName(paramCompactData.getPrefix());
    Class[] arrayOfClass = new Class[paramCompactData.getArguments().size()];
    for (byte b = 0; b < arrayOfClass.length; b++)
      arrayOfClass[b] = String.class; 
    Constructor constructor = clazz.getDeclaredConstructor(arrayOfClass);
    constructor.setAccessible(true);
    return constructor.newInstance(paramCompactData.getArguments().toArray());
  }
  
  protected void setProperties(Object paramObject, Map<String, Object> paramMap) throws Exception {
    if (paramMap == null)
      throw new NullPointerException("Data for Compact Object Notation cannot be null."); 
    for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
      String str = (String)entry.getKey();
      Property property = getPropertyUtils().getProperty(paramObject.getClass(), str);
      try {
        property.set(paramObject, entry.getValue());
      } catch (IllegalArgumentException illegalArgumentException) {
        throw new YAMLException("Cannot set property='" + str + "' with value='" + paramMap
            .get(str) + "' (" + paramMap.get(str).getClass() + ") in " + paramObject);
      } 
    } 
  }
  
  public CompactData getCompactData(String paramString) {
    if (!paramString.endsWith(")"))
      return null; 
    if (paramString.indexOf('(') < 0)
      return null; 
    Matcher matcher = FIRST_PATTERN.matcher(paramString);
    if (matcher.matches()) {
      String str1 = matcher.group(1).trim();
      String str2 = matcher.group(3);
      CompactData compactData = new CompactData(str1);
      if (str2.length() == 0)
        return compactData; 
      String[] arrayOfString = str2.split("\\s*,\\s*");
      for (byte b = 0; b < arrayOfString.length; b++) {
        String str = arrayOfString[b];
        if (str.indexOf('=') < 0) {
          compactData.getArguments().add(str);
        } else {
          Matcher matcher1 = PROPERTY_NAME_PATTERN.matcher(str);
          if (matcher1.matches()) {
            String str3 = matcher1.group(1);
            String str4 = matcher1.group(2).trim();
            compactData.getProperties().put(str3, str4);
          } else {
            return null;
          } 
        } 
      } 
      return compactData;
    } 
    return null;
  }
  
  private Construct getCompactConstruct() {
    if (this.compactConstruct == null)
      this.compactConstruct = createCompactConstruct(); 
    return this.compactConstruct;
  }
  
  protected Construct createCompactConstruct() {
    return (Construct)new ConstructCompactObject();
  }
  
  protected Construct getConstructor(Node paramNode) {
    if (paramNode instanceof MappingNode) {
      MappingNode mappingNode = (MappingNode)paramNode;
      List<NodeTuple> list = mappingNode.getValue();
      if (list.size() == 1) {
        NodeTuple nodeTuple = list.get(0);
        Node node = nodeTuple.getKeyNode();
        if (node instanceof ScalarNode) {
          ScalarNode scalarNode = (ScalarNode)node;
          if (GUESS_COMPACT.matcher(scalarNode.getValue()).matches())
            return getCompactConstruct(); 
        } 
      } 
    } else if (paramNode instanceof ScalarNode) {
      ScalarNode scalarNode = (ScalarNode)paramNode;
      if (GUESS_COMPACT.matcher(scalarNode.getValue()).matches())
        return getCompactConstruct(); 
    } 
    return super.getConstructor(paramNode);
  }
  
  public class ConstructCompactObject extends Constructor.ConstructMapping {
    public ConstructCompactObject() {
      super(CompactConstructor.this);
    }
    
    public void construct2ndStep(Node param1Node, Object param1Object) {
      MappingNode mappingNode = (MappingNode)param1Node;
      NodeTuple nodeTuple = mappingNode.getValue().iterator().next();
      Node node = nodeTuple.getValueNode();
      if (node instanceof MappingNode) {
        node.setType(param1Object.getClass());
        constructJavaBean2ndStep((MappingNode)node, param1Object);
      } else {
        CompactConstructor.this.applySequence(param1Object, CompactConstructor.this.constructSequence((SequenceNode)node));
      } 
    }
    
    public Object construct(Node param1Node) {
      ScalarNode scalarNode = null;
      if (param1Node instanceof MappingNode) {
        MappingNode mappingNode = (MappingNode)param1Node;
        NodeTuple nodeTuple = mappingNode.getValue().iterator().next();
        param1Node.setTwoStepsConstruction(true);
        scalarNode = (ScalarNode)nodeTuple.getKeyNode();
      } else {
        scalarNode = (ScalarNode)param1Node;
      } 
      CompactData compactData = CompactConstructor.this.getCompactData(scalarNode.getValue());
      if (compactData == null)
        return CompactConstructor.this.constructScalar(scalarNode); 
      return CompactConstructor.this.constructCompactFormat(scalarNode, compactData);
    }
  }
  
  protected void applySequence(Object paramObject, List<?> paramList) {
    try {
      Property property = getPropertyUtils().getProperty(paramObject.getClass(), 
          getSequencePropertyName(paramObject.getClass()));
      property.set(paramObject, paramList);
    } catch (Exception exception) {
      throw new YAMLException(exception);
    } 
  }
  
  protected String getSequencePropertyName(Class<?> paramClass) {
    Set<Property> set = getPropertyUtils().getProperties(paramClass);
    for (Iterator<Property> iterator = set.iterator(); iterator.hasNext(); ) {
      Property property = iterator.next();
      if (!List.class.isAssignableFrom(property.getType()))
        iterator.remove(); 
    } 
    if (set.size() == 0)
      throw new YAMLException("No list property found in " + paramClass); 
    if (set.size() > 1)
      throw new YAMLException("Many list properties found in " + paramClass + "; Please override getSequencePropertyName() to specify which property to use."); 
    return ((Property)set.iterator().next()).getName();
  }
}
