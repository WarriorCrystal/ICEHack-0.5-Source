package javassist.compiler;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.CallExpr;
import javassist.compiler.ast.CastExpr;
import javassist.compiler.ast.Expr;
import javassist.compiler.ast.Member;
import javassist.compiler.ast.Symbol;

public class JvstTypeChecker extends TypeChecker {
  private JvstCodeGen codeGen;
  
  public JvstTypeChecker(CtClass paramCtClass, ClassPool paramClassPool, JvstCodeGen paramJvstCodeGen) {
    super(paramCtClass, paramClassPool);
    this.codeGen = paramJvstCodeGen;
  }
  
  public void addNullIfVoid() {
    if (this.exprType == 344) {
      this.exprType = 307;
      this.arrayDim = 0;
      this.className = "java/lang/Object";
    } 
  }
  
  public void atMember(Member paramMember) throws CompileError {
    String str = paramMember.get();
    if (str.equals(this.codeGen.paramArrayName)) {
      this.exprType = 307;
      this.arrayDim = 1;
      this.className = "java/lang/Object";
    } else if (str.equals("$sig")) {
      this.exprType = 307;
      this.arrayDim = 1;
      this.className = "java/lang/Class";
    } else if (str.equals("$type") || str
      .equals("$class")) {
      this.exprType = 307;
      this.arrayDim = 0;
      this.className = "java/lang/Class";
    } else {
      super.atMember(paramMember);
    } 
  }
  
  protected void atFieldAssign(Expr paramExpr, int paramInt, ASTree paramASTree1, ASTree paramASTree2) throws CompileError {
    if (paramASTree1 instanceof Member && ((Member)paramASTree1)
      .get().equals(this.codeGen.paramArrayName)) {
      paramASTree2.accept(this);
      CtClass[] arrayOfCtClass = this.codeGen.paramTypeList;
      if (arrayOfCtClass == null)
        return; 
      int i = arrayOfCtClass.length;
      for (byte b = 0; b < i; b++)
        compileUnwrapValue(arrayOfCtClass[b]); 
    } else {
      super.atFieldAssign(paramExpr, paramInt, paramASTree1, paramASTree2);
    } 
  }
  
  public void atCastExpr(CastExpr paramCastExpr) throws CompileError {
    ASTList aSTList = paramCastExpr.getClassName();
    if (aSTList != null && paramCastExpr.getArrayDim() == 0) {
      ASTree aSTree = aSTList.head();
      if (aSTree instanceof Symbol && aSTList.tail() == null) {
        String str = ((Symbol)aSTree).get();
        if (str.equals(this.codeGen.returnCastName)) {
          atCastToRtype(paramCastExpr);
          return;
        } 
        if (str.equals("$w")) {
          atCastToWrapper(paramCastExpr);
          return;
        } 
      } 
    } 
    super.atCastExpr(paramCastExpr);
  }
  
  protected void atCastToRtype(CastExpr paramCastExpr) throws CompileError {
    CtClass ctClass = this.codeGen.returnType;
    paramCastExpr.getOprand().accept(this);
    if (this.exprType == 344 || CodeGen.isRefType(this.exprType) || this.arrayDim > 0) {
      compileUnwrapValue(ctClass);
    } else if (ctClass instanceof CtPrimitiveType) {
      CtPrimitiveType ctPrimitiveType = (CtPrimitiveType)ctClass;
      int i = MemberResolver.descToType(ctPrimitiveType.getDescriptor());
      this.exprType = i;
      this.arrayDim = 0;
      this.className = null;
    } 
  }
  
  protected void atCastToWrapper(CastExpr paramCastExpr) throws CompileError {
    paramCastExpr.getOprand().accept(this);
    if (CodeGen.isRefType(this.exprType) || this.arrayDim > 0)
      return; 
    CtClass ctClass = this.resolver.lookupClass(this.exprType, this.arrayDim, this.className);
    if (ctClass instanceof CtPrimitiveType) {
      this.exprType = 307;
      this.arrayDim = 0;
      this.className = "java/lang/Object";
    } 
  }
  
