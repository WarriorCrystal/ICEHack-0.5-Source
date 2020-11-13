package javassist.compiler;

import java.util.ArrayList;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool14;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.ArrayInit;
import javassist.compiler.ast.CallExpr;
import javassist.compiler.ast.Declarator;
import javassist.compiler.ast.Expr;
import javassist.compiler.ast.Keyword;
import javassist.compiler.ast.Member;
import javassist.compiler.ast.MethodDecl;
import javassist.compiler.ast.NewExpr;
import javassist.compiler.ast.Pair;
import javassist.compiler.ast.Stmnt;
import javassist.compiler.ast.Symbol;

public class MemberCodeGen extends CodeGen {
  protected MemberResolver resolver;
  
  protected CtClass thisClass;
  
  protected MethodInfo thisMethod;
  
  protected boolean resultStatic;
  
  public MemberCodeGen(Bytecode1 paramBytecode1, CtClass paramCtClass, ClassPool paramClassPool) {
    super(paramBytecode1);
    this.resolver = new MemberResolver(paramClassPool);
    this.thisClass = paramCtClass;
    this.thisMethod = null;
  }
  
  public int getMajorVersion() {
    ClassFile classFile = this.thisClass.getClassFile2();
    if (classFile == null)
      return ClassFile.MAJOR_VERSION; 
    return classFile.getMajorVersion();
  }
  
  public void setThisMethod(CtMethod paramCtMethod) {
    this.thisMethod = paramCtMethod.getMethodInfo2();
    if (this.typeChecker != null)
      this.typeChecker.setThisMethod(this.thisMethod); 
  }
  
  public CtClass getThisClass() {
    return this.thisClass;
  }
  
  protected String getThisName() {
    return MemberResolver.javaToJvmName(this.thisClass.getName());
  }
  
  protected String getSuperName() throws CompileError {
    return MemberResolver.javaToJvmName(
        MemberResolver.getSuperclass(this.thisClass).getName());
  }
  
  protected void insertDefaultSuperCall() throws CompileError {
    this.bytecode.addAload(0);
    this.bytecode.addInvokespecial(MemberResolver.getSuperclass(this.thisClass), "<init>", "()V");
  }
  
  static class JsrHook extends CodeGen.ReturnHook {
    ArrayList jsrList;
    
    CodeGen cgen;
    
    int var;
    
    JsrHook(CodeGen param1CodeGen) {
      super(param1CodeGen);
      this.jsrList = new ArrayList();
      this.cgen = param1CodeGen;
      this.var = -1;
    }
    
    private int getVar(int param1Int) {
      if (this.var < 0) {
        this.var = this.cgen.getMaxLocals();
        this.cgen.incMaxLocals(param1Int);
      } 
      return this.var;
    }
    
    private void jsrJmp(Bytecode1 param1Bytecode1) {
      param1Bytecode1.addOpcode(167);
      this.jsrList.add(new int[] { param1Bytecode1.currentPc(), this.var });
      param1Bytecode1.addIndex(0);
    }
    
    protected boolean doit(Bytecode1 param1Bytecode1, int param1Int) {
      switch (param1Int) {
        case 177:
          jsrJmp(param1Bytecode1);
          return false;
        case 176:
          param1Bytecode1.addAstore(getVar(1));
          jsrJmp(param1Bytecode1);
          param1Bytecode1.addAload(this.var);
          return false;
        case 172:
          param1Bytecode1.addIstore(getVar(1));
          jsrJmp(param1Bytecode1);
          param1Bytecode1.addIload(this.var);
          return false;
        case 173:
          param1Bytecode1.addLstore(getVar(2));
          jsrJmp(param1Bytecode1);
          param1Bytecode1.addLload(this.var);
          return false;
        case 175:
          param1Bytecode1.addDstore(getVar(2));
          jsrJmp(param1Bytecode1);
          param1Bytecode1.addDload(this.var);
          return false;
        case 174:
          param1Bytecode1.addFstore(getVar(1));
          jsrJmp(param1Bytecode1);
          param1Bytecode1.addFload(this.var);
          return false;
      } 
      throw new RuntimeException("fatal");
    }
  }
  
  static class JsrHook2 extends CodeGen.ReturnHook {
    int var;
    
    int target;
    
