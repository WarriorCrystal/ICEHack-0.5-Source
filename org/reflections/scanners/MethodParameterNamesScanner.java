package org.reflections.scanners;

import com.google.common.base.Joiner;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.reflections.adapters.MetadataAdapter;

public class MethodParameterNamesScanner extends AbstractScanner {
  public void scan(Object paramObject) {
    MetadataAdapter metadataAdapter = getMetadataAdapter();
    for (MethodInfo methodInfo : metadataAdapter.getMethods(paramObject)) {
      String str = metadataAdapter.getMethodFullKey(paramObject, methodInfo);
      if (acceptResult(str)) {
        LocalVariableAttribute localVariableAttribute = (LocalVariableAttribute)((MethodInfo)methodInfo).getCodeAttribute().getAttribute("LocalVariableTable");
        int i = localVariableAttribute.tableLength();
        byte b = Modifier.isStatic(methodInfo.getAccessFlags()) ? 0 : 1;
        if (b < i) {
          ArrayList<String> arrayList = new ArrayList(i - b);
          for (; b < i; arrayList.add(methodInfo.getConstPool().getUtf8Info(localVariableAttribute.nameIndex(b++))));
          getStore().put(str, Joiner.on(", ").join(arrayList));
        } 
      } 
    } 
  }
}
