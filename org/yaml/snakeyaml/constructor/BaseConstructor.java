package org.yaml.snakeyaml.constructor;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public abstract class BaseConstructor {
  protected final Map<NodeId, Construct> yamlClassConstructors = new EnumMap<NodeId, Construct>(NodeId.class);
  
  protected final Map<Tag, Construct> yamlConstructors = new HashMap<Tag, Construct>();
  
  protected final Map<String, Construct> yamlMultiConstructors = new HashMap<String, Construct>();
  
  protected Composer composer;
  
  final Map<Node, Object> constructedObjects;
  
  private final Set<Node> recursiveObjects;
  
  private final ArrayList<RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>> maps2fill;
  
  private final ArrayList<RecursiveTuple<Set<Object>, Object>> sets2fill;
  
  protected Tag rootTag;
  
  private PropertyUtils propertyUtils;
  
  private boolean explicitPropertyUtils;
  
  private boolean allowDuplicateKeys = true;
  
  protected final Map<Class<? extends Object>, TypeDescription> typeDefinitions;
  
  protected final Map<Tag, Class<? extends Object>> typeTags;
  
  public BaseConstructor() {
    this.constructedObjects = new HashMap<Node, Object>();
    this.recursiveObjects = new HashSet<Node>();
    this.maps2fill = new ArrayList<RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>>();
    this.sets2fill = new ArrayList<RecursiveTuple<Set<Object>, Object>>();
    this.typeDefinitions = new HashMap<Class<? extends Object>, TypeDescription>();
    this.typeTags = new HashMap<Tag, Class<? extends Object>>();
    this.rootTag = null;
    this.explicitPropertyUtils = false;
    this.typeDefinitions.put(SortedMap.class, new TypeDescription(SortedMap.class, Tag.OMAP, TreeMap.class));
    this.typeDefinitions.put(SortedSet.class, new TypeDescription(SortedSet.class, Tag.SET, TreeSet.class));
  }
  
  public void setComposer(Composer paramComposer) {
    this.composer = paramComposer;
  }
  
  public boolean checkData() {
    return this.composer.checkNode();
  }
  
  public Object getData() {
    this.composer.checkNode();
    Node node = this.composer.getNode();
    if (this.rootTag != null)
      node.setTag(this.rootTag); 
    return constructDocument(node);
  }
  
  public Object getSingleData(Class<?> paramClass) {
    Node node = this.composer.getSingleNode();
    if (node != null && !Tag.NULL.equals(node.getTag())) {
      if (Object.class != paramClass) {
        node.setTag(new Tag(paramClass));
      } else if (this.rootTag != null) {
        node.setTag(this.rootTag);
      } 
      return constructDocument(node);
    } 
    return null;
  }
  
  protected final Object constructDocument(Node paramNode) {
    Object object = constructObject(paramNode);
    fillRecursive();
    this.constructedObjects.clear();
    this.recursiveObjects.clear();
    return object;
  }
  
  private void fillRecursive() {
    if (!this.maps2fill.isEmpty()) {
      for (RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>> recursiveTuple : this.maps2fill) {
        RecursiveTuple recursiveTuple1 = (RecursiveTuple)recursiveTuple._2();
        ((Map)recursiveTuple._1()).put(recursiveTuple1._1(), recursiveTuple1._2());
      } 
      this.maps2fill.clear();
    } 
    if (!this.sets2fill.isEmpty()) {
      for (RecursiveTuple<Set<Object>, Object> recursiveTuple : this.sets2fill)
        ((Set)recursiveTuple._1()).add(recursiveTuple._2()); 
      this.sets2fill.clear();
    } 
  }
  
  protected Object constructObject(Node paramNode) {
    if (this.constructedObjects.containsKey(paramNode))
      return this.constructedObjects.get(paramNode); 
    return constructObjectNoCheck(paramNode);
  }
  
  protected Object constructObjectNoCheck(Node paramNode) {
    if (this.recursiveObjects.contains(paramNode))
      throw new ConstructorException(null, null, "found unconstructable recursive node", paramNode
          .getStartMark()); 
    this.recursiveObjects.add(paramNode);
    Construct construct = getConstructor(paramNode);
    Object object = this.constructedObjects.containsKey(paramNode) ? this.constructedObjects.get(paramNode) : construct.construct(paramNode);
    finalizeConstruction(paramNode, object);
    this.constructedObjects.put(paramNode, object);
    this.recursiveObjects.remove(paramNode);
    if (paramNode.isTwoStepsConstruction())
      construct.construct2ndStep(paramNode, object); 
    return object;
  }
  
  protected Construct getConstructor(Node paramNode) {
    if (paramNode.useClassConstructor())
      return this.yamlClassConstructors.get(paramNode.getNodeId()); 
    Construct construct = this.yamlConstructors.get(paramNode.getTag());
    if (construct == null) {
      for (String str : this.yamlMultiConstructors.keySet()) {
        if (paramNode.getTag().startsWith(str))
          return this.yamlMultiConstructors.get(str); 
      } 
      return this.yamlConstructors.get(null);
    } 
    return construct;
  }
  
  protected Object constructScalar(ScalarNode paramScalarNode) {
    return paramScalarNode.getValue();
  }
  
  protected List<Object> createDefaultList(int paramInt) {
    return new ArrayList(paramInt);
  }
  
  protected Set<Object> createDefaultSet(int paramInt) {
    return new LinkedHashSet(paramInt);
  }
  
  protected Map<Object, Object> createDefaultMap() {
    return new LinkedHashMap<Object, Object>();
  }
  
  protected Set<Object> createDefaultSet() {
    return new LinkedHashSet();
  }
  
  protected Object createArray(Class<?> paramClass, int paramInt) {
    return Array.newInstance(paramClass.getComponentType(), paramInt);
  }
  
  protected Object finalizeConstruction(Node paramNode, Object paramObject) {
    Class clazz = paramNode.getType();
    if (this.typeDefinitions.containsKey(clazz))
      return ((TypeDescription)this.typeDefinitions.get(clazz)).finalizeConstruction(paramObject); 
    return paramObject;
  }
  
  protected Object newInstance(Node paramNode) {
    try {
      return newInstance(Object.class, paramNode);
    } catch (InstantiationException instantiationException) {
      throw new YAMLException(instantiationException);
    } 
  }
  
  protected final Object newInstance(Class<?> paramClass, Node paramNode) throws InstantiationException {
    return newInstance(paramClass, paramNode, true);
  }
  
  protected Object newInstance(Class<?> paramClass, Node paramNode, boolean paramBoolean) throws InstantiationException {
    Class<?> clazz = paramNode.getType();
    if (this.typeDefinitions.containsKey(clazz)) {
      TypeDescription typeDescription = this.typeDefinitions.get(clazz);
      Object object = typeDescription.newInstance(paramNode);
      if (object != null)
        return object; 
    } 
    if (paramBoolean)
      if (paramClass.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers()))
        try {
          Constructor<?> constructor = clazz.getDeclaredConstructor(new Class[0]);
          constructor.setAccessible(true);
          return constructor.newInstance(new Object[0]);
        } catch (NoSuchMethodException noSuchMethodException) {
          throw new InstantiationException("NoSuchMethodException:" + noSuchMethodException
              .getLocalizedMessage());
        } catch (Exception exception) {
          throw new YAMLException(exception);
        }   
    throw new InstantiationException();
  }
  
  protected Set<Object> newSet(CollectionNode<?> paramCollectionNode) {
    try {
      return (Set<Object>)newInstance(Set.class, (Node)paramCollectionNode);
    } catch (InstantiationException instantiationException) {
      return createDefaultSet(paramCollectionNode.getValue().size());
    } 
  }
  
  protected List<Object> newList(SequenceNode paramSequenceNode) {
    try {
      return (List<Object>)newInstance(List.class, (Node)paramSequenceNode);
    } catch (InstantiationException instantiationException) {
      return createDefaultList(paramSequenceNode.getValue().size());
    } 
  }
  
  protected Map<Object, Object> newMap(MappingNode paramMappingNode) {
    try {
      return (Map<Object, Object>)newInstance(Map.class, (Node)paramMappingNode);
    } catch (InstantiationException instantiationException) {
      return createDefaultMap();
    } 
  }
  
  protected List<? extends Object> constructSequence(SequenceNode paramSequenceNode) {
    List<Object> list = newList(paramSequenceNode);
    constructSequenceStep2(paramSequenceNode, list);
    return list;
  }
  
  protected Set<? extends Object> constructSet(SequenceNode paramSequenceNode) {
    Set<Object> set = newSet((CollectionNode<?>)paramSequenceNode);
    constructSequenceStep2(paramSequenceNode, set);
    return set;
  }
  
  protected Object constructArray(SequenceNode paramSequenceNode) {
    return constructArrayStep2(paramSequenceNode, createArray(paramSequenceNode.getType(), paramSequenceNode.getValue().size()));
  }
  
  protected void constructSequenceStep2(SequenceNode paramSequenceNode, Collection<Object> paramCollection) {
    for (Node node : paramSequenceNode.getValue())
      paramCollection.add(constructObject(node)); 
  }
  
  protected Object constructArrayStep2(SequenceNode paramSequenceNode, Object paramObject) {
    Class<?> clazz = paramSequenceNode.getType().getComponentType();
    byte b = 0;
    for (Node node : paramSequenceNode.getValue()) {
      if (node.getType() == Object.class)
        node.setType(clazz); 
      Object object = constructObject(node);
      if (clazz.isPrimitive()) {
        if (object == null)
          throw new NullPointerException("Unable to construct element value for " + node); 
        if (byte.class.equals(clazz)) {
          Array.setByte(paramObject, b, ((Number)object).byteValue());
        } else if (short.class.equals(clazz)) {
          Array.setShort(paramObject, b, ((Number)object).shortValue());
        } else if (int.class.equals(clazz)) {
          Array.setInt(paramObject, b, ((Number)object).intValue());
        } else if (long.class.equals(clazz)) {
          Array.setLong(paramObject, b, ((Number)object).longValue());
        } else if (float.class.equals(clazz)) {
          Array.setFloat(paramObject, b, ((Number)object).floatValue());
        } else if (double.class.equals(clazz)) {
          Array.setDouble(paramObject, b, ((Number)object).doubleValue());
        } else if (char.class.equals(clazz)) {
          Array.setChar(paramObject, b, ((Character)object).charValue());
        } else if (boolean.class.equals(clazz)) {
          Array.setBoolean(paramObject, b, ((Boolean)object).booleanValue());
        } else {
          throw new YAMLException("unexpected primitive type");
        } 
      } else {
        Array.set(paramObject, b, object);
      } 
      b++;
    } 
    return paramObject;
  }
  
  protected Set<Object> constructSet(MappingNode paramMappingNode) {
    Set<Object> set = newSet((CollectionNode<?>)paramMappingNode);
    constructSet2ndStep(paramMappingNode, set);
    return set;
  }
  
  protected Map<Object, Object> constructMapping(MappingNode paramMappingNode) {
    Map<Object, Object> map = newMap(paramMappingNode);
    constructMapping2ndStep(paramMappingNode, map);
    return map;
  }
  
  protected void constructMapping2ndStep(MappingNode paramMappingNode, Map<Object, Object> paramMap) {
    List list = paramMappingNode.getValue();
    for (NodeTuple nodeTuple : list) {
      Node node1 = nodeTuple.getKeyNode();
      Node node2 = nodeTuple.getValueNode();
      Object object1 = constructObject(node1);
      if (object1 != null)
        try {
          object1.hashCode();
        } catch (Exception exception) {
          throw new ConstructorException("while constructing a mapping", paramMappingNode
              .getStartMark(), "found unacceptable key " + object1, nodeTuple
              .getKeyNode().getStartMark(), exception);
        }  
      Object object2 = constructObject(node2);
      if (node1.isTwoStepsConstruction()) {
        this.maps2fill.add(0, new RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>(paramMap, new RecursiveTuple<Object, Object>(object1, object2)));
        continue;
      } 
      paramMap.put(object1, object2);
    } 
  }
  
  protected void constructSet2ndStep(MappingNode paramMappingNode, Set<Object> paramSet) {
    List list = paramMappingNode.getValue();
    for (NodeTuple nodeTuple : list) {
      Node node = nodeTuple.getKeyNode();
      Object object = constructObject(node);
      if (object != null)
        try {
          object.hashCode();
        } catch (Exception exception) {
          throw new ConstructorException("while constructing a Set", paramMappingNode.getStartMark(), "found unacceptable key " + object, nodeTuple
              .getKeyNode().getStartMark(), exception);
        }  
      if (node.isTwoStepsConstruction()) {
        this.sets2fill.add(0, new RecursiveTuple<Set<Object>, Object>(paramSet, object));
        continue;
      } 
      paramSet.add(object);
    } 
  }
  
  public void setPropertyUtils(PropertyUtils paramPropertyUtils) {
    this.propertyUtils = paramPropertyUtils;
    this.explicitPropertyUtils = true;
    Collection<TypeDescription> collection = this.typeDefinitions.values();
    for (TypeDescription typeDescription : collection)
      typeDescription.setPropertyUtils(paramPropertyUtils); 
  }
  
  public final PropertyUtils getPropertyUtils() {
    if (this.propertyUtils == null)
      this.propertyUtils = new PropertyUtils(); 
    return this.propertyUtils;
  }
  
  public TypeDescription addTypeDescription(TypeDescription paramTypeDescription) {
    if (paramTypeDescription == null)
      throw new NullPointerException("TypeDescription is required."); 
    Tag tag = paramTypeDescription.getTag();
    this.typeTags.put(tag, paramTypeDescription.getType());
    paramTypeDescription.setPropertyUtils(getPropertyUtils());
    return this.typeDefinitions.put(paramTypeDescription.getType(), paramTypeDescription);
  }
  
  private static class RecursiveTuple<T, K> {
    private final T _1;
    
    private final K _2;
    
    public RecursiveTuple(T param1T, K param1K) {
      this._1 = param1T;
      this._2 = param1K;
    }
    
    public K _2() {
      return this._2;
    }
    
    public T _1() {
      return this._1;
    }
  }
  
  public final boolean isExplicitPropertyUtils() {
    return this.explicitPropertyUtils;
  }
  
  public boolean isAllowDuplicateKeys() {
    return this.allowDuplicateKeys;
  }
  
  public void setAllowDuplicateKeys(boolean paramBoolean) {
    this.allowDuplicateKeys = paramBoolean;
  }
}
