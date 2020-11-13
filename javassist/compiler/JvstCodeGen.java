package javassist.compiler;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.Descriptor;
import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.CallExpr;
import javassist.compiler.ast.CastExpr;
import javassist.compiler.ast.Declarator;
import javassist.compiler.ast.Expr;
import javassist.compiler.ast.Member;
import javassist.compiler.ast.Stmnt;
import javassist.compiler.ast.Symbol;

public class JvstCodeGen extends MemberCodeGen {
  String paramArrayName = null;
  
  String paramListName = null;
  
  CtClass[] paramTypeList = null;
  
  private int paramVarBase = 0;
  
  private boolean useParam0 = false;
  
  private String param0Type = null;
  
  public static final String sigName = "$sig";
  
  public static final String dollarTypeName = "$type";
  
  public static final String clazzName = "$class";
  
  private CtClass dollarType = null;
  
  CtClass returnType = null;
  
  String returnCastName = null;
  
  private String returnVarName = null;
  
  public static final String wrapperCastName = "$w";
  
  String proceedName = null;
  
  public static final String cflowName = "$cflow";
  
  ProceedHandler procHandler = null;
  
  public JvstCodeGen(Bytecode1 paramBytecode1, CtClass paramCtClass, ClassPool paramClassPool) {
    super(paramBytecode1, paramCtClass, paramClassPool);
    setTypeChecker(new JvstTypeChecker(paramCtClass, paramClassPool, this));
  }
  
  private int indexOfParam1() {
    return this.paramVarBase + (this.useParam0 ? 1 : 0);
  }
  
  public void setProceedHandler(ProceedHandler paramProceedHandler, String paramString) {
    this.proceedName = paramString;
    this.procHandler = paramProceedHandler;
  }
  
  public void addNullIfVoid() {
    if (this.exprType == 344) {
      this.bytecode.addOpcode(1);
      this.exprType = 307;
      this.arrayDim = 0;
      this.className = "java/lang/Object";
    } 
  }
  
  public void atMember(Member paramMember) throws CompileError {
    String str = paramMember.get();
    if (str.equals(this.paramArrayName)) {
      compileParameterList(this.bytecode, this.paramTypeList, indexOfParam1());
      this.exprType = 307;
      this.arrayDim = 1;
      this.className = "java/lang/Object";
    } else if (str.equals("$sig")) {
      this.bytecode.addLdc(Descriptor.ofMethod(this.returnType, this.paramTypeList));
      this.bytecode.addInvokestatic("javassist/runtime/Desc", "getParams", "(Ljava/lang/String;)[Ljava/lang/Class;");
      this.exprType = 307;
      this.arrayDim = 1;
      this.className = "java/lang/Class";
    } else if (str.equals("$type")) {
      if (this.dollarType == null)
        throw new CompileError("$type is not available"); 
      this.bytecode.addLdc(Descriptor.of(this.dollarType));
      callGetType("getType");
    } else if (str.equals("$class")) {
      if (this.param0Type == null)
        throw new CompileError("$class is not available"); 
      this.bytecode.addLdc(this.param0Type);
      callGetType("getClazz");
    } else {
      super.atMember(paramMember);
    } 
  }
  
  private void callGetType(String paramString) {
    this.bytecode.addInvokestatic("javassist/runtime/Desc", paramString, "(Ljava/lang/String;)Ljava/lang/Class;");
    this.exprType = 307;
    this.arrayDim = 0;
    this.className = "java/lang/Class";
  }
  
  protected void atFieldAssign(Expr paramExpr, int paramInt, ASTree paramASTree1, ASTree paramASTree2, boolean paramBoolean) throws CompileError {
    if (paramASTree1 instanceof Member && ((Member)paramASTree1)
      .get().equals(this.paramArrayName)) {
      if (paramInt != 61)
        throw new CompileError("bad operator for " + this.paramArrayName); 
      paramASTree2.accept(this);
      if (this.arrayDim != 1 || this.exprType != 307)
        throw new CompileError("invalid type for " + this.paramArrayName); 
      atAssignParamList(this.paramTypeList, this.bytecode);
      if (!paramBoolean)
        this.bytecode.addOpcode(87); 
    } else {
      super.atFieldAssign(paramExpr, paramInt, paramASTree1, paramASTree2, paramBoolean);
    } 
  }
  