    JsrHook2(CodeGen param1CodeGen, int[] param1ArrayOfint) {
      super(param1CodeGen);
      this.target = param1ArrayOfint[0];
      this.var = param1ArrayOfint[1];
    }
    
    protected boolean doit(Bytecode1 param1Bytecode1, int param1Int) {
      switch (param1Int) {
        case 177:
          param1Bytecode1.addOpcode(167);
          param1Bytecode1.addIndex(this.target - param1Bytecode1.currentPc() + 3);
          return true;
        case 176:
          param1Bytecode1.addAstore(this.var);
        case 172:
          param1Bytecode1.addIstore(this.var);
        case 173:
          param1Bytecode1.addLstore(this.var);
        case 175:
          param1Bytecode1.addDstore(this.var);
        case 174:
          param1Bytecode1.addFstore(this.var);
      } 
      throw new RuntimeException("fatal");
    }
  }
  
  protected void atTryStmnt(Stmnt paramStmnt) throws CompileError {
    Bytecode1 bytecode1 = this.bytecode;
    Stmnt stmnt1 = (Stmnt)paramStmnt.getLeft();
    if (stmnt1 == null)
      return; 
    ASTList aSTList = (ASTList)paramStmnt.getRight().getLeft();
    Stmnt stmnt2 = (Stmnt)paramStmnt.getRight().getRight().getLeft();
    ArrayList<Integer> arrayList = new ArrayList();
    JsrHook jsrHook = null;
    if (stmnt2 != null)
      jsrHook = new JsrHook(this); 
    int i = bytecode1.currentPc();
    stmnt1.accept(this);
    int j = bytecode1.currentPc();
    if (i == j)
      throw new CompileError("empty try block"); 
    boolean bool = !this.hasReturned ? true : false;
    if (bool) {
      bytecode1.addOpcode(167);
      arrayList.add(new Integer(bytecode1.currentPc()));
      bytecode1.addIndex(0);
    } 
    int k = getMaxLocals();
    incMaxLocals(1);
    while (aSTList != null) {
      Pair pair = (Pair)aSTList.head();
      aSTList = aSTList.tail();
      Declarator declarator = (Declarator)pair.getLeft();
      Stmnt stmnt = (Stmnt)pair.getRight();
      declarator.setLocalVar(k);
      CtClass ctClass = this.resolver.lookupClassByJvmName(declarator.getClassName());
      declarator.setClassName(MemberResolver.javaToJvmName(ctClass.getName()));
      bytecode1.addExceptionHandler(i, j, bytecode1.currentPc(), ctClass);
      bytecode1.growStack(1);
      bytecode1.addAstore(k);
      this.hasReturned = false;
      if (stmnt != null)
        stmnt.accept(this); 
      if (!this.hasReturned) {
        bytecode1.addOpcode(167);
        arrayList.add(new Integer(bytecode1.currentPc()));
        bytecode1.addIndex(0);
        bool = true;
      } 
    } 
    if (stmnt2 != null) {
      jsrHook.remove(this);
      int n = bytecode1.currentPc();
      bytecode1.addExceptionHandler(i, n, n, 0);
      bytecode1.growStack(1);
      bytecode1.addAstore(k);
      this.hasReturned = false;
      stmnt2.accept(this);
      if (!this.hasReturned) {
        bytecode1.addAload(k);
        bytecode1.addOpcode(191);
      } 
      addFinally(jsrHook.jsrList, stmnt2);
    } 
    int m = bytecode1.currentPc();
    patchGoto(arrayList, m);
    this.hasReturned = !bool;
    if (stmnt2 != null && 
      bool)
      stmnt2.accept(this); 
  }
  
  private void addFinally(ArrayList<int[]> paramArrayList, Stmnt paramStmnt) throws CompileError {
    Bytecode1 bytecode1 = this.bytecode;
    int i = paramArrayList.size();
    for (byte b = 0; b < i; b++) {
      int[] arrayOfInt = paramArrayList.get(b);
      int j = arrayOfInt[0];
      bytecode1.write16bit(j, bytecode1.currentPc() - j + 1);
      JsrHook2 jsrHook2 = new JsrHook2(this, arrayOfInt);
      paramStmnt.accept(this);
      jsrHook2.remove(this);
      if (!this.hasReturned) {
        bytecode1.addOpcode(167);
        bytecode1.addIndex(j + 3 - bytecode1.currentPc());
      } 
    } 
  }
  
