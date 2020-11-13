package org.yaml.snakeyaml.representer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class Representer extends SafeRepresenter {
  protected Map<Class<? extends Object>, TypeDescription> typeDefinitions = Collections.emptyMap();
  
  public Representer() {
    this.representers.put(null, new RepresentJavaBean());
  }
  
  public TypeDescription addTypeDescription(TypeDescription paramTypeDescription) {
    if (Collections.EMPTY_MAP == this.typeDefinitions)
      this.typeDefinitions = new HashMap<Class<? extends Object>, TypeDescription>(); 
    if (paramTypeDescription.getTag() != null)
      addClassTag(paramTypeDescription.getType(), paramTypeDescription.getTag()); 
    paramTypeDescription.setPropertyUtils(getPropertyUtils());
    return this.typeDefinitions.put(paramTypeDescription.getType(), paramTypeDescription);
  }
  
  public void setPropertyUtils(PropertyUtils paramPropertyUtils) {
    super.setPropertyUtils(paramPropertyUtils);
    Collection<TypeDescription> collection = this.typeDefinitions.values();
    for (TypeDescription typeDescription : collection)
      typeDescription.setPropertyUtils(paramPropertyUtils); 
  }
  
  protected class RepresentJavaBean implements Represent {
    public Node representData(Object param1Object) {
      return (Node)Representer.this.representJavaBean(Representer.this.getProperties((Class)param1Object.getClass()), param1Object);
    }
  }
  
  protected MappingNode representJavaBean(Set<Property> paramSet, Object paramObject) {
    ArrayList<NodeTuple> arrayList = new ArrayList(paramSet.size());
    Tag tag2 = this.classTags.get(paramObject.getClass());
    Tag tag1 = (tag2 != null) ? tag2 : new Tag(paramObject.getClass());
    MappingNode mappingNode = new MappingNode(tag1, arrayList, null);
    this.representedObjects.put(paramObject, mappingNode);
    boolean bool = true;
    for (Property property : paramSet) {
      Object object = property.get(paramObject);
      Tag tag = (object == null) ? null : this.classTags.get(object.getClass());
      NodeTuple nodeTuple = representJavaBeanProperty(paramObject, property, object, tag);
      if (nodeTuple == null)
        continue; 
      if (((ScalarNode)nodeTuple.getKeyNode()).getStyle() != null)
        bool = false; 
      Node node = nodeTuple.getValueNode();
      if (!(node instanceof ScalarNode) || ((ScalarNode)node).getStyle() != null)
        bool = false; 
      arrayList.add(nodeTuple);
    } 
    if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
      mappingNode.setFlowStyle(this.defaultFlowStyle.getStyleBoolean());
    } else {
      mappingNode.setFlowStyle(Boolean.valueOf(bool));
    } 
    return mappingNode;
  }
  
  protected NodeTuple representJavaBeanProperty(Object paramObject1, Property paramProperty, Object paramObject2, Tag paramTag) {
    ScalarNode scalarNode = (ScalarNode)representData(paramProperty.getName());
    boolean bool = this.representedObjects.containsKey(paramObject2);
    Node node = representData(paramObject2);
    if (paramObject2 != null && !bool) {
      NodeId nodeId = node.getNodeId();
      if (paramTag == null)
        if (nodeId == NodeId.scalar) {
          if (paramProperty.getType() == paramObject2.getClass() && 
            paramObject2 instanceof Enum)
            node.setTag(Tag.STR); 
        } else {
          if (nodeId == NodeId.mapping && 
            paramProperty.getType() == paramObject2.getClass() && 
            !(paramObject2 instanceof Map) && 
            !node.getTag().equals(Tag.SET))
            node.setTag(Tag.MAP); 
          checkGlobalTag(paramProperty, node, paramObject2);
        }  
    } 
    return new NodeTuple((Node)scalarNode, node);
  }
  
  protected void checkGlobalTag(Property paramProperty, Node paramNode, Object paramObject) {
    if (paramObject.getClass().isArray() && paramObject.getClass().getComponentType().isPrimitive())
      return; 
    Class[] arrayOfClass = paramProperty.getActualTypeArguments();
    if (arrayOfClass != null)
      if (paramNode.getNodeId() == NodeId.sequence) {
        Class clazz = arrayOfClass[0];
        SequenceNode sequenceNode = (SequenceNode)paramNode;
        Iterable iterable = Collections.EMPTY_LIST;
        if (paramObject.getClass().isArray()) {
          iterable = Arrays.asList((Object[])paramObject);
        } else if (paramObject instanceof Iterable) {
          iterable = (Iterable)paramObject;
        } 
        Iterator<Object> iterator = iterable.iterator();
        if (iterator.hasNext())
          for (Node node : sequenceNode.getValue()) {
            Object object = iterator.next();
            if (object != null && 
              clazz.equals(object.getClass()) && 
              node.getNodeId() == NodeId.mapping)
              node.setTag(Tag.MAP); 
          }  
      } else if (paramObject instanceof Set) {
        Class clazz = arrayOfClass[0];
        MappingNode mappingNode = (MappingNode)paramNode;
        Iterator<NodeTuple> iterator = mappingNode.getValue().iterator();
        Set set = (Set)paramObject;
        for (Object object : set) {
          NodeTuple nodeTuple = iterator.next();
          Node node = nodeTuple.getKeyNode();
          if (clazz.equals(object.getClass()) && 
            node.getNodeId() == NodeId.mapping)
            node.setTag(Tag.MAP); 
        } 
      } else if (paramObject instanceof Map) {
        Class<? extends Object> clazz1 = arrayOfClass[0];
        Class<? extends Object> clazz2 = arrayOfClass[1];
        MappingNode mappingNode = (MappingNode)paramNode;
        for (NodeTuple nodeTuple : mappingNode.getValue()) {
          resetTag(clazz1, nodeTuple.getKeyNode());
          resetTag(clazz2, nodeTuple.getValueNode());
        } 
      }  
  }
  
  private void resetTag(Class<? extends Object> paramClass, Node paramNode) {
    Tag tag = paramNode.getTag();
    if (tag.matches(paramClass))
      if (Enum.class.isAssignableFrom(paramClass)) {
        paramNode.setTag(Tag.STR);
      } else {
        paramNode.setTag(Tag.MAP);
      }  
  }
  
  protected Set<Property> getProperties(Class<? extends Object> paramClass) {
    if (this.typeDefinitions.containsKey(paramClass))
      return ((TypeDescription)this.typeDefinitions.get(paramClass)).getProperties(); 
    return getPropertyUtils().getProperties(paramClass);
  }
}
