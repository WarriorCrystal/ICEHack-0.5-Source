package javassist;

import javassist.bytecode.ClassFile;
import javassist.bytecode.InnerClassesAttribute;

class CtNewNestedClass extends CtNewClass {
  CtNewNestedClass(String paramString, ClassPool paramClassPool, boolean paramBoolean, CtClass paramCtClass) {
    super(paramString, paramClassPool, paramBoolean, paramCtClass);
  }
  
  public void setModifiers(int paramInt) {
    paramInt &= 0xFFFFFFF7;
    super.setModifiers(paramInt);
    updateInnerEntry(paramInt, getName(), this, true);
  }
  
  private static void updateInnerEntry(int paramInt, String paramString, CtClass paramCtClass, boolean paramBoolean) {
    ClassFile classFile = paramCtClass.getClassFile2();
    InnerClassesAttribute innerClassesAttribute = (InnerClassesAttribute)classFile.getAttribute("InnerClasses");
    if (innerClassesAttribute == null)
      return; 
    int i = innerClassesAttribute.tableLength();
    for (byte b = 0; b < i; b++) {
      if (paramString.equals(innerClassesAttribute.innerClass(b))) {
        int j = innerClassesAttribute.accessFlags(b) & 0x8;
        innerClassesAttribute.setAccessFlags(b, paramInt | j);
        String str = innerClassesAttribute.outerClass(b);
        if (str != null && paramBoolean)
          try {
            CtClass ctClass = paramCtClass.getClassPool().get(str);
            updateInnerEntry(paramInt, paramString, ctClass, false);
          } catch (NotFoundException notFoundException) {
            throw new RuntimeException("cannot find the declaring class: " + str);
          }  
        break;
      } 
    } 
  }
}