  public void atNewExpr(NewExpr paramNewExpr) throws CompileError {
    if (paramNewExpr.isArray()) {
      atNewArrayExpr(paramNewExpr);
    } else {
      CtClass ctClass = this.resolver.lookupClassByName(paramNewExpr.getClassName());
      String str = ctClass.getName();
      ASTList aSTList = paramNewExpr.getArguments();
      this.bytecode.addNew(str);
      this.bytecode.addOpcode(89);
      atMethodCallCore(ctClass, "<init>", aSTList, false, true, -1, (MemberResolver.Method)null);
      this.exprType = 307;
      this.arrayDim = 0;
      this.className = MemberResolver.javaToJvmName(str);
    } 
  }
  
  public void atNewArrayExpr(NewExpr paramNewExpr) throws CompileError {
    int i = paramNewExpr.getArrayType();
    ASTList aSTList1 = paramNewExpr.getArraySize();
    ASTList aSTList2 = paramNewExpr.getClassName();
    ArrayInit arrayInit = paramNewExpr.getInitializer();
    if (aSTList1.length() > 1) {
      if (arrayInit != null)
        throw new CompileError("sorry, multi-dimensional array initializer for new is not supported"); 
      atMultiNewArray(i, aSTList2, aSTList1);
      return;
    } 
    ASTree aSTree = aSTList1.head();
    atNewArrayExpr2(i, aSTree, Declarator.astToClassName(aSTList2, '/'), arrayInit);
  }
  
  private void atNewArrayExpr2(int paramInt, ASTree paramASTree, String paramString, ArrayInit paramArrayInit) throws CompileError {
    String str;
    if (paramArrayInit == null) {
      if (paramASTree == null)
        throw new CompileError("no array size"); 
      paramASTree.accept(this);
    } else if (paramASTree == null) {
      int i = paramArrayInit.length();
      this.bytecode.addIconst(i);
    } else {
      throw new CompileError("unnecessary array size specified for new");
    } 
    if (paramInt == 307) {
      str = resolveClassName(paramString);
      this.bytecode.addAnewarray(MemberResolver.jvmToJavaName(str));
    } else {
      str = null;
      byte b = 0;
      switch (paramInt) {
        case 301:
          b = 4;
          break;
        case 306:
          b = 5;
          break;
        case 317:
          b = 6;
          break;
        case 312:
          b = 7;
          break;
        case 303:
          b = 8;
          break;
        case 334:
          b = 9;
          break;
        case 324:
          b = 10;
          break;
        case 326:
          b = 11;
          break;
        default:
          badNewExpr();
          break;
      } 
      this.bytecode.addOpcode(188);
      this.bytecode.add(b);
    } 
    if (paramArrayInit != null) {
      int i = paramArrayInit.length();
      ArrayInit arrayInit = paramArrayInit;
      for (byte b = 0; b < i; b++) {
        this.bytecode.addOpcode(89);
        this.bytecode.addIconst(b);
        arrayInit.head().accept(this);
        if (!isRefType(paramInt))
          atNumCastExpr(this.exprType, paramInt); 
        this.bytecode.addOpcode(getArrayWriteOp(paramInt, 0));
        ASTList aSTList = arrayInit.tail();
      } 
    } 
    this.exprType = paramInt;
    this.arrayDim = 1;
    this.className = str;
  }
  
  private static void badNewExpr() throws CompileError {
    throw new CompileError("bad new expression");
  }
  
  protected void atArrayVariableAssign(ArrayInit paramArrayInit, int paramInt1, int paramInt2, String paramString) throws CompileError {
    atNewArrayExpr2(paramInt1, (ASTree)null, paramString, paramArrayInit);
  }
  
  public void atArrayInit(ArrayInit paramArrayInit) throws CompileError {
    throw new CompileError("array initializer is not supported");
  }
  
