package org.yaml.snakeyaml.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;

public class Resolver {
  public static final Pattern BOOL = Pattern.compile("^(?:yes|Yes|YES|no|No|NO|true|True|TRUE|false|False|FALSE|on|On|ON|off|Off|OFF)$");
  
  public static final Pattern FLOAT = Pattern.compile("^([-+]?(\\.[0-9]+|[0-9_]+(\\.[0-9_]*)?)([eE][-+]?[0-9]+)?|[-+]?[0-9][0-9_]*(?::[0-5]?[0-9])+\\.[0-9_]*|[-+]?\\.(?:inf|Inf|INF)|\\.(?:nan|NaN|NAN))$");
  
  public static final Pattern INT = Pattern.compile("^(?:[-+]?0b[0-1_]+|[-+]?0[0-7_]+|[-+]?(?:0|[1-9][0-9_]*)|[-+]?0x[0-9a-fA-F_]+|[-+]?[1-9][0-9_]*(?::[0-5]?[0-9])+)$");
  
  public static final Pattern MERGE = Pattern.compile("^(?:<<)$");
  
  public static final Pattern NULL = Pattern.compile("^(?:~|null|Null|NULL| )$");
  
  public static final Pattern EMPTY = Pattern.compile("^$");
  
  public static final Pattern TIMESTAMP = Pattern.compile("^(?:[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]|[0-9][0-9][0-9][0-9]-[0-9][0-9]?-[0-9][0-9]?(?:[Tt]|[ \t]+)[0-9][0-9]?:[0-9][0-9]:[0-9][0-9](?:\\.[0-9]*)?(?:[ \t]*(?:Z|[-+][0-9][0-9]?(?::[0-9][0-9])?))?)$");
  
  public static final Pattern VALUE = Pattern.compile("^(?:=)$");
  
  public static final Pattern YAML = Pattern.compile("^(?:!|&|\\*)$");
  
  protected Map<Character, List<ResolverTuple>> yamlImplicitResolvers = new HashMap<Character, List<ResolverTuple>>();
  
  protected void addImplicitResolvers() {
    addImplicitResolver(Tag.BOOL, BOOL, "yYnNtTfFoO");
    addImplicitResolver(Tag.INT, INT, "-+0123456789");
    addImplicitResolver(Tag.FLOAT, FLOAT, "-+0123456789.");
    addImplicitResolver(Tag.MERGE, MERGE, "<");
    addImplicitResolver(Tag.NULL, NULL, "~nN\000");
    addImplicitResolver(Tag.NULL, EMPTY, null);
    addImplicitResolver(Tag.TIMESTAMP, TIMESTAMP, "0123456789");
    addImplicitResolver(Tag.YAML, YAML, "!&*");
  }
  
  public Resolver() {
    addImplicitResolvers();
  }
  
  public void addImplicitResolver(Tag paramTag, Pattern paramPattern, String paramString) {
    if (paramString == null) {
      List<ResolverTuple> list = this.yamlImplicitResolvers.get(null);
      if (list == null) {
        list = new ArrayList();
        this.yamlImplicitResolvers.put(null, list);
      } 
      list.add(new ResolverTuple(paramTag, paramPattern));
    } else {
      char[] arrayOfChar = paramString.toCharArray();
      byte b;
      int i;
      for (b = 0, i = arrayOfChar.length; b < i; b++) {
        Character character = Character.valueOf(arrayOfChar[b]);
        if (character.charValue() == '\000')
          character = null; 
        List<ResolverTuple> list = this.yamlImplicitResolvers.get(character);
        if (list == null) {
          list = new ArrayList();
          this.yamlImplicitResolvers.put(character, list);
        } 
        list.add(new ResolverTuple(paramTag, paramPattern));
      } 
    } 
  }
  
  public Tag resolve(NodeId paramNodeId, String paramString, boolean paramBoolean) {
    if (paramNodeId == NodeId.scalar && paramBoolean) {
      List list = null;
      if (paramString.length() == 0) {
        list = this.yamlImplicitResolvers.get(Character.valueOf(false));
      } else {
        list = this.yamlImplicitResolvers.get(Character.valueOf(paramString.charAt(0)));
      } 
      if (list != null)
        for (ResolverTuple resolverTuple : list) {
          Tag tag = resolverTuple.getTag();
          Pattern pattern = resolverTuple.getRegexp();
          if (pattern.matcher(paramString).matches())
            return tag; 
        }  
      if (this.yamlImplicitResolvers.containsKey(null))
        for (ResolverTuple resolverTuple : this.yamlImplicitResolvers.get(null)) {
          Tag tag = resolverTuple.getTag();
          Pattern pattern = resolverTuple.getRegexp();
          if (pattern.matcher(paramString).matches())
            return tag; 
        }  
    } 
    switch (paramNodeId) {
      case scalar:
        return Tag.STR;
      case sequence:
        return Tag.SEQ;
    } 
    return Tag.MAP;
  }
}
