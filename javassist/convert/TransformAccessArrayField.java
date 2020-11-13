package javassist.convert;

import javassist.CannotCompileException;
import javassist.CodeConverter;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.analysis.Analyzer;
import javassist.bytecode.analysis.Frame;

public final class TransformAccessArrayField extends Transformer {
  private final String methodClassname;
  
  private final CodeConverter.ArrayAccessReplacementMethodNames names;
  
  private Frame[] frames;
  
  private int offset;
  
  public TransformAccessArrayField(Transformer paramTransformer, String paramString, CodeConverter.ArrayAccessReplacementMethodNames paramArrayAccessReplacementMethodNames) throws NotFoundException {
    super(paramTransformer);
    this.methodClassname = paramString;
    this.names = paramArrayAccessReplacementMethodNames;
  }
  
  public void initialize(ConstPool14 paramConstPool14, CtClass paramCtClass, MethodInfo paramMethodInfo) throws CannotCompileException {
    CodeIterator codeIterator = paramMethodInfo.getCodeAttribute().iterator();
    while (codeIterator.hasNext()) {
      try {
        int i = codeIterator.next();
        int j = codeIterator.byteAt(i);
        if (j == 50)
          initFrames(paramCtClass, paramMethodInfo); 
        if (j == 50 || j == 51 || j == 52 || j == 49 || j == 48 || j == 46 || j == 47 || j == 53) {
          i = replace(paramConstPool14, codeIterator, i, j, getLoadReplacementSignature(j));
          continue;
        } 
        if (j == 83 || j == 84 || j == 85 || j == 82 || j == 81 || j == 79 || j == 80 || j == 86)
          i = replace(paramConstPool14, codeIterator, i, j, getStoreReplacementSignature(j)); 
      } catch (Exception exception) {
        throw new CannotCompileException(exception);
      } 
    } 
  }
  
  public void clean() {
    this.frames = null;
    this.offset = -1;
  }
  
  public int transform(CtClass paramCtClass, int paramInt, CodeIterator paramCodeIterator, ConstPool14 paramConstPool14) throws BadBytecode {
    return paramInt;
  }
  
  private Frame getFrame(int paramInt) throws BadBytecode {
    return this.frames[paramInt - this.offset];
  }
  
  private void initFrames(CtClass paramCtClass, MethodInfo paramMethodInfo) throws BadBytecode {
    if (this.frames == null) {
      this.frames = (new Analyzer()).analyze(paramCtClass, paramMethodInfo);
      this.offset = 0;
    } 
  }
  
  private int updatePos(int paramInt1, int paramInt2) {
    if (this.offset > -1)
      this.offset += paramInt2; 
    return paramInt1 + paramInt2;
  }
  
  private String getTopType(int paramInt) throws BadBytecode {
    Frame frame = getFrame(paramInt);
    if (frame == null)
      return null; 
    CtClass ctClass = frame.peek().getCtClass();
    return (ctClass != null) ? Descriptor.toJvmName(ctClass) : null;
  }
  
  private int replace(ConstPool14 paramConstPool14, CodeIterator paramCodeIterator, int paramInt1, int paramInt2, String paramString) throws BadBytecode {
    String str1 = null;
    String str2 = getMethodName(paramInt2);
    if (str2 != null) {
      if (paramInt2 == 50) {
        str1 = getTopType(paramCodeIterator.lookAhead());
        if (str1 == null)
          return paramInt1; 
        if ("java/lang/Object".equals(str1))
          str1 = null; 
      } 
      paramCodeIterator.writeByte(0, paramInt1);
      CodeIterator.Gap gap = paramCodeIterator.insertGapAt(paramInt1, (str1 != null) ? 5 : 2, false);
      paramInt1 = gap.position;
      int i = paramConstPool14.addClassInfo(this.methodClassname);
      int j = paramConstPool14.addMethodrefInfo(i, str2, paramString);
      paramCodeIterator.writeByte(184, paramInt1);
      paramCodeIterator.write16bit(j, paramInt1 + 1);
      if (str1 != null) {
        int k = paramConstPool14.addClassInfo(str1);
        paramCodeIterator.writeByte(192, paramInt1 + 3);
        paramCodeIterator.write16bit(k, paramInt1 + 4);
      } 
      paramInt1 = updatePos(paramInt1, gap.length);
    } 
    return paramInt1;
  }
  
  private String getMethodName(int paramInt) {
    String str = null;
    switch (paramInt) {
      case 50:
        str = this.names.objectRead();
        break;
      case 51:
        str = this.names.byteOrBooleanRead();
        break;
      case 52:
        str = this.names.charRead();
        break;
      case 49:
        str = this.names.doubleRead();
        break;
      case 48:
        str = this.names.floatRead();
        break;
      case 46:
        str = this.names.intRead();
        break;
      case 53:
        str = this.names.shortRead();
        break;
      case 47:
        str = this.names.longRead();
        break;
      case 83:
        str = this.names.objectWrite();
        break;
      case 84:
        str = this.names.byteOrBooleanWrite();
        break;
      case 85:
        str = this.names.charWrite();
        break;
      case 82:
        str = this.names.doubleWrite();
        break;
      case 81:
        str = this.names.floatWrite();
        break;
      case 79:
        str = this.names.intWrite();
        break;
      case 86:
        str = this.names.shortWrite();
        break;
      case 80:
        str = this.names.longWrite();
        break;
    } 
    if (str.equals(""))
      str = null; 
    return str;
  }
  
  private String getLoadReplacementSignature(int paramInt) throws BadBytecode {
    switch (paramInt) {
      case 50:
        return "(Ljava/lang/Object;I)Ljava/lang/Object;";
      case 51:
        return "(Ljava/lang/Object;I)B";
      case 52:
        return "(Ljava/lang/Object;I)C";
      case 49:
        return "(Ljava/lang/Object;I)D";
      case 48:
        return "(Ljava/lang/Object;I)F";
      case 46:
        return "(Ljava/lang/Object;I)I";
      case 53:
        return "(Ljava/lang/Object;I)S";
      case 47:
        return "(Ljava/lang/Object;I)J";
    } 
    throw new BadBytecode(paramInt);
  }
  
  private String getStoreReplacementSignature(int paramInt) throws BadBytecode {
    switch (paramInt) {
      case 83:
        return "(Ljava/lang/Object;ILjava/lang/Object;)V";
      case 84:
        return "(Ljava/lang/Object;IB)V";
      case 85:
        return "(Ljava/lang/Object;IC)V";
      case 82:
        return "(Ljava/lang/Object;ID)V";
      case 81:
        return "(Ljava/lang/Object;IF)V";
      case 79:
        return "(Ljava/lang/Object;II)V";
      case 86:
        return "(Ljava/lang/Object;IS)V";
      case 80:
        return "(Ljava/lang/Object;IJ)V";
    } 
    throw new BadBytecode(paramInt);
  }
}
