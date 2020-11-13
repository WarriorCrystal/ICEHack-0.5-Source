package javassist;

import java.io.DataOutputStream;
import java.io.IOException;
import javassist.bytecode.ClassFile;

class CtNewClass extends CtClassType1 {
  protected boolean hasConstructor;
  
  CtNewClass(String paramString, ClassPool paramClassPool, boolean paramBoolean, CtClass paramCtClass) {
    super(paramString, paramClassPool);
    String str;
    this.wasChanged = true;
    if (paramBoolean || paramCtClass == null) {
      str = null;
    } else {
      str = paramCtClass.getName();
    } 
    this.classfile = new ClassFile(paramBoolean, paramString, str);
    if (paramBoolean && paramCtClass != null)
      this.classfile.setInterfaces(new String[] { paramCtClass.getName() }); 
    setModifiers(Modifier.setPublic(getModifiers()));
    this.hasConstructor = paramBoolean;
  }
  
  protected void extendToString(StringBuffer paramStringBuffer) {
    if (this.hasConstructor)
      paramStringBuffer.append("hasConstructor "); 
    super.extendToString(paramStringBuffer);
  }
  
  public void addConstructor(CtConstructor paramCtConstructor) throws CannotCompileException {
    this.hasConstructor = true;
    super.addConstructor(paramCtConstructor);
  }
  
  public void toBytecode(DataOutputStream paramDataOutputStream) throws CannotCompileException, IOException {
    if (!this.hasConstructor)
      try {
        inheritAllConstructors();
        this.hasConstructor = true;
      } catch (NotFoundException notFoundException) {
        throw new CannotCompileException(notFoundException);
      }  
    super.toBytecode(paramDataOutputStream);
  }
  
  public void inheritAllConstructors() throws CannotCompileException, NotFoundException {
    CtClass ctClass = getSuperclass();
    CtConstructor[] arrayOfCtConstructor = ctClass.getDeclaredConstructors();
    byte b1 = 0;
    for (byte b2 = 0; b2 < arrayOfCtConstructor.length; b2++) {
      CtConstructor ctConstructor = arrayOfCtConstructor[b2];
      int i = ctConstructor.getModifiers();
      if (isInheritable(i, ctClass)) {
        CtConstructor ctConstructor1 = CtNewConstructor.make(ctConstructor.getParameterTypes(), ctConstructor
            .getExceptionTypes(), this);
        ctConstructor1.setModifiers(i & 0x7);
        addConstructor(ctConstructor1);
        b1++;
      } 
    } 
    if (b1 < 1)
      throw new CannotCompileException("no inheritable constructor in " + ctClass
          .getName()); 
  }
  
  private boolean isInheritable(int paramInt, CtClass paramCtClass) {
    if (Modifier.isPrivate(paramInt))
      return false; 
    if (Modifier.isPackage(paramInt)) {
      String str1 = getPackageName();
      String str2 = paramCtClass.getPackageName();
      if (str1 == null)
        return (str2 == null); 
      return str1.equals(str2);
    } 
    return true;
  }
}