  protected void atMultiNewArray(int paramInt, ASTList paramASTList1, ASTList paramASTList2) throws CompileError {
    String str;
    int i = paramASTList2.length();
    byte b;
    for (b = 0; paramASTList2 != null; paramASTList2 = paramASTList2.tail()) {
      ASTree aSTree = paramASTList2.head();
      if (aSTree == null)
        break; 
      b++;
      aSTree.accept(this);
      if (this.exprType != 324)
        throw new CompileError("bad type for array size"); 
    } 
    this.exprType = paramInt;
    this.arrayDim = i;
    if (paramInt == 307) {
      this.className = resolveClassName(paramASTList1);
      str = toJvmArrayName(this.className, i);
    } else {
      str = toJvmTypeName(paramInt, i);
    } 
    this.bytecode.addMultiNewarray(str, b);
  }
  
  public void atCallExpr(CallExpr paramCallExpr) throws CompileError {
    String str = null;
    CtClass ctClass = null;
    ASTree aSTree = paramCallExpr.oprand1();
    ASTList aSTList = (ASTList)paramCallExpr.oprand2();
    boolean bool1 = false;
    boolean bool2 = false;
    int i = -1;
    MemberResolver.Method method = paramCallExpr.getMethod();
    if (aSTree instanceof Member) {
      str = ((Member)aSTree).get();
      ctClass = this.thisClass;
      if (this.inStaticMethod || (method != null && method.isStatic())) {
        bool1 = true;
      } else {
        i = this.bytecode.currentPc();
        this.bytecode.addAload(0);
      } 
    } else if (aSTree instanceof Keyword) {
      bool2 = true;
      str = "<init>";
      ctClass = this.thisClass;
      if (this.inStaticMethod)
        throw new CompileError("a constructor cannot be static"); 
      this.bytecode.addAload(0);
      if (((Keyword)aSTree).get() == 336)
        ctClass = MemberResolver.getSuperclass(ctClass); 
    } else if (aSTree instanceof Expr) {
      Expr expr = (Expr)aSTree;
      str = ((Symbol)expr.oprand2()).get();
      int j = expr.getOperator();
      if (j == 35) {
        ctClass = this.resolver.lookupClass(((Symbol)expr.oprand1()).get(), false);
        bool1 = true;
      } else if (j == 46) {
        ASTree aSTree1 = expr.oprand1();
        String str1 = TypeChecker.isDotSuper(aSTree1);
        if (str1 != null) {
          bool2 = true;
          ctClass = MemberResolver.getSuperInterface(this.thisClass, str1);
          if (this.inStaticMethod || (method != null && method.isStatic())) {
            bool1 = true;
          } else {
            i = this.bytecode.currentPc();
            this.bytecode.addAload(0);
          } 
        } else {
          if (aSTree1 instanceof Keyword && (
            (Keyword)aSTree1).get() == 336)
            bool2 = true; 
          try {
            aSTree1.accept(this);
          } catch (NoFieldException noFieldException) {
            if (noFieldException.getExpr() != aSTree1)
              throw noFieldException; 
            this.exprType = 307;
            this.arrayDim = 0;
            this.className = noFieldException.getField();
            bool1 = true;
          } 
          if (this.arrayDim > 0) {
            ctClass = this.resolver.lookupClass("java.lang.Object", true);
          } else if (this.exprType == 307) {
            ctClass = this.resolver.lookupClassByJvmName(this.className);
          } else {
            badMethod();
          } 
        } 
      } else {
        badMethod();
      } 
    } else {
      fatal();
    } 
    atMethodCallCore(ctClass, str, aSTList, bool1, bool2, i, method);
  }
  
  private static void badMethod() throws CompileError {
    throw new CompileError("bad method");
  }
  
  public void atMethodCallCore(CtClass paramCtClass, String paramString, ASTList paramASTList, boolean paramBoolean1, boolean paramBoolean2, int paramInt, MemberResolver.Method paramMethod) throws CompileError {
    int i = getMethodArgsLength(paramASTList);
    int[] arrayOfInt1 = new int[i];
    int[] arrayOfInt2 = new int[i];
    String[] arrayOfString = new String[i];
    if (!paramBoolean1 && paramMethod != null && paramMethod.isStatic()) {
      this.bytecode.addOpcode(87);
      paramBoolean1 = true;
    } 
    int j = this.bytecode.getStackDepth();
    atMethodArgs(paramASTList, arrayOfInt1, arrayOfInt2, arrayOfString);
    if (paramMethod == null)
      paramMethod = this.resolver.lookupMethod(paramCtClass, this.thisClass, this.thisMethod, paramString, arrayOfInt1, arrayOfInt2, arrayOfString); 
    if (paramMethod == null) {
      String str;
      if (paramString.equals("<init>")) {
        str = "constructor not found";
      } else {
        str = "Method " + paramString + " not found in " + paramCtClass.getName();
      } 
      throw new CompileError(str);
    } 
    atMethodCallCore2(paramCtClass, paramString, paramBoolean1, paramBoolean2, paramInt, paramMethod);
  }
  
