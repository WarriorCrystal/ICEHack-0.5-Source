package org.yaml.snakeyaml.util;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

public class ArrayUtils {
  public static <E> List<E> toUnmodifiableList(E... paramVarArgs) {
    return (paramVarArgs.length == 0) ? Collections.<E>emptyList() : new UnmodifiableArrayList<E>(paramVarArgs);
  }
  
  public static <E> List<E> toUnmodifiableCompositeList(E[] paramArrayOfE1, E[] paramArrayOfE2) {
    List<E> list;
    if (paramArrayOfE1.length == 0) {
      list = toUnmodifiableList(paramArrayOfE2);
    } else if (paramArrayOfE2.length == 0) {
      list = toUnmodifiableList(paramArrayOfE1);
    } else {
      list = new CompositeUnmodifiableArrayList<E>(paramArrayOfE1, paramArrayOfE2);
    } 
    return list;
  }
  
  private static class UnmodifiableArrayList<E> extends AbstractList<E> {
    private final E[] array;
    
    UnmodifiableArrayList(E[] param1ArrayOfE) {
      this.array = param1ArrayOfE;
    }
    
    public E get(int param1Int) {
      if (param1Int >= this.array.length)
        throw new IndexOutOfBoundsException("Index: " + param1Int + ", Size: " + size()); 
      return this.array[param1Int];
    }
    
    public int size() {
      return this.array.length;
    }
  }
  
  private static class CompositeUnmodifiableArrayList<E> extends AbstractList<E> {
    private final E[] array1;
    
    private final E[] array2;
    
    CompositeUnmodifiableArrayList(E[] param1ArrayOfE1, E[] param1ArrayOfE2) {
      this.array1 = param1ArrayOfE1;
      this.array2 = param1ArrayOfE2;
    }
    
    public E get(int param1Int) {
      E e;
      if (param1Int < this.array1.length) {
        e = this.array1[param1Int];
      } else if (param1Int - this.array1.length < this.array2.length) {
        e = this.array2[param1Int - this.array1.length];
      } else {
        throw new IndexOutOfBoundsException("Index: " + param1Int + ", Size: " + size());
      } 
      return e;
    }
    
    public int size() {
      return this.array1.length + this.array2.length;
    }
  }
}
