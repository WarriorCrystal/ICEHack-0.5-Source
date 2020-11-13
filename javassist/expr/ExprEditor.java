package javassist.expr;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;

public class ExprEditor {
  public boolean doit(CtClass paramCtClass, MethodInfo paramMethodInfo) throws CannotCompileException {
    CodeAttribute codeAttribute = paramMethodInfo.getCodeAttribute();
    if (codeAttribute == null)
      return false; 
    CodeIterator codeIterator = codeAttribute.iterator();
    boolean bool = false;
    LoopContext loopContext = new LoopContext(codeAttribute.getMaxLocals());
    while (codeIterator.hasNext()) {
      if (loopBody(codeIterator, paramCtClass, paramMethodInfo, loopContext))
        bool = true; 
    } 
    ExceptionTable exceptionTable = codeAttribute.getExceptionTable();
    int i = exceptionTable.size();
    for (byte b = 0; b < i; b++) {
      Handler handler = new Handler(exceptionTable, b, codeIterator, paramCtClass, paramMethodInfo);
      edit(handler);
      if (handler.edited()) {
        bool = true;
        loopContext.updateMax(handler.locals(), handler.stack());
      } 
    } 
    if (codeAttribute.getMaxLocals() < loopContext.maxLocals)
      codeAttribute.setMaxLocals(loopContext.maxLocals); 
    codeAttribute.setMaxStack(codeAttribute.getMaxStack() + loopContext.maxStack);
    try {
      if (bool)
        paramMethodInfo.rebuildStackMapIf6(paramCtClass.getClassPool(), paramCtClass
            .getClassFile2()); 
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode.getMessage(), badBytecode);
    } 
    return bool;
  }
  
  boolean doit(CtClass paramCtClass, MethodInfo paramMethodInfo, LoopContext paramLoopContext, CodeIterator paramCodeIterator, int paramInt) throws CannotCompileException {
    boolean bool = false;
    while (paramCodeIterator.hasNext() && paramCodeIterator.lookAhead() < paramInt) {
      int i = paramCodeIterator.getCodeLength();
      if (loopBody(paramCodeIterator, paramCtClass, paramMethodInfo, paramLoopContext)) {
        bool = true;
        int j = paramCodeIterator.getCodeLength();
        if (i != j)
          paramInt += j - i; 
      } 
    } 
    return bool;
  }
  
  static final class NewOp {
    NewOp next;
    
    int pos;
    
    String type;
    
    NewOp(NewOp param1NewOp, int param1Int, String param1String) {
      this.next = param1NewOp;
      this.pos = param1Int;
      this.type = param1String;
    }
  }
  
  static final class LoopContext {
    ExprEditor.NewOp newList;
    
    int maxLocals;
    
    int maxStack;
    
    LoopContext(int param1Int) {
      this.maxLocals = param1Int;
      this.maxStack = 0;
      this.newList = null;
    }
    
    void updateMax(int param1Int1, int param1Int2) {
      if (this.maxLocals < param1Int1)
        this.maxLocals = param1Int1; 
      if (this.maxStack < param1Int2)
        this.maxStack = param1Int2; 
    }
  }
  
  final boolean loopBody(CodeIterator paramCodeIterator, CtClass paramCtClass, MethodInfo paramMethodInfo, LoopContext paramLoopContext) throws CannotCompileException {
    try {
      Cast cast;
      MethodCall methodCall = null;
      int i = paramCodeIterator.next();
      int j = paramCodeIterator.byteAt(i);
      if (j >= 178)
        if (j < 188) {
          if (j == 184 || j == 185 || j == 182) {
            methodCall = new MethodCall(i, paramCodeIterator, paramCtClass, paramMethodInfo);
            edit(methodCall);
          } else if (j == 180 || j == 178 || j == 181 || j == 179) {
            FieldAccess fieldAccess = new FieldAccess(i, paramCodeIterator, paramCtClass, paramMethodInfo, j);
            edit(fieldAccess);
          } else if (j == 187) {
            int k = paramCodeIterator.u16bitAt(i + 1);
            paramLoopContext
              .newList = new NewOp(paramLoopContext.newList, i, paramMethodInfo.getConstPool().getClassInfo(k));
          } else if (j == 183) {
            NewOp newOp = paramLoopContext.newList;
            if (newOp != null && paramMethodInfo
              .getConstPool().isConstructor(newOp.type, paramCodeIterator
                .u16bitAt(i + 1)) > 0) {
              NewExpr newExpr = new NewExpr(i, paramCodeIterator, paramCtClass, paramMethodInfo, newOp.type, newOp.pos);
              edit(newExpr);
              paramLoopContext.newList = newOp.next;
            } else {
              MethodCall methodCall1 = new MethodCall(i, paramCodeIterator, paramCtClass, paramMethodInfo);
              if (methodCall1.getMethodName().equals("<init>")) {
                ConstructorCall constructorCall = new ConstructorCall(i, paramCodeIterator, paramCtClass, paramMethodInfo);
                methodCall = constructorCall;
                edit(constructorCall);
              } else {
                methodCall = methodCall1;
                edit(methodCall1);
              } 
            } 
          } 
        } else if (j == 188 || j == 189 || j == 197) {
          NewArray newArray = new NewArray(i, paramCodeIterator, paramCtClass, paramMethodInfo, j);
          edit(newArray);
        } else if (j == 193) {
          Instanceof instanceof_ = new Instanceof(i, paramCodeIterator, paramCtClass, paramMethodInfo);
          edit(instanceof_);
        } else if (j == 192) {
          cast = new Cast(i, paramCodeIterator, paramCtClass, paramMethodInfo);
          edit(cast);
        }  
      if (cast != null && cast.edited()) {
        paramLoopContext.updateMax(cast.locals(), cast.stack());
        return true;
      } 
      return false;
    } catch (BadBytecode badBytecode) {
      throw new CannotCompileException(badBytecode);
    } 
  }
  
  public void edit(NewExpr paramNewExpr) throws CannotCompileException {}
  
  public void edit(NewArray paramNewArray) throws CannotCompileException {}
  
  public void edit(MethodCall paramMethodCall) throws CannotCompileException {}
  
  public void edit(ConstructorCall paramConstructorCall) throws CannotCompileException {}
  
  public void edit(FieldAccess paramFieldAccess) throws CannotCompileException {}
  
  public void edit(Instanceof paramInstanceof) throws CannotCompileException {}
  
  public void edit(Cast paramCast) throws CannotCompileException {}
  
  public void edit(Handler paramHandler) throws CannotCompileException {}
}