  private void atMethodCallCore2(CtClass paramCtClass, String paramString, boolean paramBoolean1, boolean paramBoolean2, int paramInt, MemberResolver.Method paramMethod) throws CompileError {
    CtClass ctClass = paramMethod.declaring;
    MethodInfo methodInfo = paramMethod.info;
    String str = methodInfo.getDescriptor();
    int i = methodInfo.getAccessFlags();
    if (paramString.equals("<init>")) {
      paramBoolean2 = true;
      if (ctClass != paramCtClass)
        throw new CompileError("no such constructor: " + paramCtClass.getName()); 
      if (ctClass != this.thisClass && AccessFlag.isPrivate(i)) {
        str = getAccessibleConstructor(str, ctClass, methodInfo);
        this.bytecode.addOpcode(1);
      } 
    } else if (AccessFlag.isPrivate(i)) {
      if (ctClass == this.thisClass) {
        paramBoolean2 = true;
      } else {
        paramBoolean2 = false;
        paramBoolean1 = true;
        String str1 = str;
        if ((i & 0x8) == 0)
          str = Descriptor.insertParameter(ctClass.getName(), str1); 
        i = AccessFlag.setPackage(i) | 0x8;
        paramString = getAccessiblePrivate(paramString, str1, str, methodInfo, ctClass);
      } 
    } 
    boolean bool = false;
    if ((i & 0x8) != 0) {
      if (!paramBoolean1) {
        paramBoolean1 = true;
        if (paramInt >= 0) {
          this.bytecode.write(paramInt, 0);
        } else {
          bool = true;
        } 
      } 
      this.bytecode.addInvokestatic(ctClass, paramString, str);
    } else if (paramBoolean2) {
      this.bytecode.addInvokespecial(paramCtClass, paramString, str);
    } else {
      if (!Modifier.isPublic(ctClass.getModifiers()) || ctClass
        .isInterface() != paramCtClass.isInterface())
        ctClass = paramCtClass; 
      if (ctClass.isInterface()) {
        int j = Descriptor.paramSize(str) + 1;
        this.bytecode.addInvokeinterface(ctClass, paramString, str, j);
      } else {
        if (paramBoolean1)
          throw new CompileError(paramString + " is not static"); 
        this.bytecode.addInvokevirtual(ctClass, paramString, str);
      } 
    } 
    setReturnType(str, paramBoolean1, bool);
  }
  
  protected String getAccessiblePrivate(String paramString1, String paramString2, String paramString3, MethodInfo paramMethodInfo, CtClass paramCtClass) throws CompileError {
    if (isEnclosing(paramCtClass, this.thisClass)) {
      AccessorMaker accessorMaker = paramCtClass.getAccessorMaker();
      if (accessorMaker != null)
        return accessorMaker.getMethodAccessor(paramString1, paramString2, paramString3, paramMethodInfo); 
    } 
    throw new CompileError("Method " + paramString1 + " is private");
  }
  
  protected String getAccessibleConstructor(String paramString, CtClass paramCtClass, MethodInfo paramMethodInfo) throws CompileError {
    if (isEnclosing(paramCtClass, this.thisClass)) {
      AccessorMaker accessorMaker = paramCtClass.getAccessorMaker();
      if (accessorMaker != null)
        return accessorMaker.getConstructor(paramCtClass, paramString, paramMethodInfo); 
    } 
    throw new CompileError("the called constructor is private in " + paramCtClass
        .getName());
  }
  
  private boolean isEnclosing(CtClass paramCtClass1, CtClass paramCtClass2) {
    try {
      while (paramCtClass2 != null) {
        paramCtClass2 = paramCtClass2.getDeclaringClass();
        if (paramCtClass2 == paramCtClass1)
          return true; 
      } 
    } catch (NotFoundException notFoundException) {}
    return false;
  }
  