  public void atCallExpr(CallExpr paramCallExpr) throws CompileError {
    ASTree aSTree = paramCallExpr.oprand1();
    if (aSTree instanceof Member) {
      String str = ((Member)aSTree).get();
      if (this.codeGen.procHandler != null && str
        .equals(this.codeGen.proceedName)) {
        this.codeGen.procHandler.setReturnType(this, (ASTList)paramCallExpr
            .oprand2());
        return;
      } 
      if (str.equals("$cflow")) {
        atCflow((ASTList)paramCallExpr.oprand2());
        return;
      } 
    } 
    super.atCallExpr(paramCallExpr);
  }
  
  protected void atCflow(ASTList paramASTList) throws CompileError {
    this.exprType = 324;
    this.arrayDim = 0;
    this.className = null;
  }
  
  public boolean isParamListName(ASTList paramASTList) {
    if (this.codeGen.paramTypeList != null && paramASTList != null && paramASTList
      .tail() == null) {
      ASTree aSTree = paramASTList.head();
      return (aSTree instanceof Member && ((Member)aSTree)
        .get().equals(this.codeGen.paramListName));
    } 
    return false;
  }
  
  public int getMethodArgsLength(ASTList paramASTList) {
    String str = this.codeGen.paramListName;
    int i = 0;
    while (paramASTList != null) {
      ASTree aSTree = paramASTList.head();
      if (aSTree instanceof Member && ((Member)aSTree).get().equals(str)) {
        if (this.codeGen.paramTypeList != null)
          i += this.codeGen.paramTypeList.length; 
      } else {
        i++;
      } 
      paramASTList = paramASTList.tail();
    } 
    return i;
  }
  
  public void atMethodArgs(ASTList paramASTList, int[] paramArrayOfint1, int[] paramArrayOfint2, String[] paramArrayOfString) throws CompileError {
    CtClass[] arrayOfCtClass = this.codeGen.paramTypeList;
    String str = this.codeGen.paramListName;
    byte b = 0;
    while (paramASTList != null) {
      ASTree aSTree = paramASTList.head();
      if (aSTree instanceof Member && ((Member)aSTree).get().equals(str)) {
        if (arrayOfCtClass != null) {
          int i = arrayOfCtClass.length;
          for (byte b1 = 0; b1 < i; b1++) {
            CtClass ctClass = arrayOfCtClass[b1];
            setType(ctClass);
            paramArrayOfint1[b] = this.exprType;
            paramArrayOfint2[b] = this.arrayDim;
            paramArrayOfString[b] = this.className;
            b++;
          } 
        } 
      } else {
        aSTree.accept(this);
        paramArrayOfint1[b] = this.exprType;
        paramArrayOfint2[b] = this.arrayDim;
        paramArrayOfString[b] = this.className;
        b++;
      } 
      paramASTList = paramASTList.tail();
    } 
  }
  
  void compileInvokeSpecial(ASTree paramASTree, String paramString1, String paramString2, String paramString3, ASTList paramASTList) throws CompileError {
    paramASTree.accept(this);
    int i = getMethodArgsLength(paramASTList);
    atMethodArgs(paramASTList, new int[i], new int[i], new String[i]);
    setReturnType(paramString3);
    addNullIfVoid();
  }
  
  protected void compileUnwrapValue(CtClass paramCtClass) throws CompileError {
    if (paramCtClass == CtClass.voidType) {
      addNullIfVoid();
    } else {
      setType(paramCtClass);
    } 
  }
  
  public void setType(CtClass paramCtClass) throws CompileError {
    setType(paramCtClass, 0);
  }
  
  private void setType(CtClass paramCtClass, int paramInt) throws CompileError {
    if (paramCtClass.isPrimitive()) {
      CtPrimitiveType ctPrimitiveType = (CtPrimitiveType)paramCtClass;
      this.exprType = MemberResolver.descToType(ctPrimitiveType.getDescriptor());
      this.arrayDim = paramInt;
      this.className = null;
    } else if (paramCtClass.isArray()) {
      try {
        setType(paramCtClass.getComponentType(), paramInt + 1);
      } catch (NotFoundException notFoundException) {
        throw new CompileError("undefined type: " + paramCtClass.getName());
      } 
    } else {
      this.exprType = 307;
      this.arrayDim = paramInt;
      this.className = MemberResolver.javaToJvmName(paramCtClass.getName());
    } 
  }
}
