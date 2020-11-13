package org.reflections.scanners;

import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import javax.annotation.Nullable;
import org.reflections.Configuration;
import org.reflections.vfs.Vfs;

public interface Scanner {
  void setConfiguration(Configuration paramConfiguration);
  
  Multimap<String, String> getStore();
  
  void setStore(Multimap<String, String> paramMultimap);
  
  Scanner filterResultsBy(Predicate<String> paramPredicate);
  
  boolean acceptsInput(String paramString);
  
  Object scan(Vfs.File paramFile, @Nullable Object paramObject);
  
  boolean acceptResult(String paramString);
}
