package org.reflections.util;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.reflections.ReflectionsException;

public class FilterBuilder implements Predicate<String> {
  private final List<Predicate<String>> chain;
  
  public FilterBuilder() {
    this.chain = Lists.newArrayList();
  }
  
  private FilterBuilder(Iterable<Predicate<String>> paramIterable) {
    this.chain = Lists.newArrayList(paramIterable);
  }
  
  public FilterBuilder include(String paramString) {
    return add(new Include(paramString));
  }
  
  public FilterBuilder exclude(String paramString) {
    add(new Exclude(paramString));
    return this;
  }
  
  public FilterBuilder add(Predicate<String> paramPredicate) {
    this.chain.add(paramPredicate);
    return this;
  }
  
  public FilterBuilder includePackage(Class<?> paramClass) {
    return add(new Include(packageNameRegex(paramClass)));
  }
  
  public FilterBuilder excludePackage(Class<?> paramClass) {
    return add(new Exclude(packageNameRegex(paramClass)));
  }
  
  public FilterBuilder includePackage(String... paramVarArgs) {
    for (String str : paramVarArgs)
      add(new Include(prefix(str))); 
    return this;
  }
  
  public FilterBuilder excludePackage(String paramString) {
    return add(new Exclude(prefix(paramString)));
  }
  
  private static String packageNameRegex(Class<?> paramClass) {
    return prefix(paramClass.getPackage().getName() + ".");
  }
  
  public static String prefix(String paramString) {
    return paramString.replace(".", "\\.") + ".*";
  }
  
  public String toString() {
    return Joiner.on(", ").join(this.chain);
  }
  
  public boolean apply(String paramString) {
    boolean bool = (this.chain == null || this.chain.isEmpty() || this.chain.get(0) instanceof Exclude);
    if (this.chain != null)
      for (Predicate<String> predicate : this.chain) {
        if ((bool && predicate instanceof Include) || (
          !bool && predicate instanceof Exclude))
          continue; 
        bool = predicate.apply(paramString);
        if (!bool && predicate instanceof Exclude)
          break; 
      }  
    return bool;
  }
  
  public static abstract class Matcher implements Predicate<String> {
    final Pattern pattern;
    
    public Matcher(String param1String) {
      this.pattern = Pattern.compile(param1String);
    }
    
    public String toString() {
      return this.pattern.pattern();
    }
    
    public abstract boolean apply(String param1String);
  }
  
  public static class Include extends Matcher {
    public Include(String param1String) {
      super(param1String);
    }
    
    public boolean apply(String param1String) {
      return this.pattern.matcher(param1String).matches();
    }
    
    public String toString() {
      return "+" + super.toString();
    }
  }
  
  public static class Exclude extends Matcher {
    public Exclude(String param1String) {
      super(param1String);
    }
    
    public boolean apply(String param1String) {
      return !this.pattern.matcher(param1String).matches();
    }
    
    public String toString() {
      return "-" + super.toString();
    }
  }
  
  public static FilterBuilder parse(String paramString) {
    ArrayList<Exclude> arrayList = new ArrayList();
    if (!Utils.isEmpty(paramString)) {
      for (String str1 : paramString.split(",")) {
        Include include;
        Exclude exclude;
        String str2 = str1.trim();
        char c = str2.charAt(0);
        String str3 = str2.substring(1);
        switch (c) {
          case '+':
            include = new Include(str3);
            break;
          case '-':
            exclude = new Exclude(str3);
            break;
          default:
            throw new ReflectionsException("includeExclude should start with either + or -");
        } 
        arrayList.add(exclude);
      } 
      return new FilterBuilder((Iterable)arrayList);
    } 
    return new FilterBuilder();
  }
  
  public static FilterBuilder parsePackages(String paramString) {
    ArrayList<Exclude> arrayList = new ArrayList();
    if (!Utils.isEmpty(paramString)) {
      for (String str1 : paramString.split(",")) {
        Include include;
        Exclude exclude;
        String str2 = str1.trim();
        char c = str2.charAt(0);
        String str3 = str2.substring(1);
        if (!str3.endsWith("."))
          str3 = str3 + "."; 
        str3 = prefix(str3);
        switch (c) {
          case '+':
            include = new Include(str3);
            break;
          case '-':
            exclude = new Exclude(str3);
            break;
          default:
            throw new ReflectionsException("includeExclude should start with either + or -");
        } 
        arrayList.add(exclude);
      } 
      return new FilterBuilder((Iterable)arrayList);
    } 
    return new FilterBuilder();
  }
}
