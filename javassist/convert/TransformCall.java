package javassist.convert;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;

public class TransformCall extends Transformer {
  protected String classname;
  
  protected String methodname;
  
  protected String methodDescriptor;
  
  protected String newClassname;
  
  protected String newMethodname;
  
  protected boolean newMethodIsPrivate;
  
  protected int newIndex;
  
  protected ConstPool14 constPool;
  
  public TransformCall(Transformer paramTransformer, CtMethod paramCtMethod1, CtMethod paramCtMethod2) {
    this(paramTransformer, paramCtMethod1.getName(), paramCtMethod2);
    this.classname = paramCtMethod1.getDeclaringClass().getName();
  }
  
  public TransformCall(Transformer paramTransformer, String paramString, CtMethod paramCtMethod) {
    super(paramTransformer);
    this.methodname = paramString;
    this.methodDescriptor = paramCtMethod.getMethodInfo2().getDescriptor();
    this.classname = this.newClassname = paramCtMethod.getDeclaringClass().getName();
    this.newMethodname = paramCtMethod.getName();
    this.constPool = null;
    this.newMethodIsPrivate = Modifier.isPrivate(paramCtMethod.getModifiers());
  }
  
  public void initialize(ConstPool14 paramConstPool14, CodeAttribute paramCodeAttribute) {
    if (this.constPool != paramConstPool14)
      this.newIndex = 0; 
  }
  
  public int transform(CtClass paramCtClass, int paramInt, CodeIterator paramCodeIterator, ConstPool14 paramConstPool14) throws BadBytecode {
    int i = paramCodeIterator.byteAt(paramInt);
    if (i == 185 || i == 183 || i == 184 || i == 182) {
      int j = paramCodeIterator.u16bitAt(paramInt + 1);
      String str = paramConstPool14.eqMember(this.methodname, this.methodDescriptor, j);
      if (str != null && matchClass(str, paramCtClass.getClassPool())) {
        int k = paramConstPool14.getMemberNameAndType(j);
        paramInt = match(i, paramInt, paramCodeIterator, paramConstPool14
            .getNameAndTypeDescriptor(k), paramConstPool14);
      } 
    } 
    return paramInt;
  }
  
  private boolean matchClass(String paramString, ClassPool paramClassPool) {
    if (this.classname.equals(paramString))
      return true; 
    try {
      CtClass ctClass1 = paramClassPool.get(paramString);
      CtClass ctClass2 = paramClassPool.get(this.classname);
      if (ctClass1.subtypeOf(ctClass2))
        try {
          CtMethod ctMethod = ctClass1.getMethod(this.methodname, this.methodDescriptor);
          return ctMethod.getDeclaringClass().getName().equals(this.classname);
        } catch (NotFoundException notFoundException) {
          return true;
        }  
    } catch (NotFoundException notFoundException) {
      return false;
    } 
    return false;
  }
  
  protected int match(int paramInt1, int paramInt2, CodeIterator paramCodeIterator, int paramInt3, ConstPool14 paramConstPool14) throws BadBytecode {
    if (this.newIndex == 0) {
      int i = paramConstPool14.addNameAndTypeInfo(paramConstPool14.addUtf8Info(this.newMethodname), paramInt3);
      int j = paramConstPool14.addClassInfo(this.newClassname);
      if (paramInt1 == 185) {
        this.newIndex = paramConstPool14.addInterfaceMethodrefInfo(j, i);
      } else {
        if (this.newMethodIsPrivate && paramInt1 == 182)
          paramCodeIterator.writeByte(183, paramInt2); 
        this.newIndex = paramConstPool14.addMethodrefInfo(j, i);
      } 
      this.constPool = paramConstPool14;
    } 
    paramCodeIterator.write16bit(this.newIndex, paramInt2 + 1);
    return paramInt2;
  }
}
