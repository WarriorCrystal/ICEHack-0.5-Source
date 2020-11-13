package org.yaml.snakeyaml.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.util.UriEncoder;

public final class Tag implements Comparable<Tag> {
  public static final String PREFIX = "tag:yaml.org,2002:";
  
  public static final Tag YAML = new Tag("tag:yaml.org,2002:yaml");
  
  public static final Tag MERGE = new Tag("tag:yaml.org,2002:merge");
  
  public static final Tag SET = new Tag("tag:yaml.org,2002:set");
  
  public static final Tag PAIRS = new Tag("tag:yaml.org,2002:pairs");
  
  public static final Tag OMAP = new Tag("tag:yaml.org,2002:omap");
  
  public static final Tag BINARY = new Tag("tag:yaml.org,2002:binary");
  
  public static final Tag INT = new Tag("tag:yaml.org,2002:int");
  
  public static final Tag FLOAT = new Tag("tag:yaml.org,2002:float");
  
  public static final Tag TIMESTAMP = new Tag("tag:yaml.org,2002:timestamp");
  
  public static final Tag BOOL = new Tag("tag:yaml.org,2002:bool");
  
  public static final Tag NULL = new Tag("tag:yaml.org,2002:null");
  
  public static final Tag STR = new Tag("tag:yaml.org,2002:str");
  
  public static final Tag SEQ = new Tag("tag:yaml.org,2002:seq");
  
  public static final Tag MAP = new Tag("tag:yaml.org,2002:map");
  
  public static final Map<Tag, Set<Class<?>>> COMPATIBILITY_MAP = new HashMap<Tag, Set<Class<?>>>();
  
  private final String value;
  
  static {
    HashSet<Class<Double>> hashSet = new HashSet();
    hashSet.add(Double.class);
    hashSet.add(Float.class);
    hashSet.add(BigDecimal.class);
    COMPATIBILITY_MAP.put(FLOAT, hashSet);
    HashSet<Class<Integer>> hashSet1 = new HashSet();
    hashSet1.add(Integer.class);
    hashSet1.add(Long.class);
    hashSet1.add(BigInteger.class);
    COMPATIBILITY_MAP.put(INT, hashSet1);
    HashSet<Class<Date>> hashSet2 = new HashSet();
    hashSet2.add(Date.class);
    hashSet2.add(Date.class);
    hashSet2.add(Timestamp.class);
    COMPATIBILITY_MAP.put(TIMESTAMP, hashSet2);
  }
  
  private boolean secondary = false;
  
  public Tag(String paramString) {
    if (paramString == null)
      throw new NullPointerException("Tag must be provided."); 
    if (paramString.length() == 0)
      throw new IllegalArgumentException("Tag must not be empty."); 
    if (paramString.trim().length() != paramString.length())
      throw new IllegalArgumentException("Tag must not contain leading or trailing spaces."); 
    this.value = UriEncoder.encode(paramString);
    this.secondary = !paramString.startsWith("tag:yaml.org,2002:");
  }
  
  public Tag(Class<? extends Object> paramClass) {
    if (paramClass == null)
      throw new NullPointerException("Class for tag must be provided."); 
    this.value = "tag:yaml.org,2002:" + UriEncoder.encode(paramClass.getName());
  }
  
  public Tag(URI paramURI) {
    if (paramURI == null)
      throw new NullPointerException("URI for tag must be provided."); 
    this.value = paramURI.toASCIIString();
  }
  
  public boolean isSecondary() {
    return this.secondary;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public boolean startsWith(String paramString) {
    return this.value.startsWith(paramString);
  }
  
  public String getClassName() {
    if (!this.value.startsWith("tag:yaml.org,2002:"))
      throw new YAMLException("Invalid tag: " + this.value); 
    return UriEncoder.decode(this.value.substring("tag:yaml.org,2002:".length()));
  }
  
  public int getLength() {
    return this.value.length();
  }
  
  public String toString() {
    return this.value;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof Tag)
      return this.value.equals(((Tag)paramObject).getValue()); 
    return false;
  }
  
  public int hashCode() {
    return this.value.hashCode();
  }
  
  public boolean isCompatible(Class<?> paramClass) {
    Set set = COMPATIBILITY_MAP.get(this);
    if (set != null)
      return set.contains(paramClass); 
    return false;
  }
  
  public boolean matches(Class<? extends Object> paramClass) {
    return this.value.equals("tag:yaml.org,2002:" + paramClass.getName());
  }
  
  public int compareTo(Tag paramTag) {
    return this.value.compareTo(paramTag.getValue());
  }
}
