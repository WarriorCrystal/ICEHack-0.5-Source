package org.reflections.adapters;

import java.util.List;
import org.reflections.vfs.Vfs;

public interface MetadataAdapter<C, F, M> {
  String getClassName(C paramC);
  
  String getSuperclassName(C paramC);
  
  List<String> getInterfacesNames(C paramC);
  
  List<F> getFields(C paramC);
  
  List<M> getMethods(C paramC);
  
  String getMethodName(M paramM);
  
  List<String> getParameterNames(M paramM);
  
  List<String> getClassAnnotationNames(C paramC);
  
  List<String> getFieldAnnotationNames(F paramF);
  
  List<String> getMethodAnnotationNames(M paramM);
  
  List<String> getParameterAnnotationNames(M paramM, int paramInt);
  
  String getReturnTypeName(M paramM);
  
  String getFieldName(F paramF);
  
  C getOfCreateClassObject(Vfs.File paramFile) throws Exception;
  
  String getMethodModifier(M paramM);
  
  String getMethodKey(C paramC, M paramM);
  
  String getMethodFullKey(C paramC, M paramM);
  
  boolean isPublic(Object paramObject);
  
  boolean acceptsInput(String paramString);
}