  public int getMethodArgsLength(ASTList paramASTList) {
    return ASTList.length(paramASTList);
  }
  
  public void atMethodArgs(ASTList paramASTList, int[] paramArrayOfint1, int[] paramArrayOfint2, String[] paramArrayOfString) throws CompileError {
    byte b = 0;
    while (paramASTList != null) {
      ASTree aSTree = paramASTList.head();
      aSTree.accept(this);
      paramArrayOfint1[b] = this.exprType;
      paramArrayOfint2[b] = this.arrayDim;
      paramArrayOfString[b] = this.className;
      b++;
      paramASTList = paramASTList.tail();
    } 
  }
  
  void setReturnType(String paramString, boolean paramBoolean1, boolean paramBoolean2) throws CompileError {
    int i = paramString.indexOf(')');
    if (i < 0)
      badMethod(); 
    char c = paramString.charAt(++i);
    byte b = 0;
    while (c == '[') {
      b++;
      c = paramString.charAt(++i);
    } 
    this.arrayDim = b;
    if (c == 'L') {
      int k = paramString.indexOf(';', i + 1);
      if (k < 0)
        badMethod(); 
      this.exprType = 307;
      this.className = paramString.substring(i + 1, k);
    } else {
      this.exprType = MemberResolver.descToType(c);
      this.className = null;
    } 
    int j = this.exprType;
    if (paramBoolean1 && 
      paramBoolean2)
      if (is2word(j, b)) {
        this.bytecode.addOpcode(93);
        this.bytecode.addOpcode(88);
        this.bytecode.addOpcode(87);
      } else if (j == 344) {
        this.bytecode.addOpcode(87);
      } else {
        this.bytecode.addOpcode(95);
        this.bytecode.addOpcode(87);
      }  
  }
  
  protected void atFieldAssign(Expr paramExpr, int paramInt, ASTree paramASTree1, ASTree paramASTree2, boolean paramBoolean) throws CompileError {
    int i;
    CtField ctField = fieldAccess(paramASTree1, false);
    boolean bool1 = this.resultStatic;
    if (paramInt != 61 && !bool1)
      this.bytecode.addOpcode(89); 
    if (paramInt == 61) {
      FieldInfo fieldInfo = ctField.getFieldInfo2();
      setFieldType(fieldInfo);
      AccessorMaker accessorMaker = isAccessibleField(ctField, fieldInfo);
      if (accessorMaker == null) {
        i = addFieldrefInfo(ctField, fieldInfo);
      } else {
        i = 0;
      } 
    } else {
      i = atFieldRead(ctField, bool1);
    } 
    int j = this.exprType;
    int k = this.arrayDim;
    String str = this.className;
    atAssignCore(paramExpr, paramInt, paramASTree2, j, k, str);
    boolean bool2 = is2word(j, k);
    if (paramBoolean) {
      byte b;
      if (bool1) {
        b = bool2 ? 92 : 89;
      } else {
        b = bool2 ? 93 : 90;
      } 
      this.bytecode.addOpcode(b);
    } 
    atFieldAssignCore(ctField, bool1, i, bool2);
    this.exprType = j;
    this.arrayDim = k;
    this.className = str;
  }
  
  private void atFieldAssignCore(CtField paramCtField, boolean paramBoolean1, int paramInt, boolean paramBoolean2) throws CompileError {
    if (paramInt != 0) {
      if (paramBoolean1) {
        this.bytecode.add(179);
        this.bytecode.growStack(paramBoolean2 ? -2 : -1);
      } else {
        this.bytecode.add(181);
        this.bytecode.growStack(paramBoolean2 ? -3 : -2);
      } 
      this.bytecode.addIndex(paramInt);
    } else {
      CtClass ctClass = paramCtField.getDeclaringClass();
      AccessorMaker accessorMaker = ctClass.getAccessorMaker();
      FieldInfo fieldInfo = paramCtField.getFieldInfo2();
      MethodInfo methodInfo = accessorMaker.getFieldSetter(fieldInfo, paramBoolean1);
      this.bytecode.addInvokestatic(ctClass, methodInfo.getName(), methodInfo
          .getDescriptor());
    } 
  }
  
