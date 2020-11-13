package org.reflections.scanners;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Multimap;
import org.reflections.Configuration;
import org.reflections.ReflectionsException;
import org.reflections.adapters.MetadataAdapter;
import org.reflections.vfs.Vfs;

public abstract class AbstractScanner implements Scanner {
  private Configuration configuration;
  
  private Multimap<String, String> store;
  
  private Predicate<String> resultFilter = Predicates.alwaysTrue();
  
  public boolean acceptsInput(String paramString) {
    return getMetadataAdapter().acceptsInput(paramString);
  }
  
  public Object scan(Vfs.File paramFile, Object paramObject) {
    if (paramObject == null)
      try {
        paramObject = this.configuration.getMetadataAdapter().getOfCreateClassObject(paramFile);
      } catch (Exception exception) {
        throw new ReflectionsException("could not create class object from file " + paramFile.getRelativePath(), exception);
      }  
    scan(paramObject);
    return paramObject;
  }
  
  public abstract void scan(Object paramObject);
  
  public Configuration getConfiguration() {
    return this.configuration;
  }
  
  public void setConfiguration(Configuration paramConfiguration) {
    this.configuration = paramConfiguration;
  }
  
  public Multimap<String, String> getStore() {
    return this.store;
  }
  
  public void setStore(Multimap<String, String> paramMultimap) {
    this.store = paramMultimap;
  }
  
  public Predicate<String> getResultFilter() {
    return this.resultFilter;
  }
  
  public void setResultFilter(Predicate<String> paramPredicate) {
    this.resultFilter = paramPredicate;
  }
  
  public Scanner filterResultsBy(Predicate<String> paramPredicate) {
    setResultFilter(paramPredicate);
    return this;
  }
  
  public boolean acceptResult(String paramString) {
    return (paramString != null && this.resultFilter.apply(paramString));
  }
  
  protected MetadataAdapter getMetadataAdapter() {
    return this.configuration.getMetadataAdapter();
  }
  
  public boolean equals(Object paramObject) {
    return (this == paramObject || (paramObject != null && getClass() == paramObject.getClass()));
  }
  
  public int hashCode() {
    return getClass().hashCode();
  }
}