  protected void atAssignParamList(CtClass[] paramArrayOfCtClass, Bytecode1 paramBytecode1) throws CompileError {
    if (paramArrayOfCtClass == null)
      return; 
    int i = indexOfParam1();
    int j = paramArrayOfCtClass.length;
    for (byte b = 0; b < j; b++) {
      paramBytecode1.addOpcode(89);
      paramBytecode1.addIconst(b);
      paramBytecode1.addOpcode(50);
      compileUnwrapValue(paramArrayOfCtClass[b], paramBytecode1);
      paramBytecode1.addStore(i, paramArrayOfCtClass[b]);
      i += is2word(this.exprType, this.arrayDim) ? 2 : 1;
    } 
  }
  
  public void atCastExpr(CastExpr paramCastExpr) throws CompileError {
    ASTList aSTList = paramCastExpr.getClassName();
    if (aSTList != null && paramCastExpr.getArrayDim() == 0) {
      ASTree aSTree = aSTList.head();
      if (aSTree instanceof Symbol && aSTList.tail() == null) {
        String str = ((Symbol)aSTree).get();
        if (str.equals(this.returnCastName)) {
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
    paramCastExpr.getOprand().accept(this);
    if (this.exprType == 344 || isRefType(this.exprType) || this.arrayDim > 0) {
      compileUnwrapValue(this.returnType, this.bytecode);
    } else if (this.returnType instanceof CtPrimitiveType) {
      CtPrimitiveType ctPrimitiveType = (CtPrimitiveType)this.returnType;
      int i = MemberResolver.descToType(ctPrimitiveType.getDescriptor());
      atNumCastExpr(this.exprType, i);
      this.exprType = i;
      this.arrayDim = 0;
      this.className = null;
    } else {
      throw new CompileError("invalid cast");
    } 
  }
  
  protected void atCastToWrapper(CastExpr paramCastExpr) throws CompileError {
    paramCastExpr.getOprand().accept(this);
    if (isRefType(this.exprType) || this.arrayDim > 0)
      return; 
    CtClass ctClass = this.resolver.lookupClass(this.exprType, this.arrayDim, this.className);
    if (ctClass instanceof CtPrimitiveType) {
      CtPrimitiveType ctPrimitiveType = (CtPrimitiveType)ctClass;
      String str = ctPrimitiveType.getWrapperName();
      this.bytecode.addNew(str);
      this.bytecode.addOpcode(89);
      if (ctPrimitiveType.getDataSize() > 1) {
        this.bytecode.addOpcode(94);
      } else {
        this.bytecode.addOpcode(93);
      } 
      this.bytecode.addOpcode(88);
      this.bytecode.addInvokespecial(str, "<init>", "(" + ctPrimitiveType
          .getDescriptor() + ")V");
      this.exprType = 307;
      this.arrayDim = 0;
      this.className = "java/lang/Object";
    } 
  }
  
  public void atCallExpr(CallExpr paramCallExpr) throws CompileError {
    ASTree aSTree = paramCallExpr.oprand1();
    if (aSTree instanceof Member) {
      String str = ((Member)aSTree).get();
      if (this.procHandler != null && str.equals(this.proceedName)) {
        this.procHandler.doit(this, this.bytecode, (ASTList)paramCallExpr.oprand2());
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
    StringBuffer stringBuffer = new StringBuffer();
    if (paramASTList == null || paramASTList.tail() != null)
      throw new CompileError("bad $cflow"); 
    makeCflowName(stringBuffer, paramASTList.head());
    String str = stringBuffer.toString();
    Object[] arrayOfObject = this.resolver.getClassPool().lookupCflow(str);
    if (arrayOfObject == null)
      throw new CompileError("no such $cflow: " + str); 
    this.bytecode.addGetstatic((String)arrayOfObject[0], (String)arrayOfObject[1], "Ljavassist/runtime/Cflow;");
    this.bytecode.addInvokevirtual("javassist.runtime.Cflow", "value", "()I");
    this.exprType = 324;
    this.arrayDim = 0;
    this.className = null;
  }
  
  private static void makeCflowName(StringBuffer paramStringBuffer, ASTree paramASTree) throws CompileError {
    if (paramASTree instanceof Symbol) {
      paramStringBuffer.append(((Symbol)paramASTree).get());
      return;
    } 
    if (paramASTree instanceof Expr) {
      Expr expr = (Expr)paramASTree;
      if (expr.getOperator() == 46) {
        makeCflowName(paramStringBuffer, expr.oprand1());
        paramStringBuffer.append('.');
        makeCflowName(paramStringBuffer, expr.oprand2());
        return;
      } 
    } 
    throw new CompileError("bad $cflow");
  }
  
  public boolean isParamListName(ASTList paramASTList) {
    if (this.paramTypeList != null && paramASTList != null && paramASTList
      .tail() == null) {
      ASTree aSTree = paramASTList.head();
      return (aSTree instanceof Member && ((Member)aSTree)
        .get().equals(this.paramListName));
    } 
    return false;
  }
  
  public int getMethodArgsLength(ASTList paramASTList) {
    String str = this.paramListName;
    int i = 0;
    while (paramASTList != null) {
      ASTree aSTree = paramASTList.head();
      if (aSTree instanceof Member && ((Member)aSTree).get().equals(str)) {
        if (this.paramTypeList != null)
          i += this.paramTypeList.length; 
      } else {
        i++;
      } 
      paramASTList = paramASTList.tail();
    } 
    return i;
  }
  
  public void atMethodArgs(ASTList paramASTList, int[] paramArrayOfint1, int[] paramArrayOfint2, String[] paramArrayOfString) throws CompileError {
    CtClass[] arrayOfCtClass = this.paramTypeList;
    String str = this.paramListName;
    byte b = 0;
    while (paramASTList != null) {
      ASTree aSTree = paramASTList.head();
      if (aSTree instanceof Member && ((Member)aSTree).get().equals(str)) {
        if (arrayOfCtClass != null) {
          int i = arrayOfCtClass.length;
          int j = indexOfParam1();
          for (byte b1 = 0; b1 < i; b1++) {
            CtClass ctClass = arrayOfCtClass[b1];
            j += this.bytecode.addLoad(j, ctClass);
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
  
  void compileInvokeSpecial(ASTree paramASTree, int paramInt, String paramString, ASTList paramASTList) throws CompileError {
    paramASTree.accept(this);
    int i = getMethodArgsLength(paramASTList);
    atMethodArgs(paramASTList, new int[i], new int[i], new String[i]);
    this.bytecode.addInvokespecial(paramInt, paramString);
    setReturnType(paramString, false, false);
    addNullIfVoid();
  }
  
  protected void atReturnStmnt(Stmnt paramStmnt) throws CompileError {
    ASTree aSTree = paramStmnt.getLeft();
    if (aSTree != null && this.returnType == CtClass.voidType) {
      compileExpr(aSTree);
      if (is2word(this.exprType, this.arrayDim)) {
        this.bytecode.addOpcode(88);
      } else if (this.exprType != 344) {
        this.bytecode.addOpcode(87);
      } 
      aSTree = null;
    } 
    atReturnStmnt2(aSTree);
  }
  
  public int recordReturnType(CtClass paramCtClass, String paramString1, String paramString2, SymbolTable paramSymbolTable) throws CompileError {
    this.returnType = paramCtClass;
    this.returnCastName = paramString1;
    this.returnVarName = paramString2;
    if (paramString2 == null)
      return -1; 
    int i = getMaxLocals();
    int j = i + recordVar(paramCtClass, paramString2, i, paramSymbolTable);
    setMaxLocals(j);
    return i;
  }
  
  public void recordType(CtClass paramCtClass) {
    this.dollarType = paramCtClass;
  }
  
  public int recordParams(CtClass[] paramArrayOfCtClass, boolean paramBoolean, String paramString1, String paramString2, String paramString3, SymbolTable paramSymbolTable) throws CompileError {
    return recordParams(paramArrayOfCtClass, paramBoolean, paramString1, paramString2, paramString3, !paramBoolean, 0, 
        getThisName(), paramSymbolTable);
  }
  
  public int recordParams(CtClass[] paramArrayOfCtClass, boolean paramBoolean1, String paramString1, String paramString2, String paramString3, boolean paramBoolean2, int paramInt, String paramString4, SymbolTable paramSymbolTable) throws CompileError {
    this.paramTypeList = paramArrayOfCtClass;
    this.paramArrayName = paramString2;
    this.paramListName = paramString3;
    this.paramVarBase = paramInt;
    this.useParam0 = paramBoolean2;
    if (paramString4 != null)
      this.param0Type = MemberResolver.jvmToJavaName(paramString4); 
    this.inStaticMethod = paramBoolean1;
    int i = paramInt;
    if (paramBoolean2) {
      String str = paramString1 + "0";
      Declarator declarator = new Declarator(307, MemberResolver.javaToJvmName(paramString4), 0, i++, new Symbol(str));
      paramSymbolTable.append(str, declarator);
    } 
    for (byte b = 0; b < paramArrayOfCtClass.length; b++)
      i += recordVar(paramArrayOfCtClass[b], paramString1 + (b + 1), i, paramSymbolTable); 
    if (getMaxLocals() < i)
      setMaxLocals(i); 
    return i;
  }
  
  public int recordVariable(CtClass paramCtClass, String paramString, SymbolTable paramSymbolTable) throws CompileError {
    if (paramString == null)
      return -1; 
    int i = getMaxLocals();
    int j = i + recordVar(paramCtClass, paramString, i, paramSymbolTable);
    setMaxLocals(j);
    return i;
  }
  
  private int recordVar(CtClass paramCtClass, String paramString, int paramInt, SymbolTable paramSymbolTable) throws CompileError {
    if (paramCtClass == CtClass.voidType) {
      this.exprType = 307;
      this.arrayDim = 0;
      this.className = "java/lang/Object";
    } else {
      setType(paramCtClass);
    } 
    Declarator declarator = new Declarator(this.exprType, this.className, this.arrayDim, paramInt, new Symbol(paramString));
    paramSymbolTable.append(paramString, declarator);
    return is2word(this.exprType, this.arrayDim) ? 2 : 1;
  }
  
  public void recordVariable(String paramString1, String paramString2, int paramInt, SymbolTable paramSymbolTable) throws CompileError {
    byte b = 0;
    char c;
    while ((c = paramString1.charAt(b)) == '[')
      b++; 
    int i = MemberResolver.descToType(c);
    String str = null;
    if (i == 307)
      if (b == 0) {
        str = paramString1.substring(1, paramString1.length() - 1);
      } else {
        str = paramString1.substring(b + 1, paramString1.length() - 1);
      }  
    Declarator declarator = new Declarator(i, str, b, paramInt, new Symbol(paramString2));
    paramSymbolTable.append(paramString2, declarator);
  }
  
  public static int compileParameterList(Bytecode1 paramBytecode1, CtClass[] paramArrayOfCtClass, int paramInt) {
    if (paramArrayOfCtClass == null) {
      paramBytecode1.addIconst(0);
      paramBytecode1.addAnewarray("java.lang.Object");
      return 1;
    } 
    CtClass[] arrayOfCtClass = new CtClass[1];
    int i = paramArrayOfCtClass.length;
    paramBytecode1.addIconst(i);
    paramBytecode1.addAnewarray("java.lang.Object");
    for (byte b = 0; b < i; b++) {
      paramBytecode1.addOpcode(89);
      paramBytecode1.addIconst(b);
      if (paramArrayOfCtClass[b].isPrimitive()) {
        CtPrimitiveType ctPrimitiveType = (CtPrimitiveType)paramArrayOfCtClass[b];
        String str = ctPrimitiveType.getWrapperName();
        paramBytecode1.addNew(str);
        paramBytecode1.addOpcode(89);
        int j = paramBytecode1.addLoad(paramInt, (CtClass)ctPrimitiveType);
        paramInt += j;
        arrayOfCtClass[0] = (CtClass)ctPrimitiveType;
        paramBytecode1.addInvokespecial(str, "<init>", 
            Descriptor.ofMethod(CtClass.voidType, arrayOfCtClass));
      } else {
        paramBytecode1.addAload(paramInt);
        paramInt++;
      } 
      paramBytecode1.addOpcode(83);
    } 
    return 8;
  }
  
  protected void compileUnwrapValue(CtClass paramCtClass, Bytecode1 paramBytecode1) throws CompileError {
    if (paramCtClass == CtClass.voidType) {
      addNullIfVoid();
      return;
    } 
    if (this.exprType == 344)
      throw new CompileError("invalid type for " + this.returnCastName); 
    if (paramCtClass instanceof CtPrimitiveType) {
      CtPrimitiveType ctPrimitiveType = (CtPrimitiveType)paramCtClass;
      String str = ctPrimitiveType.getWrapperName();
      paramBytecode1.addCheckcast(str);
      paramBytecode1.addInvokevirtual(str, ctPrimitiveType.getGetMethodName(), ctPrimitiveType
          .getGetMethodDescriptor());
      setType(paramCtClass);
    } else {
      paramBytecode1.addCheckcast(paramCtClass);
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
  
  public void doNumCast(CtClass paramCtClass) throws CompileError {
    if (this.arrayDim == 0 && !isRefType(this.exprType))
      if (paramCtClass instanceof CtPrimitiveType) {
        CtPrimitiveType ctPrimitiveType = (CtPrimitiveType)paramCtClass;
        atNumCastExpr(this.exprType, 
            MemberResolver.descToType(ctPrimitiveType.getDescriptor()));
      } else {
        throw new CompileError("type mismatch");
      }  
  }
}