  public void atMember(Member paramMember) throws CompileError {
    atFieldRead((ASTree)paramMember);
  }
  
  protected void atFieldRead(ASTree paramASTree) throws CompileError {
    CtField ctField = fieldAccess(paramASTree, true);
    if (ctField == null) {
      atArrayLength(paramASTree);
      return;
    } 
    boolean bool = this.resultStatic;
    ASTree aSTree = TypeChecker.getConstantFieldValue(ctField);
    if (aSTree == null) {
      atFieldRead(ctField, bool);
    } else {
      aSTree.accept(this);
      setFieldType(ctField.getFieldInfo2());
    } 
  }
  
  private void atArrayLength(ASTree paramASTree) throws CompileError {
    if (this.arrayDim == 0)
      throw new CompileError(".length applied to a non array"); 
    this.bytecode.addOpcode(190);
    this.exprType = 324;
    this.arrayDim = 0;
  }
  
  private int atFieldRead(CtField paramCtField, boolean paramBoolean) throws CompileError {
    FieldInfo fieldInfo = paramCtField.getFieldInfo2();
    boolean bool = setFieldType(fieldInfo);
    AccessorMaker accessorMaker = isAccessibleField(paramCtField, fieldInfo);
    if (accessorMaker != null) {
      MethodInfo methodInfo = accessorMaker.getFieldGetter(fieldInfo, paramBoolean);
      this.bytecode.addInvokestatic(paramCtField.getDeclaringClass(), methodInfo.getName(), methodInfo
          .getDescriptor());
      return 0;
    } 
    int i = addFieldrefInfo(paramCtField, fieldInfo);
    if (paramBoolean) {
      this.bytecode.add(178);
      this.bytecode.growStack(bool ? 2 : 1);
    } else {
      this.bytecode.add(180);
      this.bytecode.growStack(bool ? 1 : 0);
    } 
    this.bytecode.addIndex(i);
    return i;
  }
  
  private AccessorMaker isAccessibleField(CtField paramCtField, FieldInfo paramFieldInfo) throws CompileError {
    if (AccessFlag.isPrivate(paramFieldInfo.getAccessFlags()) && paramCtField
      .getDeclaringClass() != this.thisClass) {
      CtClass ctClass = paramCtField.getDeclaringClass();
      if (isEnclosing(ctClass, this.thisClass)) {
        AccessorMaker accessorMaker = ctClass.getAccessorMaker();
        if (accessorMaker != null)
          return accessorMaker; 
        throw new CompileError("fatal error.  bug?");
      } 
      throw new CompileError("Field " + paramCtField.getName() + " in " + ctClass
          .getName() + " is private.");
    } 
    return null;
  }
  
  private boolean setFieldType(FieldInfo paramFieldInfo) throws CompileError {
    String str = paramFieldInfo.getDescriptor();
    byte b1 = 0;
    byte b2 = 0;
    char c = str.charAt(b1);
    while (c == '[') {
      b2++;
      c = str.charAt(++b1);
    } 
    this.arrayDim = b2;
    this.exprType = MemberResolver.descToType(c);
    if (c == 'L') {
      this.className = str.substring(b1 + 1, str.indexOf(';', b1 + 1));
    } else {
      this.className = null;
    } 
    return (b2 == 0 && (c == 'J' || c == 'D'));
  }
  
  private int addFieldrefInfo(CtField paramCtField, FieldInfo paramFieldInfo) {
    ConstPool14 constPool14 = this.bytecode.getConstPool();
    String str1 = paramCtField.getDeclaringClass().getName();
    int i = constPool14.addClassInfo(str1);
    String str2 = paramFieldInfo.getName();
    String str3 = paramFieldInfo.getDescriptor();
    return constPool14.addFieldrefInfo(i, str2, str3);
  }
  
  protected void atClassObject2(String paramString) throws CompileError {
    if (getMajorVersion() < 49) {
      super.atClassObject2(paramString);
    } else {
      this.bytecode.addLdc(this.bytecode.getConstPool().addClassInfo(paramString));
    } 
  }
  
