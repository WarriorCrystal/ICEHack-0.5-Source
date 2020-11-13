package org.yaml.snakeyaml.constructor;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class Constructor extends SafeConstructor {
  public Constructor() {
    this(Object.class);
  }
  
  public Constructor(Class<? extends Object> paramClass) {
    this(new TypeDescription(checkRoot(paramClass)));
  }
  
  private static Class<? extends Object> checkRoot(Class<? extends Object> paramClass) {
    if (paramClass == null)
      throw new NullPointerException("Root class must be provided."); 
    return paramClass;
  }
  
  public Constructor(TypeDescription paramTypeDescription) {
    this(paramTypeDescription, (Collection<TypeDescription>)null);
  }
  
  public Constructor(TypeDescription paramTypeDescription, Collection<TypeDescription> paramCollection) {
    if (paramTypeDescription == null)
      throw new NullPointerException("Root type must be provided."); 
    this.yamlConstructors.put(null, new ConstructYamlObject());
    if (!Object.class.equals(paramTypeDescription.getType()))
      this.rootTag = new Tag(paramTypeDescription.getType()); 
    this.yamlClassConstructors.put(NodeId.scalar, new ConstructScalar());
    this.yamlClassConstructors.put(NodeId.mapping, new ConstructMapping());
    this.yamlClassConstructors.put(NodeId.sequence, new ConstructSequence());
    addTypeDescription(paramTypeDescription);
    if (paramCollection != null)
      for (TypeDescription typeDescription : paramCollection)
        addTypeDescription(typeDescription);  
  }
  
  public Constructor(String paramString) throws ClassNotFoundException {
    this((Class)Class.forName(check(paramString)));
  }
  
  private static final String check(String paramString) {
    if (paramString == null)
      throw new NullPointerException("Root type must be provided."); 
    if (paramString.trim().length() == 0)
      throw new YAMLException("Root type must be provided."); 
    return paramString;
  }
  
  protected class ConstructMapping implements Construct {
    public Object construct(Node param1Node) {
      MappingNode mappingNode = (MappingNode)param1Node;
      if (Map.class.isAssignableFrom(param1Node.getType())) {
        if (param1Node.isTwoStepsConstruction())
          return Constructor.this.newMap(mappingNode); 
        return Constructor.this.constructMapping(mappingNode);
      } 
      if (Collection.class.isAssignableFrom(param1Node.getType())) {
        if (param1Node.isTwoStepsConstruction())
          return Constructor.this.newSet((CollectionNode<?>)mappingNode); 
        return Constructor.this.constructSet(mappingNode);
      } 
      Object object = Constructor.this.newInstance((Node)mappingNode);
      if (param1Node.isTwoStepsConstruction())
        return object; 
      return constructJavaBean2ndStep(mappingNode, object);
    }
    
    public void construct2ndStep(Node param1Node, Object param1Object) {
      if (Map.class.isAssignableFrom(param1Node.getType())) {
        Constructor.this.constructMapping2ndStep((MappingNode)param1Node, (Map<Object, Object>)param1Object);
      } else if (Set.class.isAssignableFrom(param1Node.getType())) {
        Constructor.this.constructSet2ndStep((MappingNode)param1Node, (Set<Object>)param1Object);
      } else {
        constructJavaBean2ndStep((MappingNode)param1Node, param1Object);
      } 
    }
    
    protected Object constructJavaBean2ndStep(MappingNode param1MappingNode, Object param1Object) {
      Constructor.this.flattenMapping(param1MappingNode);
      Class<? extends Object> clazz = param1MappingNode.getType();
      List list = param1MappingNode.getValue();
      for (NodeTuple nodeTuple : list) {
        ScalarNode scalarNode;
        if (nodeTuple.getKeyNode() instanceof ScalarNode) {
          scalarNode = (ScalarNode)nodeTuple.getKeyNode();
        } else {
          throw new YAMLException("Keys must be scalars but found: " + nodeTuple
              .getKeyNode());
        } 
        Node node = nodeTuple.getValueNode();
        scalarNode.setType(String.class);
        String str = (String)Constructor.this.constructObject((Node)scalarNode);
        try {
          TypeDescription typeDescription = Constructor.this.typeDefinitions.get(clazz);
          Property property = (typeDescription == null) ? getProperty(clazz, str) : typeDescription.getProperty(str);
          if (!property.isWritable())
            throw new YAMLException("No writable property '" + str + "' on class: " + clazz
                .getName()); 
          node.setType(property.getType());
          boolean bool = (typeDescription != null) ? typeDescription.setupPropertyType(str, node) : false;
          if (!bool && node.getNodeId() != NodeId.scalar) {
            Class[] arrayOfClass = property.getActualTypeArguments();
            if (arrayOfClass != null && arrayOfClass.length > 0)
              if (node.getNodeId() == NodeId.sequence) {
                Class clazz1 = arrayOfClass[0];
                SequenceNode sequenceNode = (SequenceNode)node;
                sequenceNode.setListType(clazz1);
              } else if (Set.class.isAssignableFrom(node.getType())) {
                Class clazz1 = arrayOfClass[0];
                MappingNode mappingNode = (MappingNode)node;
                mappingNode.setOnlyKeyType(clazz1);
                mappingNode.setUseClassConstructor(Boolean.valueOf(true));
              } else if (Map.class.isAssignableFrom(node.getType())) {
                Class clazz1 = arrayOfClass[0];
                Class clazz2 = arrayOfClass[1];
                MappingNode mappingNode = (MappingNode)node;
                mappingNode.setTypes(clazz1, clazz2);
                mappingNode.setUseClassConstructor(Boolean.valueOf(true));
              }  
          } 
          Object object = (typeDescription != null) ? newInstance(typeDescription, str, node) : Constructor.this.constructObject(node);
          if ((property.getType() == float.class || property.getType() == Float.class) && 
            object instanceof Double)
            object = Float.valueOf(((Double)object).floatValue()); 
          if (property.getType() == String.class && Tag.BINARY.equals(node.getTag()) && object instanceof byte[])
            object = new String((byte[])object); 
          if (typeDescription == null || 
            !typeDescription.setProperty(param1Object, str, object))
            property.set(param1Object, object); 
        } catch (Exception exception) {
          throw new ConstructorException("Cannot create property=" + str + " for JavaBean=" + param1Object, param1MappingNode
              
              .getStartMark(), exception.getMessage(), node.getStartMark(), exception);
        } 
      } 
      return param1Object;
    }
    
    private Object newInstance(TypeDescription param1TypeDescription, String param1String, Node param1Node) {
      Object object = param1TypeDescription.newInstance(param1String, param1Node);
      if (object != null) {
        Constructor.this.constructedObjects.put(param1Node, object);
        return Constructor.this.constructObjectNoCheck(param1Node);
      } 
      return Constructor.this.constructObject(param1Node);
    }
    
    protected Property getProperty(Class<? extends Object> param1Class, String param1String) {
      return Constructor.this.getPropertyUtils().getProperty(param1Class, param1String);
    }
  }
  
  protected class ConstructYamlObject implements Construct {
    private Construct getConstructor(Node param1Node) {
      Class<?> clazz = Constructor.this.getClassForNode(param1Node);
      param1Node.setType(clazz);
      return Constructor.this.yamlClassConstructors.get(param1Node.getNodeId());
    }
    
    public Object construct(Node param1Node) {
      Object object = null;
      try {
        object = getConstructor(param1Node).construct(param1Node);
      } catch (ConstructorException constructorException) {
        throw constructorException;
      } catch (Exception exception) {
        throw new ConstructorException(null, null, "Can't construct a java object for " + param1Node
            .getTag() + "; exception=" + exception.getMessage(), param1Node.getStartMark(), exception);
      } 
      return object;
    }
    
    public void construct2ndStep(Node param1Node, Object param1Object) {
      try {
        getConstructor(param1Node).construct2ndStep(param1Node, param1Object);
      } catch (Exception exception) {
        throw new ConstructorException(null, null, "Can't construct a second step for a java object for " + param1Node
            
            .getTag() + "; exception=" + exception.getMessage(), param1Node
            .getStartMark(), exception);
      } 
    }
  }
  
  protected class ConstructScalar extends AbstractConstruct {
    public Object construct(Node param1Node) {
      ScalarNode scalarNode = (ScalarNode)param1Node;
      Class<?> clazz = scalarNode.getType();
      try {
        return Constructor.this.newInstance(clazz, (Node)scalarNode, false);
      } catch (InstantiationException instantiationException) {
        if (clazz.isPrimitive() || clazz == String.class || Number.class.isAssignableFrom(clazz) || clazz == Boolean.class || Date.class
          .isAssignableFrom(clazz) || clazz == Character.class || clazz == BigInteger.class || clazz == BigDecimal.class || Enum.class
          
          .isAssignableFrom(clazz) || Tag.BINARY
          .equals(scalarNode.getTag()) || Calendar.class.isAssignableFrom(clazz) || clazz == UUID.class) {
          Object object = constructStandardJavaInstance(clazz, scalarNode);
        } else {
          Object object;
          Constructor[] arrayOfConstructor = (Constructor[])clazz.getDeclaredConstructors();
          byte b = 0;
          Constructor<?> constructor = null;
          for (Constructor constructor1 : arrayOfConstructor) {
            if ((constructor1.getParameterTypes()).length == 1) {
              b++;
              constructor = constructor1;
            } 
          } 
          if (constructor == null)
            try {
              return Constructor.this.newInstance(clazz, (Node)scalarNode, false);
            } catch (InstantiationException instantiationException1) {
              throw new YAMLException("No single argument constructor found for " + clazz + " : " + instantiationException1
                  .getMessage());
            }  
          if (b == 1) {
            object = constructStandardJavaInstance(constructor.getParameterTypes()[0], scalarNode);
          } else {
            object = Constructor.this.constructScalar(scalarNode);
            try {
              constructor = clazz.getDeclaredConstructor(new Class[] { String.class });
            } catch (Exception exception) {
              throw new YAMLException("Can't construct a java object for scalar " + scalarNode
                  .getTag() + "; No String constructor found. Exception=" + exception
                  .getMessage(), exception);
            } 
          } 
          try {
            constructor.setAccessible(true);
            instantiationException = (InstantiationException)constructor.newInstance(new Object[] { object });
          } catch (Exception exception) {
            throw new ConstructorException(null, null, "Can't construct a java object for scalar " + scalarNode
                .getTag() + "; exception=" + exception
                .getMessage(), scalarNode
                .getStartMark(), exception);
          } 
        } 
        return instantiationException;
      } 
    }
    
    private Object constructStandardJavaInstance(Class<String> param1Class, ScalarNode param1ScalarNode) {
      Object object;
      if (param1Class == String.class) {
        Construct construct = Constructor.this.yamlConstructors.get(Tag.STR);
        object = construct.construct((Node)param1ScalarNode);
      } else if (param1Class == Boolean.class || param1Class == boolean.class) {
        Construct construct = Constructor.this.yamlConstructors.get(Tag.BOOL);
        object = construct.construct((Node)param1ScalarNode);
      } else if (param1Class == Character.class || param1Class == char.class) {
        Construct construct = Constructor.this.yamlConstructors.get(Tag.STR);
        String str = (String)construct.construct((Node)param1ScalarNode);
        if (str.length() == 0) {
          object = null;
        } else {
          if (str.length() != 1)
            throw new YAMLException("Invalid node Character: '" + str + "'; length: " + str
                .length()); 
          object = Character.valueOf(str.charAt(0));
        } 
      } else if (Date.class.isAssignableFrom(param1Class)) {
        Construct construct = Constructor.this.yamlConstructors.get(Tag.TIMESTAMP);
        Date date = (Date)construct.construct((Node)param1ScalarNode);
        if (param1Class == Date.class) {
          object = date;
        } else {
          try {
            Constructor<String> constructor = param1Class.getConstructor(new Class[] { long.class });
            object = constructor.newInstance(new Object[] { Long.valueOf(date.getTime()) });
          } catch (RuntimeException runtimeException) {
            throw runtimeException;
          } catch (Exception exception) {
            throw new YAMLException("Cannot construct: '" + param1Class + "'");
          } 
        } 
      } else if (param1Class == Float.class || param1Class == Double.class || param1Class == float.class || param1Class == double.class || param1Class == BigDecimal.class) {
        if (param1Class == BigDecimal.class) {
          object = new BigDecimal(param1ScalarNode.getValue());
        } else {
          Construct construct = Constructor.this.yamlConstructors.get(Tag.FLOAT);
          object = construct.construct((Node)param1ScalarNode);
          if (param1Class == Float.class || param1Class == float.class)
            object = new Float(((Double)object).doubleValue()); 
        } 
      } else if (param1Class == Byte.class || param1Class == Short.class || param1Class == Integer.class || param1Class == Long.class || param1Class == BigInteger.class || param1Class == byte.class || param1Class == short.class || param1Class == int.class || param1Class == long.class) {
        Construct construct = Constructor.this.yamlConstructors.get(Tag.INT);
        object = construct.construct((Node)param1ScalarNode);
        if (param1Class == Byte.class || param1Class == byte.class) {
          object = Byte.valueOf(object.toString());
        } else if (param1Class == Short.class || param1Class == short.class) {
          object = Short.valueOf(object.toString());
        } else if (param1Class == Integer.class || param1Class == int.class) {
          object = Integer.valueOf(Integer.parseInt(object.toString()));
        } else if (param1Class == Long.class || param1Class == long.class) {
          object = Long.valueOf(object.toString());
        } else {
          object = new BigInteger(object.toString());
        } 
      } else if (Enum.class.isAssignableFrom(param1Class)) {
        String str = param1ScalarNode.getValue();
        try {
          object = Enum.valueOf((Class)param1Class, str);
        } catch (Exception exception) {
          throw new YAMLException("Unable to find enum value '" + str + "' for enum class: " + param1Class
              .getName());
        } 
      } else if (Calendar.class.isAssignableFrom(param1Class)) {
        SafeConstructor.ConstructYamlTimestamp constructYamlTimestamp = new SafeConstructor.ConstructYamlTimestamp();
        constructYamlTimestamp.construct((Node)param1ScalarNode);
        object = constructYamlTimestamp.getCalendar();
      } else if (Number.class.isAssignableFrom(param1Class)) {
        SafeConstructor.ConstructYamlFloat constructYamlFloat = new SafeConstructor.ConstructYamlFloat(Constructor.this);
        object = constructYamlFloat.construct((Node)param1ScalarNode);
      } else if (UUID.class == param1Class) {
        object = UUID.fromString(param1ScalarNode.getValue());
      } else if (Constructor.this.yamlConstructors.containsKey(param1ScalarNode.getTag())) {
        object = ((Construct)Constructor.this.yamlConstructors.get(param1ScalarNode.getTag())).construct((Node)param1ScalarNode);
      } else {
        throw new YAMLException("Unsupported class: " + param1Class);
      } 
      return object;
    }
  }
  
  protected class ConstructSequence implements Construct {
    public Object construct(Node param1Node) {
      SequenceNode sequenceNode = (SequenceNode)param1Node;
      if (Set.class.isAssignableFrom(param1Node.getType())) {
        if (param1Node.isTwoStepsConstruction())
          throw new YAMLException("Set cannot be recursive."); 
        return Constructor.this.constructSet(sequenceNode);
      } 
      if (Collection.class.isAssignableFrom(param1Node.getType())) {
        if (param1Node.isTwoStepsConstruction())
          return Constructor.this.newList(sequenceNode); 
        return Constructor.this.constructSequence(sequenceNode);
      } 
      if (param1Node.getType().isArray()) {
        if (param1Node.isTwoStepsConstruction())
          return Constructor.this.createArray(param1Node.getType(), sequenceNode.getValue().size()); 
        return Constructor.this.constructArray(sequenceNode);
      } 
      ArrayList<Constructor> arrayList = new ArrayList(sequenceNode.getValue().size());
      for (Constructor<?> constructor : param1Node.getType()
        .getDeclaredConstructors()) {
        if (sequenceNode.getValue().size() == (constructor.getParameterTypes()).length)
          arrayList.add(constructor); 
      } 
      if (!arrayList.isEmpty()) {
        if (arrayList.size() == 1) {
          Object[] arrayOfObject = new Object[sequenceNode.getValue().size()];
          Constructor constructor = arrayList.get(0);
          byte b1 = 0;
          for (Node node : sequenceNode.getValue()) {
            Class<?> clazz = constructor.getParameterTypes()[b1];
            node.setType(clazz);
            arrayOfObject[b1++] = Constructor.this.constructObject(node);
          } 
          try {
            constructor.setAccessible(true);
            return constructor.newInstance(arrayOfObject);
          } catch (Exception exception) {
            throw new YAMLException(exception);
          } 
        } 
        List<? extends Object> list = Constructor.this.constructSequence(sequenceNode);
        Class[] arrayOfClass = new Class[list.size()];
        byte b = 0;
        for (Object object : list) {
          arrayOfClass[b] = object.getClass();
          b++;
        } 
        for (Constructor constructor : arrayList) {
          Class[] arrayOfClass1 = constructor.getParameterTypes();
          boolean bool = true;
          for (byte b1 = 0; b1 < arrayOfClass1.length; b1++) {
            if (!wrapIfPrimitive(arrayOfClass1[b1]).isAssignableFrom(arrayOfClass[b1])) {
              bool = false;
              break;
            } 
          } 
          if (bool)
            try {
              constructor.setAccessible(true);
              return constructor.newInstance(list.toArray());
            } catch (Exception exception) {
              throw new YAMLException(exception);
            }  
        } 
      } 
      throw new YAMLException("No suitable constructor with " + 
          String.valueOf(sequenceNode.getValue().size()) + " arguments found for " + param1Node
          .getType());
    }
    
    private final Class<? extends Object> wrapIfPrimitive(Class<?> param1Class) {
      if (!param1Class.isPrimitive())
        return (Class)param1Class; 
      if (param1Class == int.class)
        return (Class)Integer.class; 
      if (param1Class == float.class)
        return (Class)Float.class; 
      if (param1Class == double.class)
        return (Class)Double.class; 
      if (param1Class == boolean.class)
        return (Class)Boolean.class; 
      if (param1Class == long.class)
        return (Class)Long.class; 
      if (param1Class == char.class)
        return (Class)Character.class; 
      if (param1Class == short.class)
        return (Class)Short.class; 
      if (param1Class == byte.class)
        return (Class)Byte.class; 
      throw new YAMLException("Unexpected primitive " + param1Class);
    }
    
    public void construct2ndStep(Node param1Node, Object param1Object) {
      SequenceNode sequenceNode = (SequenceNode)param1Node;
      if (List.class.isAssignableFrom(param1Node.getType())) {
        List<Object> list = (List)param1Object;
        Constructor.this.constructSequenceStep2(sequenceNode, list);
      } else if (param1Node.getType().isArray()) {
        Constructor.this.constructArrayStep2(sequenceNode, param1Object);
      } else {
        throw new YAMLException("Immutable objects cannot be recursive.");
      } 
    }
  }
  
  protected Class<?> getClassForNode(Node paramNode) {
    Class<?> clazz = this.typeTags.get(paramNode.getTag());
    if (clazz == null) {
      Class<?> clazz1;
      String str = paramNode.getTag().getClassName();
      try {
        clazz1 = getClassForName(str);
      } catch (ClassNotFoundException classNotFoundException) {
        throw new YAMLException("Class not found: " + str);
      } 
      this.typeTags.put(paramNode.getTag(), clazz1);
      return clazz1;
    } 
    return clazz;
  }
  
  protected Class<?> getClassForName(String paramString) throws ClassNotFoundException {
    try {
      return Class.forName(paramString, true, Thread.currentThread().getContextClassLoader());
    } catch (ClassNotFoundException classNotFoundException) {
      return Class.forName(paramString);
    } 
  }
}
