package javassist.compiler;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.ArrayInit;
import javassist.compiler.ast.AssignExpr;
import javassist.compiler.ast.BinExpr;
import javassist.compiler.ast.CallExpr;
import javassist.compiler.ast.CastExpr;
import javassist.compiler.ast.CondExpr;
import javassist.compiler.ast.Declarator;
import javassist.compiler.ast.DoubleConst;
import javassist.compiler.ast.Expr;
import javassist.compiler.ast.InstanceOfExpr;
import javassist.compiler.ast.IntConst;
import javassist.compiler.ast.Keyword;
import javassist.compiler.ast.Member;
import javassist.compiler.ast.NewExpr;
import javassist.compiler.ast.StringL;
import javassist.compiler.ast.Symbol;
import javassist.compiler.ast.Variable;
import javassist.compiler.ast.Visitor;

public class TypeChecker extends Visitor implements Opcode, TokenId {
  static final String javaLangObject = "java.lang.Object";
  
  static final String jvmJavaLangObject = "java/lang/Object";
  
  static final String jvmJavaLangString = "java/lang/String";
  
  static final String jvmJavaLangClass = "java/lang/Class";
  
  protected int exprType;
  
  protected int arrayDim;
  
  protected String className;
  
  protected MemberResolver resolver;
  
  protected CtClass thisClass;
  
  protected MethodInfo thisMethod;
  
  public TypeChecker(CtClass paramCtClass, ClassPool paramClassPool) {
    this.resolver = new MemberResolver(paramClassPool);
    this.thisClass = paramCtClass;
    this.thisMethod = null;
  }
  
