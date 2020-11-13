package org.yaml.snakeyaml.representer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public abstract class BaseRepresenter {
  protected final Map<Class<?>, Represent> representers = new HashMap<Class<?>, Represent>();
  
  protected Represent nullRepresenter;
  
  protected final Map<Class<?>, Represent> multiRepresenters = new LinkedHashMap<Class<?>, Represent>();
  
  protected Character defaultScalarStyle;
  
  protected DumperOptions.FlowStyle defaultFlowStyle = DumperOptions.FlowStyle.AUTO;
  
  protected final Map<Object, Node> representedObjects = new IdentityHashMap<Object, Node>() {
      private static final long serialVersionUID = -5576159264232131854L;
      
      public Node put(Object param1Object, Node param1Node) {
        return (Node)super.put(param1Object, new AnchorNode(param1Node));
      }
    };
  
  protected Object objectToRepresent;
  
  private PropertyUtils propertyUtils;
  
  private boolean explicitPropertyUtils = false;
  
  public Node represent(Object paramObject) {
    Node node = representData(paramObject);
    this.representedObjects.clear();
    this.objectToRepresent = null;
    return node;
  }
  
  protected final Node representData(Object paramObject) {
    Node node;
    this.objectToRepresent = paramObject;
    if (this.representedObjects.containsKey(this.objectToRepresent)) {
      node = this.representedObjects.get(this.objectToRepresent);
      return node;
    } 
    if (paramObject == null) {
      node = this.nullRepresenter.representData(null);
      return node;
    } 
    Class<?> clazz = paramObject.getClass();
    if (this.representers.containsKey(clazz)) {
      Represent represent = this.representers.get(clazz);
      node = represent.representData(paramObject);
    } else {
      for (Class<?> clazz1 : this.multiRepresenters.keySet()) {
        if (clazz1 != null && clazz1.isInstance(paramObject)) {
          Represent represent = this.multiRepresenters.get(clazz1);
          node = represent.representData(paramObject);
          return node;
        } 
      } 
      if (this.multiRepresenters.containsKey(null)) {
        Represent represent = this.multiRepresenters.get(null);
        node = represent.representData(paramObject);
      } else {
        Represent represent = this.representers.get(null);
        node = represent.representData(paramObject);
      } 
    } 
    return node;
  }
  
  protected Node representScalar(Tag paramTag, String paramString, Character paramCharacter) {
    if (paramCharacter == null)
      paramCharacter = this.defaultScalarStyle; 
    return (Node)new ScalarNode(paramTag, paramString, null, null, paramCharacter);
  }
  
  protected Node representScalar(Tag paramTag, String paramString) {
    return representScalar(paramTag, paramString, null);
  }
  
  protected Node representSequence(Tag paramTag, Iterable<?> paramIterable, Boolean paramBoolean) {
    int i = 10;
    if (paramIterable instanceof List)
      i = ((List)paramIterable).size(); 
    ArrayList<Node> arrayList = new ArrayList(i);
    SequenceNode sequenceNode = new SequenceNode(paramTag, arrayList, paramBoolean);
    this.representedObjects.put(this.objectToRepresent, sequenceNode);
    boolean bool = true;
    for (Object object : paramIterable) {
      Node node = representData(object);
      if (!(node instanceof ScalarNode) || ((ScalarNode)node).getStyle() != null)
        bool = false; 
      arrayList.add(node);
    } 
    if (paramBoolean == null)
      if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
        sequenceNode.setFlowStyle(this.defaultFlowStyle.getStyleBoolean());
      } else {
        sequenceNode.setFlowStyle(Boolean.valueOf(bool));
      }  
    return (Node)sequenceNode;
  }
  
  protected Node representMapping(Tag paramTag, Map<?, ?> paramMap, Boolean paramBoolean) {
    ArrayList<NodeTuple> arrayList = new ArrayList(paramMap.size());
    MappingNode mappingNode = new MappingNode(paramTag, arrayList, paramBoolean);
    this.representedObjects.put(this.objectToRepresent, mappingNode);
    boolean bool = true;
    for (Map.Entry<?, ?> entry : paramMap.entrySet()) {
      Node node1 = representData(entry.getKey());
      Node node2 = representData(entry.getValue());
      if (!(node1 instanceof ScalarNode) || ((ScalarNode)node1).getStyle() != null)
        bool = false; 
      if (!(node2 instanceof ScalarNode) || ((ScalarNode)node2).getStyle() != null)
        bool = false; 
      arrayList.add(new NodeTuple(node1, node2));
    } 
    if (paramBoolean == null)
      if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
        mappingNode.setFlowStyle(this.defaultFlowStyle.getStyleBoolean());
      } else {
        mappingNode.setFlowStyle(Boolean.valueOf(bool));
      }  
    return (Node)mappingNode;
  }
  
  public void setDefaultScalarStyle(DumperOptions.ScalarStyle paramScalarStyle) {
    this.defaultScalarStyle = paramScalarStyle.getChar();
  }
  
  public DumperOptions.ScalarStyle getDefaultScalarStyle() {
    return DumperOptions.ScalarStyle.createStyle(this.defaultScalarStyle);
  }
  
  public void setDefaultFlowStyle(DumperOptions.FlowStyle paramFlowStyle) {
    this.defaultFlowStyle = paramFlowStyle;
  }
  
  public DumperOptions.FlowStyle getDefaultFlowStyle() {
    return this.defaultFlowStyle;
  }
  
  public void setPropertyUtils(PropertyUtils paramPropertyUtils) {
    this.propertyUtils = paramPropertyUtils;
    this.explicitPropertyUtils = true;
  }
  
  public final PropertyUtils getPropertyUtils() {
    if (this.propertyUtils == null)
      this.propertyUtils = new PropertyUtils(); 
    return this.propertyUtils;
  }
  
  public final boolean isExplicitPropertyUtils() {
    return this.explicitPropertyUtils;
  }
}
