package javassist;

import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.MethodInfo;
import javassist.convert.TransformAccessArrayField;
import javassist.convert.TransformAfter;
import javassist.convert.TransformBefore;
import javassist.convert.TransformCall;
import javassist.convert.TransformFieldAccess;
import javassist.convert.TransformNew;
import javassist.convert.TransformNewClass;
import javassist.convert.TransformReadField;
import javassist.convert.TransformWriteField;
import javassist.convert.Transformer;

public class CodeConverter {
  protected Transformer transformers = null;
  
  public void replaceNew(CtClass paramCtClass1, CtClass paramCtClass2, String paramString) {
    this
      .transformers = (Transformer)new TransformNew(this.transformers, paramCtClass1.getName(), paramCtClass2.getName(), paramString);
  }
  
  public void replaceNew(CtClass paramCtClass1, CtClass paramCtClass2) {
    this
      .transformers = (Transformer)new TransformNewClass(this.transformers, paramCtClass1.getName(), paramCtClass2.getName());
  }
  
  public void redirectFieldAccess(CtField paramCtField, CtClass paramCtClass, String paramString) {
    this
      .transformers = (Transformer)new TransformFieldAccess(this.transformers, paramCtField, paramCtClass.getName(), paramString);
  }
  
  public void replaceFieldRead(CtField paramCtField, CtClass paramCtClass, String paramString) {
    this
      .transformers = (Transformer)new TransformReadField(this.transformers, paramCtField, paramCtClass.getName(), paramString);
  }
  
  public void replaceFieldWrite(CtField paramCtField, CtClass paramCtClass, String paramString) {
    this
      .transformers = (Transformer)new TransformWriteField(this.transformers, paramCtField, paramCtClass.getName(), paramString);
  }
  
  public void replaceArrayAccess(CtClass paramCtClass, ArrayAccessReplacementMethodNames paramArrayAccessReplacementMethodNames) throws NotFoundException {
    this.transformers = (Transformer)new TransformAccessArrayField(this.transformers, paramCtClass.getName(), paramArrayAccessReplacementMethodNames);
  }
  
  public void redirectMethodCall(CtMethod paramCtMethod1, CtMethod paramCtMethod2) throws CannotCompileException {
    String str1 = paramCtMethod1.getMethodInfo2().getDescriptor();
    String str2 = paramCtMethod2.getMethodInfo2().getDescriptor();
    if (!str1.equals(str2))
      throw new CannotCompileException("signature mismatch: " + paramCtMethod2
          .getLongName()); 
    int i = paramCtMethod1.getModifiers();
    int j = paramCtMethod2.getModifiers();
    if (Modifier.isStatic(i) != Modifier.isStatic(j) || (
      Modifier.isPrivate(i) && !Modifier.isPrivate(j)) || paramCtMethod1
      .getDeclaringClass().isInterface() != paramCtMethod2
      .getDeclaringClass().isInterface())
      throw new CannotCompileException("invoke-type mismatch " + paramCtMethod2
          .getLongName()); 
    this.transformers = (Transformer)new TransformCall(this.transformers, paramCtMethod1, paramCtMethod2);
  }
  
  public void redirectMethodCall(String paramString, CtMethod paramCtMethod) throws CannotCompileException {
    this.transformers = (Transformer)new TransformCall(this.transformers, paramString, paramCtMethod);
  }
  
  public void insertBeforeMethod(CtMethod paramCtMethod1, CtMethod paramCtMethod2) throws CannotCompileException {
    try {
      this.transformers = (Transformer)new TransformBefore(this.transformers, paramCtMethod1, paramCtMethod2);
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } 
  }
  
  public void insertAfterMethod(CtMethod paramCtMethod1, CtMethod paramCtMethod2) throws CannotCompileException {
    try {
      this.transformers = (Transformer)new TransformAfter(this.transformers, paramCtMethod1, paramCtMethod2);
    } catch (NotFoundException notFoundException) {
      throw new CannotCompileException(notFoundException);
    } 
  }
  
  protected void doit(CtClass paramCtClass, MethodInfo paramMethodInfo, ConstPool14 paramConstPool14) throws CannotCompileException {
    CodeAttribute codeAttribute = paramMethodInfo.getCodeAttribute();
    if (codeAttribute == null || this.transformers == null)
      return; 
    Transformer transformer;
    for (transformer = this.transformers; transformer != null; transformer = transformer.getNext())
      transformer.initialize(paramConstPool14, paramCtClass, paramMethodInfo); 
    CodeIterator codeIterator = codeAttribute.iterator();
    while (codeIterator.hasNext()) {
      try {
        int k = codeIterator.next();
        for (transformer = this.transformers; transformer != null; transformer = transformer.getNext())
          k = transformer.transform(paramCtClass, k, codeIterator, paramConstPool14); 
      } catch (BadBytecode badBytecode) {
        throw new CannotCompileException(badBytecode);
      } 
    } 
    int i = 0;
    int j = 0;
    for (transformer = this.transformers; transformer != null; transformer = transformer.getNext()) {
      int k = transformer.extraLocals();
      if (k > i)
        i = k; 
      k = transformer.extraStack();
      if (k > j)
        j = k; 
    } 
    for (transformer = this.transformers; transformer != null; transformer = transformer.getNext())
      transformer.clean(); 
    if (i > 0)
      codeAttribute.setMaxLocals(codeAttribute.getMaxLocals() + i); 
    if (j > 0)
      codeAttribute.setMaxStack(codeAttribute.getMaxStack() + j); 
    try {
      paramMethodInfo.rebuildStackMapIf6(paramCtClass.getClassPool(), paramCtClass
          .getClassFile2());
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode.getMessage(), badBytecode);
    } 
  }
  
  public static class DefaultArrayAccessReplacementMethodNames implements ArrayAccessReplacementMethodNames {
    public String byteOrBooleanRead() {
      return "arrayReadByteOrBoolean";
    }
    
    public String byteOrBooleanWrite() {
      return "arrayWriteByteOrBoolean";
    }
    
    public String charRead() {
      return "arrayReadChar";
    }
    
    public String charWrite() {
      return "arrayWriteChar";
    }
    
    public String doubleRead() {
      return "arrayReadDouble";
    }
    
    public String doubleWrite() {
      return "arrayWriteDouble";
    }
    
    public String floatRead() {
      return "arrayReadFloat";
    }
    
    public String floatWrite() {
      return "arrayWriteFloat";
    }
    
    public String intRead() {
      return "arrayReadInt";
    }
    
    public String intWrite() {
      return "arrayWriteInt";
    }
    
    public String longRead() {
      return "arrayReadLong";
    }
    
    public String longWrite() {
      return "arrayWriteLong";
    }
    
    public String objectRead() {
      return "arrayReadObject";
    }
    
    public String objectWrite() {
      return "arrayWriteObject";
    }
    
    public String shortRead() {
      return "arrayReadShort";
    }
    
    public String shortWrite() {
      return "arrayWriteShort";
    }
  }
  
  public static interface ArrayAccessReplacementMethodNames {
    String byteOrBooleanRead();
    
    String byteOrBooleanWrite();
    
    String charRead();
    
    String charWrite();
    
    String doubleRead();
    
    String doubleWrite();
    
    String floatRead();
    
    String floatWrite();
    
    String intRead();
    
    String intWrite();
    
    String longRead();
    
    String longWrite();
    
    String objectRead();
    
    String objectWrite();
    
    String shortRead();
    
    String shortWrite();
  }
}