  protected void atFieldPlusPlus(int paramInt, boolean paramBoolean1, ASTree paramASTree, Expr paramExpr, boolean paramBoolean2) throws CompileError {
    byte b;
    CtField ctField = fieldAccess(paramASTree, false);
    boolean bool1 = this.resultStatic;
    if (!bool1)
      this.bytecode.addOpcode(89); 
    int i = atFieldRead(ctField, bool1);
    int j = this.exprType;
    boolean bool2 = is2word(j, this.arrayDim);
    if (bool1) {
      b = bool2 ? 92 : 89;
    } else {
      b = bool2 ? 93 : 90;
    } 
    atPlusPlusCore(b, paramBoolean2, paramInt, paramBoolean1, paramExpr);
    atFieldAssignCore(ctField, bool1, i, bool2);
  }
  
  protected CtField fieldAccess(ASTree paramASTree, boolean paramBoolean) throws CompileError {
    if (paramASTree instanceof Member) {
      String str = ((Member)paramASTree).get();
      CtField ctField = null;
      try {
        ctField = this.thisClass.getField(str);
      } catch (NotFoundException notFoundException) {
        throw new NoFieldException(str, paramASTree);
      } 
      boolean bool = Modifier.isStatic(ctField.getModifiers());
      if (!bool) {
        if (this.inStaticMethod)
          throw new CompileError("not available in a static method: " + str); 
        this.bytecode.addAload(0);
      } 
      this.resultStatic = bool;
      return ctField;
    } 
    if (paramASTree instanceof Expr) {
      Expr expr = (Expr)paramASTree;
      int i = expr.getOperator();
      if (i == 35) {
        CtField ctField = this.resolver.lookupField(((Symbol)expr.oprand1()).get(), (Symbol)expr
            .oprand2());
        this.resultStatic = true;
        return ctField;
      } 
      if (i == 46) {
        CtField ctField = null;
        try {
          expr.oprand1().accept(this);
          if (this.exprType == 307 && this.arrayDim == 0) {
            ctField = this.resolver.lookupFieldByJvmName(this.className, (Symbol)expr
                .oprand2());
          } else {
            if (paramBoolean && this.arrayDim > 0 && ((Symbol)expr
              .oprand2()).get().equals("length"))
              return null; 
            badLvalue();
          } 
          boolean bool = Modifier.isStatic(ctField.getModifiers());
          if (bool)
            this.bytecode.addOpcode(87); 
          this.resultStatic = bool;
          return ctField;
        } catch (NoFieldException noFieldException) {
          if (noFieldException.getExpr() != expr.oprand1())
            throw noFieldException; 
          Symbol symbol = (Symbol)expr.oprand2();
          String str = noFieldException.getField();
          ctField = this.resolver.lookupFieldByJvmName2(str, symbol, paramASTree);
          this.resultStatic = true;
          return ctField;
        } 
      } 
      badLvalue();
    } else {
      badLvalue();
    } 
    this.resultStatic = false;
    return null;
  }
  
  private static void badLvalue() throws CompileError {
    throw new CompileError("bad l-value");
  }
  
  public CtClass[] makeParamList(MethodDecl paramMethodDecl) throws CompileError {
    CtClass[] arrayOfCtClass;
    ASTList aSTList = paramMethodDecl.getParams();
    if (aSTList == null) {
      arrayOfCtClass = new CtClass[0];
    } else {
      byte b = 0;
      arrayOfCtClass = new CtClass[aSTList.length()];
      while (aSTList != null) {
        arrayOfCtClass[b++] = this.resolver.lookupClass((Declarator)aSTList.head());
        aSTList = aSTList.tail();
      } 
    } 
    return arrayOfCtClass;
  }
  
  public CtClass[] makeThrowsList(MethodDecl paramMethodDecl) throws CompileError {
    ASTList aSTList = paramMethodDecl.getThrows();
    if (aSTList == null)
      return null; 
    byte b = 0;
    CtClass[] arrayOfCtClass = new CtClass[aSTList.length()];
    while (aSTList != null) {
      arrayOfCtClass[b++] = this.resolver.lookupClassByName((ASTList)aSTList.head());
      aSTList = aSTList.tail();
    } 
    return arrayOfCtClass;
  }
  
  protected String resolveClassName(ASTList paramASTList) throws CompileError {
    return this.resolver.resolveClassName(paramASTList);
  }
  
  protected String resolveClassName(String paramString) throws CompileError {
    return this.resolver.resolveJvmClassName(paramString);
  }
}