  protected static String argTypesToString(int[] paramArrayOfint1, int[] paramArrayOfint2, String[] paramArrayOfString) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('(');
    int i = paramArrayOfint1.length;
    if (i > 0) {
      byte b = 0;
      while (true) {
        typeToString(stringBuffer, paramArrayOfint1[b], paramArrayOfint2[b], paramArrayOfString[b]);
        if (++b < i) {
          stringBuffer.append(',');
          continue;
        } 
        break;
      } 
    } 
    stringBuffer.append(')');
    return stringBuffer.toString();
  }
  
  protected static StringBuffer typeToString(StringBuffer paramStringBuffer, int paramInt1, int paramInt2, String paramString) {
    String str;
    if (paramInt1 == 307) {
      str = MemberResolver.jvmToJavaName(paramString);
    } else if (paramInt1 == 412) {
      str = "Object";
    } else {
      try {
        str = MemberResolver.getTypeName(paramInt1);
      } catch (CompileError compileError) {
        str = "?";
      } 
    } 
    paramStringBuffer.append(str);
    while (paramInt2-- > 0)
      paramStringBuffer.append("[]"); 
    return paramStringBuffer;
  }
  
  public void setThisMethod(MethodInfo paramMethodInfo) {
    this.thisMethod = paramMethodInfo;
  }
  
  protected static void fatal() throws CompileError {
    throw new CompileError("fatal");
  }
  
  protected String getThisName() {
    return MemberResolver.javaToJvmName(this.thisClass.getName());
  }
  
  protected String getSuperName() throws CompileError {
    return MemberResolver.javaToJvmName(
        MemberResolver.getSuperclass(this.thisClass).getName());
  }
  
  protected String resolveClassName(ASTList paramASTList) throws CompileError {
    return this.resolver.resolveClassName(paramASTList);
  }
  
  protected String resolveClassName(String paramString) throws CompileError {
    return this.resolver.resolveJvmClassName(paramString);
  }
  
  public void atNewExpr(NewExpr paramNewExpr) throws CompileError {
    if (paramNewExpr.isArray()) {
      atNewArrayExpr(paramNewExpr);
    } else {
      CtClass ctClass = this.resolver.lookupClassByName(paramNewExpr.getClassName());
      String str = ctClass.getName();
      ASTList aSTList = paramNewExpr.getArguments();
      atMethodCallCore(ctClass, "<init>", aSTList);
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
    if (arrayInit != null)
      arrayInit.accept(this); 
    if (aSTList1.length() > 1) {
      atMultiNewArray(i, aSTList2, aSTList1);
    } else {
      ASTree aSTree = aSTList1.head();
      if (aSTree != null)
        aSTree.accept(this); 
      this.exprType = i;
      this.arrayDim = 1;
      if (i == 307) {
        this.className = resolveClassName(aSTList2);
      } else {
        this.className = null;
      } 
    } 
  }
  
  public void atArrayInit(ArrayInit paramArrayInit) throws CompileError {
    ArrayInit arrayInit = paramArrayInit;
    while (arrayInit != null) {
      ASTree aSTree = arrayInit.head();
      ASTList aSTList = arrayInit.tail();
      if (aSTree != null)
        aSTree.accept(this); 
    } 
  }
  
  protected void atMultiNewArray(int paramInt, ASTList paramASTList1, ASTList paramASTList2) throws CompileError {
    int i = paramASTList2.length();
    for (byte b = 0; paramASTList2 != null; paramASTList2 = paramASTList2.tail()) {
      ASTree aSTree = paramASTList2.head();
      if (aSTree == null)
        break; 
      b++;
      aSTree.accept(this);
    } 
    this.exprType = paramInt;
    this.arrayDim = i;
    if (paramInt == 307) {
      this.className = resolveClassName(paramASTList1);
    } else {
      this.className = null;
    } 
  }
  
  public void atAssignExpr(AssignExpr paramAssignExpr) throws CompileError {
    int i = paramAssignExpr.getOperator();
    ASTree aSTree1 = paramAssignExpr.oprand1();
    ASTree aSTree2 = paramAssignExpr.oprand2();
    if (aSTree1 instanceof Variable) {
      atVariableAssign((Expr)paramAssignExpr, i, (Variable)aSTree1, ((Variable)aSTree1)
          .getDeclarator(), aSTree2);
    } else {
      if (aSTree1 instanceof Expr) {
        Expr expr = (Expr)aSTree1;
        if (expr.getOperator() == 65) {
          atArrayAssign((Expr)paramAssignExpr, i, (Expr)aSTree1, aSTree2);
          return;
        } 
      } 
      atFieldAssign((Expr)paramAssignExpr, i, aSTree1, aSTree2);
    } 
  }
  
  private void atVariableAssign(Expr paramExpr, int paramInt, Variable paramVariable, Declarator paramDeclarator, ASTree paramASTree) throws CompileError {
    int i = paramDeclarator.getType();
    int j = paramDeclarator.getArrayDim();
    String str = paramDeclarator.getClassName();
    if (paramInt != 61)
      atVariable(paramVariable); 
    paramASTree.accept(this);
    this.exprType = i;
    this.arrayDim = j;
    this.className = str;
  }
  
  private void atArrayAssign(Expr paramExpr1, int paramInt, Expr paramExpr2, ASTree paramASTree) throws CompileError {
    atArrayRead(paramExpr2.oprand1(), paramExpr2.oprand2());
    int i = this.exprType;
    int j = this.arrayDim;
    String str = this.className;
    paramASTree.accept(this);
    this.exprType = i;
    this.arrayDim = j;
    this.className = str;
  }
  
  protected void atFieldAssign(Expr paramExpr, int paramInt, ASTree paramASTree1, ASTree paramASTree2) throws CompileError {
    CtField ctField = fieldAccess(paramASTree1);
    atFieldRead(ctField);
    int i = this.exprType;
    int j = this.arrayDim;
    String str = this.className;
    paramASTree2.accept(this);
    this.exprType = i;
    this.arrayDim = j;
    this.className = str;
  }
  
  public void atCondExpr(CondExpr paramCondExpr) throws CompileError {
    booleanExpr(paramCondExpr.condExpr());
    paramCondExpr.thenExpr().accept(this);
    int i = this.exprType;
    int j = this.arrayDim;
    String str = this.className;
    paramCondExpr.elseExpr().accept(this);
    if (j == 0 && j == this.arrayDim)
      if (CodeGen.rightIsStrong(i, this.exprType)) {
        paramCondExpr.setThen((ASTree)new CastExpr(this.exprType, 0, paramCondExpr.thenExpr()));
      } else if (CodeGen.rightIsStrong(this.exprType, i)) {
        paramCondExpr.setElse((ASTree)new CastExpr(i, 0, paramCondExpr.elseExpr()));
        this.exprType = i;
      }  
  }
  
  public void atBinExpr(BinExpr paramBinExpr) throws CompileError {
    int i = paramBinExpr.getOperator();
    int j = CodeGen.lookupBinOp(i);
    if (j >= 0) {
      if (i == 43) {
        Expr expr = atPlusExpr(paramBinExpr);
        if (expr != null) {
          CallExpr callExpr = CallExpr.makeCall((ASTree)Expr.make(46, (ASTree)expr, (ASTree)new Member("toString")), null);
          paramBinExpr.setOprand1((ASTree)callExpr);
          paramBinExpr.setOprand2(null);
          this.className = "java/lang/String";
        } 
      } else {
        ASTree aSTree1 = paramBinExpr.oprand1();
        ASTree aSTree2 = paramBinExpr.oprand2();
        aSTree1.accept(this);
        int k = this.exprType;
        aSTree2.accept(this);
        if (!isConstant(paramBinExpr, i, aSTree1, aSTree2))
          computeBinExprType(paramBinExpr, i, k); 
      } 
    } else {
      booleanExpr((ASTree)paramBinExpr);
    } 
  }
  
  private Expr atPlusExpr(BinExpr paramBinExpr) throws CompileError {
    ASTree aSTree1 = paramBinExpr.oprand1();
    ASTree aSTree2 = paramBinExpr.oprand2();
    if (aSTree2 == null) {
      aSTree1.accept(this);
      return null;
    } 
    if (isPlusExpr(aSTree1)) {
      Expr expr = atPlusExpr((BinExpr)aSTree1);
      if (expr != null) {
        aSTree2.accept(this);
        this.exprType = 307;
        this.arrayDim = 0;
        this.className = "java/lang/StringBuffer";
        return makeAppendCall((ASTree)expr, aSTree2);
      } 
    } else {
      aSTree1.accept(this);
    } 
    int i = this.exprType;
    int j = this.arrayDim;
    String str = this.className;
    aSTree2.accept(this);
    if (isConstant(paramBinExpr, 43, aSTree1, aSTree2))
      return null; 
    if ((i == 307 && j == 0 && "java/lang/String".equals(str)) || (this.exprType == 307 && this.arrayDim == 0 && "java/lang/String"
      
      .equals(this.className))) {
      ASTList aSTList = ASTList.make((ASTree)new Symbol("java"), (ASTree)new Symbol("lang"), (ASTree)new Symbol("StringBuffer"));
      NewExpr newExpr = new NewExpr(aSTList, null);
      this.exprType = 307;
      this.arrayDim = 0;
      this.className = "java/lang/StringBuffer";
      return makeAppendCall((ASTree)makeAppendCall((ASTree)newExpr, aSTree1), aSTree2);
    } 
    computeBinExprType(paramBinExpr, 43, i);
    return null;
  }
  
  private boolean isConstant(BinExpr paramBinExpr, int paramInt, ASTree paramASTree1, ASTree paramASTree2) throws CompileError {
    ASTree aSTree;
    paramASTree1 = stripPlusExpr(paramASTree1);
    paramASTree2 = stripPlusExpr(paramASTree2);
    StringL stringL = null;
    if (paramASTree1 instanceof StringL && paramASTree2 instanceof StringL && paramInt == 43) {
      stringL = new StringL(((StringL)paramASTree1).get() + ((StringL)paramASTree2).get());
    } else if (paramASTree1 instanceof IntConst) {
      aSTree = ((IntConst)paramASTree1).compute(paramInt, paramASTree2);
    } else if (paramASTree1 instanceof DoubleConst) {
      aSTree = ((DoubleConst)paramASTree1).compute(paramInt, paramASTree2);
    } 
    if (aSTree == null)
      return false; 
    paramBinExpr.setOperator(43);
    paramBinExpr.setOprand1(aSTree);
    paramBinExpr.setOprand2(null);
    aSTree.accept(this);
    return true;
  }
  
  static ASTree stripPlusExpr(ASTree paramASTree) {
    if (paramASTree instanceof BinExpr) {
      BinExpr binExpr = (BinExpr)paramASTree;
      if (binExpr.getOperator() == 43 && binExpr.oprand2() == null)
        return binExpr.getLeft(); 
    } else if (paramASTree instanceof Expr) {
      Expr expr = (Expr)paramASTree;
      int i = expr.getOperator();
      if (i == 35) {
        ASTree aSTree = getConstantFieldValue((Member)expr.oprand2());
        if (aSTree != null)
          return aSTree; 
      } else if (i == 43 && expr.getRight() == null) {
        return expr.getLeft();
      } 
    } else if (paramASTree instanceof Member) {
      ASTree aSTree = getConstantFieldValue((Member)paramASTree);
      if (aSTree != null)
        return aSTree; 
    } 
    return paramASTree;
  }
  
  private static ASTree getConstantFieldValue(Member paramMember) {
    return getConstantFieldValue(paramMember.getField());
  }
  
  public static ASTree getConstantFieldValue(CtField paramCtField) {
    if (paramCtField == null)
      return null; 
    Object object = paramCtField.getConstantValue();
    if (object == null)
      return null; 
    if (object instanceof String)
      return (ASTree)new StringL((String)object); 
    if (object instanceof Double || object instanceof Float) {
      char c = (object instanceof Double) ? 'ƕ' : 'Ɣ';
      return (ASTree)new DoubleConst(((Number)object).doubleValue(), c);
    } 
    if (object instanceof Number) {
      char c = (object instanceof Long) ? 'Ɠ' : 'ƒ';
      return (ASTree)new IntConst(((Number)object).longValue(), c);
    } 
    if (object instanceof Boolean)
      return (ASTree)new Keyword(((Boolean)object).booleanValue() ? 410 : 411); 
    return null;
  }
  
  private static boolean isPlusExpr(ASTree paramASTree) {
    if (paramASTree instanceof BinExpr) {
      BinExpr binExpr = (BinExpr)paramASTree;
      int i = binExpr.getOperator();
      return (i == 43);
    } 
    return false;
  }
  
  private static Expr makeAppendCall(ASTree paramASTree1, ASTree paramASTree2) {
    return (Expr)CallExpr.makeCall((ASTree)Expr.make(46, paramASTree1, (ASTree)new Member("append")), (ASTree)new ASTList(paramASTree2));
  }
  
  private void computeBinExprType(BinExpr paramBinExpr, int paramInt1, int paramInt2) throws CompileError {
    int i = this.exprType;
    if (paramInt1 == 364 || paramInt1 == 366 || paramInt1 == 370) {
      this.exprType = paramInt2;
    } else {
      insertCast(paramBinExpr, paramInt2, i);
    } 
    if (CodeGen.isP_INT(this.exprType) && this.exprType != 301)
      this.exprType = 324; 
  }
  
  private void booleanExpr(ASTree paramASTree) throws CompileError {
    int i = CodeGen.getCompOperator(paramASTree);
    if (i == 358) {
      BinExpr binExpr = (BinExpr)paramASTree;
      binExpr.oprand1().accept(this);
      int j = this.exprType;
      int k = this.arrayDim;
      binExpr.oprand2().accept(this);
      if (k == 0 && this.arrayDim == 0)
        insertCast(binExpr, j, this.exprType); 
    } else if (i == 33) {
      ((Expr)paramASTree).oprand1().accept(this);
    } else if (i == 369 || i == 368) {
      BinExpr binExpr = (BinExpr)paramASTree;
      binExpr.oprand1().accept(this);
      binExpr.oprand2().accept(this);
    } else {
      paramASTree.accept(this);
    } 
    this.exprType = 301;
    this.arrayDim = 0;
  }
  
  private void insertCast(BinExpr paramBinExpr, int paramInt1, int paramInt2) throws CompileError {
    if (CodeGen.rightIsStrong(paramInt1, paramInt2)) {
      paramBinExpr.setLeft((ASTree)new CastExpr(paramInt2, 0, paramBinExpr.oprand1()));
    } else {
      this.exprType = paramInt1;
    } 
  }
  
  public void atCastExpr(CastExpr paramCastExpr) throws CompileError {
    String str = resolveClassName(paramCastExpr.getClassName());
    paramCastExpr.getOprand().accept(this);
    this.exprType = paramCastExpr.getType();
    this.arrayDim = paramCastExpr.getArrayDim();
    this.className = str;
  }
  
  public void atInstanceOfExpr(InstanceOfExpr paramInstanceOfExpr) throws CompileError {
    paramInstanceOfExpr.getOprand().accept(this);
    this.exprType = 301;
    this.arrayDim = 0;
  }
  
  public void atExpr(Expr paramExpr) throws CompileError {
    int i = paramExpr.getOperator();
    ASTree aSTree = paramExpr.oprand1();
    if (i == 46) {
      String str = ((Symbol)paramExpr.oprand2()).get();
      if (str.equals("length")) {
        try {
          atArrayLength(paramExpr);
        } catch (NoFieldException noFieldException) {
          atFieldRead((ASTree)paramExpr);
        } 
      } else if (str.equals("class")) {
        atClassObject(paramExpr);
      } else {
        atFieldRead((ASTree)paramExpr);
      } 
    } else if (i == 35) {
      String str = ((Symbol)paramExpr.oprand2()).get();
      if (str.equals("class")) {
        atClassObject(paramExpr);
      } else {
        atFieldRead((ASTree)paramExpr);
      } 
    } else if (i == 65) {
      atArrayRead(aSTree, paramExpr.oprand2());
    } else if (i == 362 || i == 363) {
      atPlusPlus(i, aSTree, paramExpr);
    } else if (i == 33) {
      booleanExpr((ASTree)paramExpr);
    } else if (i == 67) {
      fatal();
    } else {
      aSTree.accept(this);
      if (!isConstant(paramExpr, i, aSTree) && (
        i == 45 || i == 126) && 
        CodeGen.isP_INT(this.exprType))
        this.exprType = 324; 
    } 
  }
  
  private boolean isConstant(Expr paramExpr, int paramInt, ASTree paramASTree) {
    paramASTree = stripPlusExpr(paramASTree);
    if (paramASTree instanceof IntConst) {
      IntConst intConst = (IntConst)paramASTree;
      long l = intConst.get();
      if (paramInt == 45) {
        l = -l;
      } else if (paramInt == 126) {
        l ^= 0xFFFFFFFFFFFFFFFFL;
      } else {
        return false;
      } 
      intConst.set(l);
    } else if (paramASTree instanceof DoubleConst) {
      DoubleConst doubleConst = (DoubleConst)paramASTree;
      if (paramInt == 45) {
        doubleConst.set(-doubleConst.get());
      } else {
        return false;
      } 
    } else {
      return false;
    } 
    paramExpr.setOperator(43);
    return true;
  }
  
  public void atCallExpr(CallExpr paramCallExpr) throws CompileError {
    String str = null;
    CtClass ctClass = null;
    ASTree aSTree = paramCallExpr.oprand1();
    ASTList aSTList = (ASTList)paramCallExpr.oprand2();
    if (aSTree instanceof Member) {
      str = ((Member)aSTree).get();
      ctClass = this.thisClass;
    } else if (aSTree instanceof Keyword) {
      str = "<init>";
      if (((Keyword)aSTree).get() == 336) {
        ctClass = MemberResolver.getSuperclass(this.thisClass);
      } else {
        ctClass = this.thisClass;
      } 
    } else if (aSTree instanceof Expr) {
      Expr expr = (Expr)aSTree;
      str = ((Symbol)expr.oprand2()).get();
      int i = expr.getOperator();
      if (i == 35) {
        ctClass = this.resolver.lookupClass(((Symbol)expr.oprand1()).get(), false);
      } else if (i == 46) {
        ASTree aSTree1 = expr.oprand1();
        String str1 = isDotSuper(aSTree1);
        if (str1 != null) {
          ctClass = MemberResolver.getSuperInterface(this.thisClass, str1);
        } else {
          try {
            aSTree1.accept(this);
          } catch (NoFieldException noFieldException) {
            if (noFieldException.getExpr() != aSTree1)
              throw noFieldException; 
            this.exprType = 307;
            this.arrayDim = 0;
            this.className = noFieldException.getField();
            expr.setOperator(35);
            expr.setOprand1((ASTree)new Symbol(MemberResolver.jvmToJavaName(this.className)));
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
    MemberResolver.Method method = atMethodCallCore(ctClass, str, aSTList);
    paramCallExpr.setMethod(method);
  }
  
  private static void badMethod() throws CompileError {
    throw new CompileError("bad method");
  }
  
  static String isDotSuper(ASTree paramASTree) {
    if (paramASTree instanceof Expr) {
      Expr expr = (Expr)paramASTree;
      if (expr.getOperator() == 46) {
        ASTree aSTree = expr.oprand2();
        if (aSTree instanceof Keyword && ((Keyword)aSTree).get() == 336)
          return ((Symbol)expr.oprand1()).get(); 
      } 
    } 
    return null;
  }
  
  public MemberResolver.Method atMethodCallCore(CtClass paramCtClass, String paramString, ASTList paramASTList) throws CompileError {
    int i = getMethodArgsLength(paramASTList);
    int[] arrayOfInt1 = new int[i];
    int[] arrayOfInt2 = new int[i];
    String[] arrayOfString = new String[i];
    atMethodArgs(paramASTList, arrayOfInt1, arrayOfInt2, arrayOfString);
    MemberResolver.Method method = this.resolver.lookupMethod(paramCtClass, this.thisClass, this.thisMethod, paramString, arrayOfInt1, arrayOfInt2, arrayOfString);
    if (method == null) {
      String str3, str1 = paramCtClass.getName();
      String str2 = argTypesToString(arrayOfInt1, arrayOfInt2, arrayOfString);
      if (paramString.equals("<init>")) {
        str3 = "cannot find constructor " + str1 + str2;
      } else {
        str3 = paramString + str2 + " not found in " + str1;
      } 
      throw new CompileError(str3);
    } 
    String str = method.info.getDescriptor();
    setReturnType(str);
    return method;
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
  
  void setReturnType(String paramString) throws CompileError {
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
      int j = paramString.indexOf(';', i + 1);
      if (j < 0)
        badMethod(); 
      this.exprType = 307;
      this.className = paramString.substring(i + 1, j);
    } else {
      this.exprType = MemberResolver.descToType(c);
      this.className = null;
    } 
  }
  
  private void atFieldRead(ASTree paramASTree) throws CompileError {
    atFieldRead(fieldAccess(paramASTree));
  }
  
  private void atFieldRead(CtField paramCtField) throws CompileError {
    FieldInfo fieldInfo = paramCtField.getFieldInfo2();
    String str = fieldInfo.getDescriptor();
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
  }
  
  protected CtField fieldAccess(ASTree paramASTree) throws CompileError {
    if (paramASTree instanceof Member) {
      Member member = (Member)paramASTree;
      String str = member.get();
      try {
        CtField ctField = this.thisClass.getField(str);
        if (Modifier.isStatic(ctField.getModifiers()))
          member.setField(ctField); 
        return ctField;
      } catch (NotFoundException notFoundException) {
        throw new NoFieldException(str, paramASTree);
      } 
    } 
    if (paramASTree instanceof Expr) {
      Expr expr = (Expr)paramASTree;
      int i = expr.getOperator();
      if (i == 35) {
        Member member = (Member)expr.oprand2();
        CtField ctField = this.resolver.lookupField(((Symbol)expr.oprand1()).get(), (Symbol)member);
        member.setField(ctField);
        return ctField;
      } 
      if (i == 46) {
        try {
          expr.oprand1().accept(this);
        } catch (NoFieldException noFieldException) {
          if (noFieldException.getExpr() != expr.oprand1())
            throw noFieldException; 
          return fieldAccess2(expr, noFieldException.getField());
        } 
        CompileError compileError = null;
        try {
          if (this.exprType == 307 && this.arrayDim == 0)
            return this.resolver.lookupFieldByJvmName(this.className, (Symbol)expr
                .oprand2()); 
        } catch (CompileError compileError1) {
          compileError = compileError1;
        } 
        ASTree aSTree = expr.oprand1();
        if (aSTree instanceof Symbol)
          return fieldAccess2(expr, ((Symbol)aSTree).get()); 
        if (compileError != null)
          throw compileError; 
      } 
    } 
    throw new CompileError("bad filed access");
  }
  
  private CtField fieldAccess2(Expr paramExpr, String paramString) throws CompileError {
    Member member = (Member)paramExpr.oprand2();
    CtField ctField = this.resolver.lookupFieldByJvmName2(paramString, (Symbol)member, (ASTree)paramExpr);
    paramExpr.setOperator(35);
    paramExpr.setOprand1((ASTree)new Symbol(MemberResolver.jvmToJavaName(paramString)));
    member.setField(ctField);
    return ctField;
  }
  
  public void atClassObject(Expr paramExpr) throws CompileError {
    this.exprType = 307;
    this.arrayDim = 0;
    this.className = "java/lang/Class";
  }
  
  public void atArrayLength(Expr paramExpr) throws CompileError {
    paramExpr.oprand1().accept(this);
    if (this.arrayDim == 0)
      throw new NoFieldException("length", paramExpr); 
    this.exprType = 324;
    this.arrayDim = 0;
  }
  
  public void atArrayRead(ASTree paramASTree1, ASTree paramASTree2) throws CompileError {
    paramASTree1.accept(this);
    int i = this.exprType;
    int j = this.arrayDim;
    String str = this.className;
    paramASTree2.accept(this);
    this.exprType = i;
    this.arrayDim = j - 1;
    this.className = str;
  }
  
  private void atPlusPlus(int paramInt, ASTree paramASTree, Expr paramExpr) throws CompileError {
    boolean bool = (paramASTree == null) ? true : false;
    if (bool)
      paramASTree = paramExpr.oprand2(); 
    if (paramASTree instanceof Variable) {
      Declarator declarator = ((Variable)paramASTree).getDeclarator();
      this.exprType = declarator.getType();
      this.arrayDim = declarator.getArrayDim();
    } else {
      if (paramASTree instanceof Expr) {
        Expr expr = (Expr)paramASTree;
        if (expr.getOperator() == 65) {
          atArrayRead(expr.oprand1(), expr.oprand2());
          int i = this.exprType;
          if (i == 324 || i == 303 || i == 306 || i == 334)
            this.exprType = 324; 
          return;
        } 
      } 
      atFieldPlusPlus(paramASTree);
    } 
  }
  
  protected void atFieldPlusPlus(ASTree paramASTree) throws CompileError {
    CtField ctField = fieldAccess(paramASTree);
    atFieldRead(ctField);
    int i = this.exprType;
    if (i == 324 || i == 303 || i == 306 || i == 334)
      this.exprType = 324; 
  }
  
  public void atMember(Member paramMember) throws CompileError {
    atFieldRead((ASTree)paramMember);
  }
  
  public void atVariable(Variable paramVariable) throws CompileError {
    Declarator declarator = paramVariable.getDeclarator();
    this.exprType = declarator.getType();
    this.arrayDim = declarator.getArrayDim();
    this.className = declarator.getClassName();
  }
  
  public void atKeyword(Keyword paramKeyword) throws CompileError {
    this.arrayDim = 0;
    int i = paramKeyword.get();
    switch (i) {
      case 410:
      case 411:
        this.exprType = 301;
        return;
      case 412:
        this.exprType = 412;
        return;
      case 336:
      case 339:
        this.exprType = 307;
        if (i == 339) {
          this.className = getThisName();
        } else {
          this.className = getSuperName();
        } 
        return;
    } 
    fatal();
  }
  
  public void atStringL(StringL paramStringL) throws CompileError {
    this.exprType = 307;
    this.arrayDim = 0;
    this.className = "java/lang/String";
  }
  
  public void atIntConst(IntConst paramIntConst) throws CompileError {
    this.arrayDim = 0;
    int i = paramIntConst.getType();
    if (i == 402 || i == 401) {
      this.exprType = (i == 402) ? 324 : 306;
    } else {
      this.exprType = 326;
    } 
  }
  
  public void atDoubleConst(DoubleConst paramDoubleConst) throws CompileError {
    this.arrayDim = 0;
    if (paramDoubleConst.getType() == 405) {
      this.exprType = 312;
    } else {
      this.exprType = 317;
    } 
  }
}
