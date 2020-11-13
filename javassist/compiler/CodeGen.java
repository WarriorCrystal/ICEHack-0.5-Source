package javassist.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import javassist.bytecode.Bytecode1;
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
import javassist.compiler.ast.FieldDecl;
import javassist.compiler.ast.InstanceOfExpr;
import javassist.compiler.ast.IntConst;
import javassist.compiler.ast.Keyword;
import javassist.compiler.ast.Member;
import javassist.compiler.ast.MethodDecl;
import javassist.compiler.ast.NewExpr;
import javassist.compiler.ast.Pair;
import javassist.compiler.ast.Stmnt;
import javassist.compiler.ast.StringL;
import javassist.compiler.ast.Symbol;
import javassist.compiler.ast.Variable;
import javassist.compiler.ast.Visitor;

public abstract class CodeGen extends Visitor implements Opcode, TokenId {
  static final String javaLangObject = "java.lang.Object";
  
  static final String jvmJavaLangObject = "java/lang/Object";
  
  static final String javaLangString = "java.lang.String";
  
  static final String jvmJavaLangString = "java/lang/String";
  
  protected Bytecode1 bytecode;
  
  private int tempVar;
  
  TypeChecker typeChecker;
  
  protected boolean hasReturned;
  
  public boolean inStaticMethod;
  
  protected ArrayList breakList;
  
  protected ArrayList continueList;
  
  protected ReturnHook returnHooks;
  
  protected int exprType;
  
  protected int arrayDim;
  
  protected String className;
  
  protected static abstract class ReturnHook {
    ReturnHook next;
    
    protected abstract boolean doit(Bytecode1 param1Bytecode1, int param1Int);
    
    protected ReturnHook(CodeGen param1CodeGen) {
      this.next = param1CodeGen.returnHooks;
      param1CodeGen.returnHooks = this;
    }
    
    protected void remove(CodeGen param1CodeGen) {
      param1CodeGen.returnHooks = this.next;
    }
  }
  
  public CodeGen(Bytecode1 paramBytecode1) {
    this.bytecode = paramBytecode1;
    this.tempVar = -1;
    this.typeChecker = null;
    this.hasReturned = false;
    this.inStaticMethod = false;
    this.breakList = null;
    this.continueList = null;
    this.returnHooks = null;
  }
  
  public void setTypeChecker(TypeChecker paramTypeChecker) {
    this.typeChecker = paramTypeChecker;
  }
  
  protected static void fatal() throws CompileError {
    throw new CompileError("fatal");
  }
  
  public static boolean is2word(int paramInt1, int paramInt2) {
    return (paramInt2 == 0 && (paramInt1 == 312 || paramInt1 == 326));
  }
  
  public int getMaxLocals() {
    return this.bytecode.getMaxLocals();
  }
  
  public void setMaxLocals(int paramInt) {
    this.bytecode.setMaxLocals(paramInt);
  }
  
  protected void incMaxLocals(int paramInt) {
    this.bytecode.incMaxLocals(paramInt);
  }
  
  protected int getTempVar() {
    if (this.tempVar < 0) {
      this.tempVar = getMaxLocals();
      incMaxLocals(2);
    } 
    return this.tempVar;
  }
  
  protected int getLocalVar(Declarator paramDeclarator) {
    int i = paramDeclarator.getLocalVar();
    if (i < 0) {
      i = getMaxLocals();
      paramDeclarator.setLocalVar(i);
      incMaxLocals(1);
    } 
    return i;
  }
  
  protected abstract String getThisName();
  
  protected abstract String getSuperName() throws CompileError;
  
  protected abstract String resolveClassName(ASTList paramASTList) throws CompileError;
  
  protected abstract String resolveClassName(String paramString) throws CompileError;
  
  protected static String toJvmArrayName(String paramString, int paramInt) {
    if (paramString == null)
      return null; 
    if (paramInt == 0)
      return paramString; 
    StringBuffer stringBuffer = new StringBuffer();
    int i = paramInt;
    while (i-- > 0)
      stringBuffer.append('['); 
    stringBuffer.append('L');
    stringBuffer.append(paramString);
    stringBuffer.append(';');
    return stringBuffer.toString();
  }
  
  protected static String toJvmTypeName(int paramInt1, int paramInt2) {
    byte b = 73;
    switch (paramInt1) {
      case 301:
        b = 90;
        break;
      case 303:
        b = 66;
        break;
      case 306:
        b = 67;
        break;
      case 334:
        b = 83;
        break;
      case 324:
        b = 73;
        break;
      case 326:
        b = 74;
        break;
      case 317:
        b = 70;
        break;
      case 312:
        b = 68;
        break;
      case 344:
        b = 86;
        break;
    } 
    StringBuffer stringBuffer = new StringBuffer();
    while (paramInt2-- > 0)
      stringBuffer.append('['); 
    stringBuffer.append(b);
    return stringBuffer.toString();
  }
  
  public void compileExpr(ASTree paramASTree) throws CompileError {
    doTypeCheck(paramASTree);
    paramASTree.accept(this);
  }
  
  public boolean compileBooleanExpr(boolean paramBoolean, ASTree paramASTree) throws CompileError {
    doTypeCheck(paramASTree);
    return booleanExpr(paramBoolean, paramASTree);
  }
  
  public void doTypeCheck(ASTree paramASTree) throws CompileError {
    if (this.typeChecker != null)
      paramASTree.accept(this.typeChecker); 
  }
  
  public void atASTList(ASTList paramASTList) throws CompileError {
    fatal();
  }
  
  public void atPair(Pair paramPair) throws CompileError {
    fatal();
  }
  
  public void atSymbol(Symbol paramSymbol) throws CompileError {
    fatal();
  }
  
  public void atFieldDecl(FieldDecl paramFieldDecl) throws CompileError {
    paramFieldDecl.getInit().accept(this);
  }
  
  public void atMethodDecl(MethodDecl paramMethodDecl) throws CompileError {
    ASTList aSTList1 = paramMethodDecl.getModifiers();
    setMaxLocals(1);
    while (aSTList1 != null) {
      Keyword keyword = (Keyword)aSTList1.head();
      aSTList1 = aSTList1.tail();
      if (keyword.get() == 335) {
        setMaxLocals(0);
        this.inStaticMethod = true;
      } 
    } 
    ASTList aSTList2 = paramMethodDecl.getParams();
    while (aSTList2 != null) {
      atDeclarator((Declarator)aSTList2.head());
      aSTList2 = aSTList2.tail();
    } 
    Stmnt stmnt = paramMethodDecl.getBody();
    atMethodBody(stmnt, paramMethodDecl.isConstructor(), 
        (paramMethodDecl.getReturn().getType() == 344));
  }
  
  public void atMethodBody(Stmnt paramStmnt, boolean paramBoolean1, boolean paramBoolean2) throws CompileError {
    if (paramStmnt == null)
      return; 
    if (paramBoolean1 && needsSuperCall(paramStmnt))
      insertDefaultSuperCall(); 
    this.hasReturned = false;
    paramStmnt.accept(this);
    if (!this.hasReturned)
      if (paramBoolean2) {
        this.bytecode.addOpcode(177);
        this.hasReturned = true;
      } else {
        throw new CompileError("no return statement");
      }  
  }
  
  private boolean needsSuperCall(Stmnt paramStmnt) throws CompileError {
    if (paramStmnt.getOperator() == 66)
      paramStmnt = (Stmnt)paramStmnt.head(); 
    if (paramStmnt != null && paramStmnt.getOperator() == 69) {
      ASTree aSTree = paramStmnt.head();
      if (aSTree != null && aSTree instanceof Expr && ((Expr)aSTree)
        .getOperator() == 67) {
        ASTree aSTree1 = ((Expr)aSTree).head();
        if (aSTree1 instanceof Keyword) {
          int i = ((Keyword)aSTree1).get();
          return (i != 339 && i != 336);
        } 
      } 
    } 
    return true;
  }
  
  protected abstract void insertDefaultSuperCall() throws CompileError;
  
  public void atStmnt(Stmnt paramStmnt) throws CompileError {
    if (paramStmnt == null)
      return; 
    int i = paramStmnt.getOperator();
    if (i == 69) {
      ASTree aSTree = paramStmnt.getLeft();
      doTypeCheck(aSTree);
      if (aSTree instanceof AssignExpr) {
        atAssignExpr((AssignExpr)aSTree, false);
      } else if (isPlusPlusExpr(aSTree)) {
        Expr expr = (Expr)aSTree;
        atPlusPlus(expr.getOperator(), expr.oprand1(), expr, false);
      } else {
        aSTree.accept(this);
        if (is2word(this.exprType, this.arrayDim)) {
          this.bytecode.addOpcode(88);
        } else if (this.exprType != 344) {
          this.bytecode.addOpcode(87);
        } 
      } 
    } else if (i == 68 || i == 66) {
      Stmnt stmnt = paramStmnt;
      while (stmnt != null) {
        ASTree aSTree = stmnt.head();
        ASTList aSTList = stmnt.tail();
        if (aSTree != null)
          aSTree.accept(this); 
      } 
    } else if (i == 320) {
      atIfStmnt(paramStmnt);
    } else if (i == 346 || i == 311) {
      atWhileStmnt(paramStmnt, (i == 346));
    } else if (i == 318) {
      atForStmnt(paramStmnt);
    } else if (i == 302 || i == 309) {
      atBreakStmnt(paramStmnt, (i == 302));
    } else if (i == 333) {
      atReturnStmnt(paramStmnt);
    } else if (i == 340) {
      atThrowStmnt(paramStmnt);
    } else if (i == 343) {
      atTryStmnt(paramStmnt);
    } else if (i == 337) {
      atSwitchStmnt(paramStmnt);
    } else if (i == 338) {
      atSyncStmnt(paramStmnt);
    } else {
      this.hasReturned = false;
      throw new CompileError("sorry, not supported statement: TokenId " + i);
    } 
  }
  
  private void atIfStmnt(Stmnt paramStmnt) throws CompileError {
    ASTree aSTree = paramStmnt.head();
    Stmnt stmnt1 = (Stmnt)paramStmnt.tail().head();
    Stmnt stmnt2 = (Stmnt)paramStmnt.tail().tail().head();
    if (compileBooleanExpr(false, aSTree)) {
      this.hasReturned = false;
      if (stmnt2 != null)
        stmnt2.accept(this); 
      return;
    } 
    int i = this.bytecode.currentPc();
    int j = 0;
    this.bytecode.addIndex(0);
    this.hasReturned = false;
    if (stmnt1 != null)
      stmnt1.accept(this); 
    boolean bool = this.hasReturned;
    this.hasReturned = false;
    if (stmnt2 != null && !bool) {
      this.bytecode.addOpcode(167);
      j = this.bytecode.currentPc();
      this.bytecode.addIndex(0);
    } 
    this.bytecode.write16bit(i, this.bytecode.currentPc() - i + 1);
    if (stmnt2 != null) {
      stmnt2.accept(this);
      if (!bool)
        this.bytecode.write16bit(j, this.bytecode.currentPc() - j + 1); 
      this.hasReturned = (bool && this.hasReturned);
    } 
  }
  
  private void atWhileStmnt(Stmnt paramStmnt, boolean paramBoolean) throws CompileError {
    ArrayList arrayList1 = this.breakList;
    ArrayList arrayList2 = this.continueList;
    this.breakList = new ArrayList();
    this.continueList = new ArrayList();
    ASTree aSTree = paramStmnt.head();
    Stmnt stmnt = (Stmnt)paramStmnt.tail();
    int i = 0;
    if (paramBoolean) {
      this.bytecode.addOpcode(167);
      i = this.bytecode.currentPc();
      this.bytecode.addIndex(0);
    } 
    int j = this.bytecode.currentPc();
    if (stmnt != null)
      stmnt.accept(this); 
    int k = this.bytecode.currentPc();
    if (paramBoolean)
      this.bytecode.write16bit(i, k - i + 1); 
    boolean bool = compileBooleanExpr(true, aSTree);
    if (bool) {
      this.bytecode.addOpcode(167);
      bool = (this.breakList.size() == 0);
    } 
    this.bytecode.addIndex(j - this.bytecode.currentPc() + 1);
    patchGoto(this.breakList, this.bytecode.currentPc());
    patchGoto(this.continueList, k);
    this.continueList = arrayList2;
    this.breakList = arrayList1;
    this.hasReturned = bool;
  }
  
  protected void patchGoto(ArrayList<Integer> paramArrayList, int paramInt) {
    int i = paramArrayList.size();
    for (byte b = 0; b < i; b++) {
      int j = ((Integer)paramArrayList.get(b)).intValue();
      this.bytecode.write16bit(j, paramInt - j + 1);
    } 
  }
  
  private void atForStmnt(Stmnt paramStmnt) throws CompileError {
    ArrayList arrayList1 = this.breakList;
    ArrayList arrayList2 = this.continueList;
    this.breakList = new ArrayList();
    this.continueList = new ArrayList();
    Stmnt stmnt1 = (Stmnt)paramStmnt.head();
    ASTList aSTList = paramStmnt.tail();
    ASTree aSTree = aSTList.head();
    aSTList = aSTList.tail();
    Stmnt stmnt2 = (Stmnt)aSTList.head();
    Stmnt stmnt3 = (Stmnt)aSTList.tail();
    if (stmnt1 != null)
      stmnt1.accept(this); 
    int i = this.bytecode.currentPc();
    int j = 0;
    if (aSTree != null) {
      if (compileBooleanExpr(false, aSTree)) {
        this.continueList = arrayList2;
        this.breakList = arrayList1;
        this.hasReturned = false;
        return;
      } 
      j = this.bytecode.currentPc();
      this.bytecode.addIndex(0);
    } 
    if (stmnt3 != null)
      stmnt3.accept(this); 
    int k = this.bytecode.currentPc();
    if (stmnt2 != null)
      stmnt2.accept(this); 
    this.bytecode.addOpcode(167);
    this.bytecode.addIndex(i - this.bytecode.currentPc() + 1);
    int m = this.bytecode.currentPc();
    if (aSTree != null)
      this.bytecode.write16bit(j, m - j + 1); 
    patchGoto(this.breakList, m);
    patchGoto(this.continueList, k);
    this.continueList = arrayList2;
    this.breakList = arrayList1;
    this.hasReturned = false;
  }
  
  private void atSwitchStmnt(Stmnt paramStmnt) throws CompileError {
    compileExpr(paramStmnt.head());
    ArrayList arrayList = this.breakList;
    this.breakList = new ArrayList();
    int i = this.bytecode.currentPc();
    this.bytecode.addOpcode(171);
    int j = 3 - (i & 0x3);
    while (j-- > 0)
      this.bytecode.add(0); 
    Stmnt stmnt1 = (Stmnt)paramStmnt.tail();
    byte b1 = 0;
    for (Stmnt stmnt2 = stmnt1; stmnt2 != null; aSTList = stmnt2.tail()) {
      ASTList aSTList;
      if (((Stmnt)stmnt2.head()).getOperator() == 304)
        b1++; 
    } 
    int k = this.bytecode.currentPc();
    this.bytecode.addGap(4);
    this.bytecode.add32bit(b1);
    this.bytecode.addGap(b1 * 8);
    long[] arrayOfLong = new long[b1];
    byte b2 = 0;
    int m = -1;
    for (Stmnt stmnt3 = stmnt1; stmnt3 != null; aSTList = stmnt3.tail()) {
      ASTList aSTList;
      Stmnt stmnt = (Stmnt)stmnt3.head();
      int i2 = stmnt.getOperator();
      if (i2 == 310) {
        m = this.bytecode.currentPc();
      } else if (i2 != 304) {
        fatal();
      } else {
        arrayOfLong[b2++] = (
          computeLabel(stmnt.head()) << 32L) + ((this.bytecode
          .currentPc() - i) & 0xFFFFFFFFFFFFFFFFL);
      } 
      this.hasReturned = false;
      ((Stmnt)stmnt.tail()).accept(this);
    } 
    Arrays.sort(arrayOfLong);
    int n = k + 8;
    int i1;
    for (i1 = 0; i1 < b1; i1++) {
      this.bytecode.write32bit(n, (int)(arrayOfLong[i1] >>> 32L));
      this.bytecode.write32bit(n + 4, (int)arrayOfLong[i1]);
      n += 8;
    } 
    if (m < 0 || this.breakList.size() > 0)
      this.hasReturned = false; 
    i1 = this.bytecode.currentPc();
    if (m < 0)
      m = i1; 
    this.bytecode.write32bit(k, m - i);
    patchGoto(this.breakList, i1);
    this.breakList = arrayList;
  }
  
  private int computeLabel(ASTree paramASTree) throws CompileError {
    doTypeCheck(paramASTree);
    paramASTree = TypeChecker.stripPlusExpr(paramASTree);
    if (paramASTree instanceof IntConst)
      return (int)((IntConst)paramASTree).get(); 
    throw new CompileError("bad case label");
  }
  
  private void atBreakStmnt(Stmnt paramStmnt, boolean paramBoolean) throws CompileError {
    if (paramStmnt.head() != null)
      throw new CompileError("sorry, not support labeled break or continue"); 
    this.bytecode.addOpcode(167);
    Integer integer = new Integer(this.bytecode.currentPc());
    this.bytecode.addIndex(0);
    if (paramBoolean) {
      this.breakList.add(integer);
    } else {
      this.continueList.add(integer);
    } 
  }
  
  protected void atReturnStmnt(Stmnt paramStmnt) throws CompileError {
    atReturnStmnt2(paramStmnt.getLeft());
  }
  
  protected final void atReturnStmnt2(ASTree paramASTree) throws CompileError {
    char c;
    if (paramASTree == null) {
      c = '±';
    } else {
      compileExpr(paramASTree);
      if (this.arrayDim > 0) {
        c = '°';
      } else {
        int i = this.exprType;
        if (i == 312) {
          c = '¯';
        } else if (i == 317) {
          c = '®';
        } else if (i == 326) {
          c = '­';
        } else if (isRefType(i)) {
          c = '°';
        } else {
          c = '¬';
        } 
      } 
    } 
    for (ReturnHook returnHook = this.returnHooks; returnHook != null; returnHook = returnHook.next) {
      if (returnHook.doit(this.bytecode, c)) {
        this.hasReturned = true;
        return;
      } 
    } 
    this.bytecode.addOpcode(c);
    this.hasReturned = true;
  }
  
  private void atThrowStmnt(Stmnt paramStmnt) throws CompileError {
    ASTree aSTree = paramStmnt.getLeft();
    compileExpr(aSTree);
    if (this.exprType != 307 || this.arrayDim > 0)
      throw new CompileError("bad throw statement"); 
    this.bytecode.addOpcode(191);
    this.hasReturned = true;
  }
  
  protected void atTryStmnt(Stmnt paramStmnt) throws CompileError {
    this.hasReturned = false;
  }
  
  private void atSyncStmnt(Stmnt paramStmnt) throws CompileError {
    int i = getListSize(this.breakList);
    int j = getListSize(this.continueList);
    compileExpr(paramStmnt.head());
    if (this.exprType != 307 && this.arrayDim == 0)
      throw new CompileError("bad type expr for synchronized block"); 
    Bytecode1 bytecode1 = this.bytecode;
    final int var = bytecode1.getMaxLocals();
    bytecode1.incMaxLocals(1);
    bytecode1.addOpcode(89);
    bytecode1.addAstore(k);
    bytecode1.addOpcode(194);
    ReturnHook returnHook = new ReturnHook(this) {
        protected boolean doit(Bytecode1 param1Bytecode1, int param1Int) {
          param1Bytecode1.addAload(var);
          param1Bytecode1.addOpcode(195);
          return false;
        }
      };
    int m = bytecode1.currentPc();
    Stmnt stmnt = (Stmnt)paramStmnt.tail();
    if (stmnt != null)
      stmnt.accept(this); 
    int n = bytecode1.currentPc();
    int i1 = 0;
    if (!this.hasReturned) {
      returnHook.doit(bytecode1, 0);
      bytecode1.addOpcode(167);
      i1 = bytecode1.currentPc();
      bytecode1.addIndex(0);
    } 
    if (m < n) {
      int i2 = bytecode1.currentPc();
      returnHook.doit(bytecode1, 0);
      bytecode1.addOpcode(191);
      bytecode1.addExceptionHandler(m, n, i2, 0);
    } 
    if (!this.hasReturned)
      bytecode1.write16bit(i1, bytecode1.currentPc() - i1 + 1); 
    returnHook.remove(this);
    if (getListSize(this.breakList) != i || 
      getListSize(this.continueList) != j)
      throw new CompileError("sorry, cannot break/continue in synchronized block"); 
  }
  
  private static int getListSize(ArrayList paramArrayList) {
    return (paramArrayList == null) ? 0 : paramArrayList.size();
  }
  
  private static boolean isPlusPlusExpr(ASTree paramASTree) {
    if (paramASTree instanceof Expr) {
      int i = ((Expr)paramASTree).getOperator();
      return (i == 362 || i == 363);
    } 
    return false;
  }
  
  public void atDeclarator(Declarator paramDeclarator) throws CompileError {
    boolean bool;
    paramDeclarator.setLocalVar(getMaxLocals());
    paramDeclarator.setClassName(resolveClassName(paramDeclarator.getClassName()));
    if (is2word(paramDeclarator.getType(), paramDeclarator.getArrayDim())) {
      bool = true;
    } else {
      bool = true;
    } 
    incMaxLocals(bool);
    ASTree aSTree = paramDeclarator.getInitializer();
    if (aSTree != null) {
      doTypeCheck(aSTree);
      atVariableAssign(null, 61, null, paramDeclarator, aSTree, false);
    } 
  }
  
  public abstract void atNewExpr(NewExpr paramNewExpr) throws CompileError;
  
  public abstract void atArrayInit(ArrayInit paramArrayInit) throws CompileError;
  
  public void atAssignExpr(AssignExpr paramAssignExpr) throws CompileError {
    atAssignExpr(paramAssignExpr, true);
  }
  
  protected void atAssignExpr(AssignExpr paramAssignExpr, boolean paramBoolean) throws CompileError {
    int i = paramAssignExpr.getOperator();
    ASTree aSTree1 = paramAssignExpr.oprand1();
    ASTree aSTree2 = paramAssignExpr.oprand2();
    if (aSTree1 instanceof Variable) {
      atVariableAssign((Expr)paramAssignExpr, i, (Variable)aSTree1, ((Variable)aSTree1)
          .getDeclarator(), aSTree2, paramBoolean);
    } else {
      if (aSTree1 instanceof Expr) {
        Expr expr = (Expr)aSTree1;
        if (expr.getOperator() == 65) {
          atArrayAssign((Expr)paramAssignExpr, i, (Expr)aSTree1, aSTree2, paramBoolean);
          return;
        } 
      } 
      atFieldAssign((Expr)paramAssignExpr, i, aSTree1, aSTree2, paramBoolean);
    } 
  }
  
  protected static void badAssign(Expr paramExpr) throws CompileError {
    String str;
    if (paramExpr == null) {
      str = "incompatible type for assignment";
    } else {
      str = "incompatible type for " + paramExpr.getName();
    } 
    throw new CompileError(str);
  }
  
  private void atVariableAssign(Expr paramExpr, int paramInt, Variable paramVariable, Declarator paramDeclarator, ASTree paramASTree, boolean paramBoolean) throws CompileError {
    int i = paramDeclarator.getType();
    int j = paramDeclarator.getArrayDim();
    String str = paramDeclarator.getClassName();
    int k = getLocalVar(paramDeclarator);
    if (paramInt != 61)
      atVariable(paramVariable); 
    if (paramExpr == null && paramASTree instanceof ArrayInit) {
      atArrayVariableAssign((ArrayInit)paramASTree, i, j, str);
    } else {
      atAssignCore(paramExpr, paramInt, paramASTree, i, j, str);
    } 
    if (paramBoolean)
      if (is2word(i, j)) {
        this.bytecode.addOpcode(92);
      } else {
        this.bytecode.addOpcode(89);
      }  
    if (j > 0) {
      this.bytecode.addAstore(k);
    } else if (i == 312) {
      this.bytecode.addDstore(k);
    } else if (i == 317) {
      this.bytecode.addFstore(k);
    } else if (i == 326) {
      this.bytecode.addLstore(k);
    } else if (isRefType(i)) {
      this.bytecode.addAstore(k);
    } else {
      this.bytecode.addIstore(k);
    } 
    this.exprType = i;
    this.arrayDim = j;
    this.className = str;
  }
  
  protected abstract void atArrayVariableAssign(ArrayInit paramArrayInit, int paramInt1, int paramInt2, String paramString) throws CompileError;
  
  private void atArrayAssign(Expr paramExpr1, int paramInt, Expr paramExpr2, ASTree paramASTree, boolean paramBoolean) throws CompileError {
    arrayAccess(paramExpr2.oprand1(), paramExpr2.oprand2());
    if (paramInt != 61) {
      this.bytecode.addOpcode(92);
      this.bytecode.addOpcode(getArrayReadOp(this.exprType, this.arrayDim));
    } 
    int i = this.exprType;
    int j = this.arrayDim;
    String str = this.className;
    atAssignCore(paramExpr1, paramInt, paramASTree, i, j, str);
    if (paramBoolean)
      if (is2word(i, j)) {
        this.bytecode.addOpcode(94);
      } else {
        this.bytecode.addOpcode(91);
      }  
    this.bytecode.addOpcode(getArrayWriteOp(i, j));
    this.exprType = i;
    this.arrayDim = j;
    this.className = str;
  }
  
  protected abstract void atFieldAssign(Expr paramExpr, int paramInt, ASTree paramASTree1, ASTree paramASTree2, boolean paramBoolean) throws CompileError;
  
  protected void atAssignCore(Expr paramExpr, int paramInt1, ASTree paramASTree, int paramInt2, int paramInt3, String paramString) throws CompileError {
    if (paramInt1 == 354 && paramInt3 == 0 && paramInt2 == 307) {
      atStringPlusEq(paramExpr, paramInt2, paramInt3, paramString, paramASTree);
    } else {
      paramASTree.accept(this);
      if (invalidDim(this.exprType, this.arrayDim, this.className, paramInt2, paramInt3, paramString, false) || (paramInt1 != 61 && paramInt3 > 0))
        badAssign(paramExpr); 
      if (paramInt1 != 61) {
        int i = assignOps[paramInt1 - 351];
        int j = lookupBinOp(i);
        if (j < 0)
          fatal(); 
        atArithBinExpr(paramExpr, i, j, paramInt2);
      } 
    } 
    if (paramInt1 != 61 || (paramInt3 == 0 && !isRefType(paramInt2)))
      atNumCastExpr(this.exprType, paramInt2); 
  }
  
  private void atStringPlusEq(Expr paramExpr, int paramInt1, int paramInt2, String paramString, ASTree paramASTree) throws CompileError {
    if (!"java/lang/String".equals(paramString))
      badAssign(paramExpr); 
    convToString(paramInt1, paramInt2);
    paramASTree.accept(this);
    convToString(this.exprType, this.arrayDim);
    this.bytecode.addInvokevirtual("java.lang.String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
    this.exprType = 307;
    this.arrayDim = 0;
    this.className = "java/lang/String";
  }
  
  private boolean invalidDim(int paramInt1, int paramInt2, String paramString1, int paramInt3, int paramInt4, String paramString2, boolean paramBoolean) {
    if (paramInt2 != paramInt4) {
      if (paramInt1 == 412)
        return false; 
      if (paramInt4 == 0 && paramInt3 == 307 && "java/lang/Object"
        .equals(paramString2))
        return false; 
      if (paramBoolean && paramInt2 == 0 && paramInt1 == 307 && "java/lang/Object"
        .equals(paramString1))
        return false; 
      return true;
    } 
    return false;
  }
  
  public void atCondExpr(CondExpr paramCondExpr) throws CompileError {
    if (booleanExpr(false, paramCondExpr.condExpr())) {
      paramCondExpr.elseExpr().accept(this);
    } else {
      int i = this.bytecode.currentPc();
      this.bytecode.addIndex(0);
      paramCondExpr.thenExpr().accept(this);
      int j = this.arrayDim;
      this.bytecode.addOpcode(167);
      int k = this.bytecode.currentPc();
      this.bytecode.addIndex(0);
      this.bytecode.write16bit(i, this.bytecode.currentPc() - i + 1);
      paramCondExpr.elseExpr().accept(this);
      if (j != this.arrayDim)
        throw new CompileError("type mismatch in ?:"); 
      this.bytecode.write16bit(k, this.bytecode.currentPc() - k + 1);
    } 
  }
  
  static final int[] binOp = new int[] { 
      43, 99, 98, 97, 96, 45, 103, 102, 101, 100, 
      42, 107, 106, 105, 104, 47, 111, 110, 109, 108, 
      37, 115, 114, 113, 112, 124, 0, 0, 129, 128, 
      94, 0, 0, 131, 130, 38, 0, 0, 127, 126, 
      364, 0, 0, 121, 120, 366, 0, 0, 123, 122, 
      370, 0, 0, 125, 124 };
  
  static int lookupBinOp(int paramInt) {
    int[] arrayOfInt = binOp;
    int i = arrayOfInt.length;
    for (int j = 0; j < i; j += 5) {
      if (arrayOfInt[j] == paramInt)
        return j; 
    } 
    return -1;
  }
  
  public void atBinExpr(BinExpr paramBinExpr) throws CompileError {
    int i = paramBinExpr.getOperator();
    int j = lookupBinOp(i);
    if (j >= 0) {
      paramBinExpr.oprand1().accept(this);
      ASTree aSTree = paramBinExpr.oprand2();
      if (aSTree == null)
        return; 
      int k = this.exprType;
      int m = this.arrayDim;
      String str = this.className;
      aSTree.accept(this);
      if (m != this.arrayDim)
        throw new CompileError("incompatible array types"); 
      if (i == 43 && m == 0 && (k == 307 || this.exprType == 307)) {
        atStringConcatExpr((Expr)paramBinExpr, k, m, str);
      } else {
        atArithBinExpr((Expr)paramBinExpr, i, j, k);
      } 
    } else {
      if (!booleanExpr(true, (ASTree)paramBinExpr)) {
        this.bytecode.addIndex(7);
        this.bytecode.addIconst(0);
        this.bytecode.addOpcode(167);
        this.bytecode.addIndex(4);
      } 
      this.bytecode.addIconst(1);
    } 
  }
  
  private void atArithBinExpr(Expr paramExpr, int paramInt1, int paramInt2, int paramInt3) throws CompileError {
    if (this.arrayDim != 0)
      badTypes(paramExpr); 
    int i = this.exprType;
    if (paramInt1 == 364 || paramInt1 == 366 || paramInt1 == 370) {
      if (i == 324 || i == 334 || i == 306 || i == 303) {
        this.exprType = paramInt3;
      } else {
        badTypes(paramExpr);
      } 
    } else {
      convertOprandTypes(paramInt3, i, paramExpr);
    } 
    int j = typePrecedence(this.exprType);
    if (j >= 0) {
      int k = binOp[paramInt2 + j + 1];
      if (k != 0) {
        if (j == 3 && this.exprType != 301)
          this.exprType = 324; 
        this.bytecode.addOpcode(k);
        return;
      } 
    } 
    badTypes(paramExpr);
  }
  
  private void atStringConcatExpr(Expr paramExpr, int paramInt1, int paramInt2, String paramString) throws CompileError {
    int i = this.exprType;
    int j = this.arrayDim;
    boolean bool = is2word(i, j);
    boolean bool1 = (i == 307 && "java/lang/String".equals(this.className)) ? true : false;
    if (bool)
      convToString(i, j); 
    if (is2word(paramInt1, paramInt2)) {
      this.bytecode.addOpcode(91);
      this.bytecode.addOpcode(87);
    } else {
      this.bytecode.addOpcode(95);
    } 
    convToString(paramInt1, paramInt2);
    this.bytecode.addOpcode(95);
    if (!bool && !bool1)
      convToString(i, j); 
    this.bytecode.addInvokevirtual("java.lang.String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
    this.exprType = 307;
    this.arrayDim = 0;
    this.className = "java/lang/String";
  }
  
  private void convToString(int paramInt1, int paramInt2) throws CompileError {
    String str = "valueOf";
    if (isRefType(paramInt1) || paramInt2 > 0) {
      this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
    } else if (paramInt1 == 312) {
      this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(D)Ljava/lang/String;");
    } else if (paramInt1 == 317) {
      this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(F)Ljava/lang/String;");
    } else if (paramInt1 == 326) {
      this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(J)Ljava/lang/String;");
    } else if (paramInt1 == 301) {
      this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(Z)Ljava/lang/String;");
    } else if (paramInt1 == 306) {
      this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(C)Ljava/lang/String;");
    } else {
      if (paramInt1 == 344)
        throw new CompileError("void type expression"); 
      this.bytecode.addInvokestatic("java.lang.String", "valueOf", "(I)Ljava/lang/String;");
    } 
  }
  
  private boolean booleanExpr(boolean paramBoolean, ASTree paramASTree) throws CompileError {
    int i = getCompOperator(paramASTree);
    if (i == 358) {
      BinExpr binExpr = (BinExpr)paramASTree;
      int j = compileOprands(binExpr);
      compareExpr(paramBoolean, binExpr.getOperator(), j, binExpr);
    } else {
      if (i == 33)
        return booleanExpr(!paramBoolean, ((Expr)paramASTree).oprand1()); 
      boolean bool;
      if ((bool = (i == 369)) || i == 368) {
        BinExpr binExpr = (BinExpr)paramASTree;
        if (booleanExpr(!bool, binExpr.oprand1())) {
          this.exprType = 301;
          this.arrayDim = 0;
          return true;
        } 
        int j = this.bytecode.currentPc();
        this.bytecode.addIndex(0);
        if (booleanExpr(bool, binExpr.oprand2()))
          this.bytecode.addOpcode(167); 
        this.bytecode.write16bit(j, this.bytecode.currentPc() - j + 3);
        if (paramBoolean != bool) {
          this.bytecode.addIndex(6);
          this.bytecode.addOpcode(167);
        } 
      } else {
        if (isAlwaysBranch(paramASTree, paramBoolean)) {
          this.exprType = 301;
          this.arrayDim = 0;
          return true;
        } 
        paramASTree.accept(this);
        if (this.exprType != 301 || this.arrayDim != 0)
          throw new CompileError("boolean expr is required"); 
        this.bytecode.addOpcode(paramBoolean ? 154 : 153);
      } 
    } 
    this.exprType = 301;
    this.arrayDim = 0;
    return false;
  }
  
  private static boolean isAlwaysBranch(ASTree paramASTree, boolean paramBoolean) {
    if (paramASTree instanceof Keyword) {
      int i = ((Keyword)paramASTree).get();
      return paramBoolean ? ((i == 410)) : ((i == 411));
    } 
    return false;
  }
  
  static int getCompOperator(ASTree paramASTree) throws CompileError {
    if (paramASTree instanceof Expr) {
      Expr expr = (Expr)paramASTree;
      int i = expr.getOperator();
      if (i == 33)
        return 33; 
      if (expr instanceof BinExpr && i != 368 && i != 369 && i != 38 && i != 124)
        return 358; 
      return i;
    } 
    return 32;
  }
  
  private int compileOprands(BinExpr paramBinExpr) throws CompileError {
    paramBinExpr.oprand1().accept(this);
    int i = this.exprType;
    int j = this.arrayDim;
    paramBinExpr.oprand2().accept(this);
    if (j != this.arrayDim) {
      if (i != 412 && this.exprType != 412)
        throw new CompileError("incompatible array types"); 
      if (this.exprType == 412)
        this.arrayDim = j; 
    } 
    if (i == 412)
      return this.exprType; 
    return i;
  }
  
  private static final int[] ifOp = new int[] { 
      358, 159, 160, 350, 160, 159, 357, 164, 163, 359, 
      162, 161, 60, 161, 162, 62, 163, 164 };
  
  private static final int[] ifOp2 = new int[] { 
      358, 153, 154, 350, 154, 153, 357, 158, 157, 359, 
      156, 155, 60, 155, 156, 62, 157, 158 };
  
  private static final int P_DOUBLE = 0;
  
  private static final int P_FLOAT = 1;
  
  private static final int P_LONG = 2;
  
  private static final int P_INT = 3;
  
  private static final int P_OTHER = -1;
  
  private void compareExpr(boolean paramBoolean, int paramInt1, int paramInt2, BinExpr paramBinExpr) throws CompileError {
    if (this.arrayDim == 0)
      convertOprandTypes(paramInt2, this.exprType, (Expr)paramBinExpr); 
    int i = typePrecedence(this.exprType);
    if (i == -1 || this.arrayDim > 0) {
      if (paramInt1 == 358) {
        this.bytecode.addOpcode(paramBoolean ? 165 : 166);
      } else if (paramInt1 == 350) {
        this.bytecode.addOpcode(paramBoolean ? 166 : 165);
      } else {
        badTypes((Expr)paramBinExpr);
      } 
    } else if (i == 3) {
      int[] arrayOfInt = ifOp;
      for (byte b = 0; b < arrayOfInt.length; b += 3) {
        if (arrayOfInt[b] == paramInt1) {
          this.bytecode.addOpcode(arrayOfInt[b + (paramBoolean ? 1 : 2)]);
          return;
        } 
      } 
      badTypes((Expr)paramBinExpr);
    } else {
      if (i == 0) {
        if (paramInt1 == 60 || paramInt1 == 357) {
          this.bytecode.addOpcode(152);
        } else {
          this.bytecode.addOpcode(151);
        } 
      } else if (i == 1) {
        if (paramInt1 == 60 || paramInt1 == 357) {
          this.bytecode.addOpcode(150);
        } else {
          this.bytecode.addOpcode(149);
        } 
      } else if (i == 2) {
        this.bytecode.addOpcode(148);
      } else {
        fatal();
      } 
      int[] arrayOfInt = ifOp2;
      for (byte b = 0; b < arrayOfInt.length; b += 3) {
        if (arrayOfInt[b] == paramInt1) {
          this.bytecode.addOpcode(arrayOfInt[b + (paramBoolean ? 1 : 2)]);
          return;
        } 
      } 
      badTypes((Expr)paramBinExpr);
    } 
  }
  
  protected static void badTypes(Expr paramExpr) throws CompileError {
    throw new CompileError("invalid types for " + paramExpr.getName());
  }
  
  protected static boolean isRefType(int paramInt) {
    return (paramInt == 307 || paramInt == 412);
  }
  
  private static int typePrecedence(int paramInt) {
    if (paramInt == 312)
      return 0; 
    if (paramInt == 317)
      return 1; 
    if (paramInt == 326)
      return 2; 
    if (isRefType(paramInt))
      return -1; 
    if (paramInt == 344)
      return -1; 
    return 3;
  }
  
  static boolean isP_INT(int paramInt) {
    return (typePrecedence(paramInt) == 3);
  }
  
  static boolean rightIsStrong(int paramInt1, int paramInt2) {
    int i = typePrecedence(paramInt1);
    int j = typePrecedence(paramInt2);
    return (i >= 0 && j >= 0 && i > j);
  }
  
  private static final int[] castOp = new int[] { 
      0, 144, 143, 142, 141, 0, 140, 139, 138, 137, 
      0, 136, 135, 134, 133, 0 };
  
  private void convertOprandTypes(int paramInt1, int paramInt2, Expr paramExpr) throws CompileError {
    boolean bool;
    int k, m, i = typePrecedence(paramInt1);
    int j = typePrecedence(paramInt2);
    if (j < 0 && i < 0)
      return; 
    if (j < 0 || i < 0)
      badTypes(paramExpr); 
    if (i <= j) {
      bool = false;
      this.exprType = paramInt1;
      k = castOp[j * 4 + i];
      m = i;
    } else {
      bool = true;
      k = castOp[i * 4 + j];
      m = j;
    } 
    if (bool) {
      if (m == 0 || m == 2) {
        if (i == 0 || i == 2) {
          this.bytecode.addOpcode(94);
        } else {
          this.bytecode.addOpcode(93);
        } 
        this.bytecode.addOpcode(88);
        this.bytecode.addOpcode(k);
        this.bytecode.addOpcode(94);
        this.bytecode.addOpcode(88);
      } else if (m == 1) {
        if (i == 2) {
          this.bytecode.addOpcode(91);
          this.bytecode.addOpcode(87);
        } else {
          this.bytecode.addOpcode(95);
        } 
        this.bytecode.addOpcode(k);
        this.bytecode.addOpcode(95);
      } else {
        fatal();
      } 
    } else if (k != 0) {
      this.bytecode.addOpcode(k);
    } 
  }
  
  public void atCastExpr(CastExpr paramCastExpr) throws CompileError {
    String str1 = resolveClassName(paramCastExpr.getClassName());
    String str2 = checkCastExpr(paramCastExpr, str1);
    int i = this.exprType;
    this.exprType = paramCastExpr.getType();
    this.arrayDim = paramCastExpr.getArrayDim();
    this.className = str1;
    if (str2 == null) {
      atNumCastExpr(i, this.exprType);
    } else {
      this.bytecode.addCheckcast(str2);
    } 
  }
  
  public void atInstanceOfExpr(InstanceOfExpr paramInstanceOfExpr) throws CompileError {
    String str1 = resolveClassName(paramInstanceOfExpr.getClassName());
    String str2 = checkCastExpr((CastExpr)paramInstanceOfExpr, str1);
    this.bytecode.addInstanceof(str2);
    this.exprType = 301;
    this.arrayDim = 0;
  }
  
  private String checkCastExpr(CastExpr paramCastExpr, String paramString) throws CompileError {
    String str = "invalid cast";
    ASTree aSTree = paramCastExpr.getOprand();
    int i = paramCastExpr.getArrayDim();
    int j = paramCastExpr.getType();
    aSTree.accept(this);
    int k = this.exprType;
    int m = this.arrayDim;
    if (invalidDim(k, this.arrayDim, this.className, j, i, paramString, true) || k == 344 || j == 344)
      throw new CompileError("invalid cast"); 
    if (j == 307) {
      if (!isRefType(k) && m == 0)
        throw new CompileError("invalid cast"); 
      return toJvmArrayName(paramString, i);
    } 
    if (i > 0)
      return toJvmTypeName(j, i); 
    return null;
  }
  
  void atNumCastExpr(int paramInt1, int paramInt2) throws CompileError {
    boolean bool1, bool2;
    if (paramInt1 == paramInt2)
      return; 
    int i = typePrecedence(paramInt1);
    int j = typePrecedence(paramInt2);
    if (0 <= i && i < 3) {
      bool1 = castOp[i * 4 + j];
    } else {
      bool1 = false;
    } 
    if (paramInt2 == 312) {
      bool2 = true;
    } else if (paramInt2 == 317) {
      bool2 = true;
    } else if (paramInt2 == 326) {
      bool2 = true;
    } else if (paramInt2 == 334) {
      bool2 = true;
    } else if (paramInt2 == 306) {
      bool2 = true;
    } else if (paramInt2 == 303) {
      bool2 = true;
    } else {
      bool2 = false;
    } 
    if (bool1)
      this.bytecode.addOpcode(bool1); 
    if ((!bool1 || bool1 == '' || bool1 == '' || bool1 == '') && 
      bool2)
      this.bytecode.addOpcode(bool2); 
  }
  
  public void atExpr(Expr paramExpr) throws CompileError {
    int i = paramExpr.getOperator();
    ASTree aSTree = paramExpr.oprand1();
    if (i == 46) {
      String str = ((Symbol)paramExpr.oprand2()).get();
      if (str.equals("class")) {
        atClassObject(paramExpr);
      } else {
        atFieldRead((ASTree)paramExpr);
      } 
    } else if (i == 35) {
      atFieldRead((ASTree)paramExpr);
    } else if (i == 65) {
      atArrayRead(aSTree, paramExpr.oprand2());
    } else if (i == 362 || i == 363) {
      atPlusPlus(i, aSTree, paramExpr, true);
    } else if (i == 33) {
      if (!booleanExpr(false, (ASTree)paramExpr)) {
        this.bytecode.addIndex(7);
        this.bytecode.addIconst(1);
        this.bytecode.addOpcode(167);
        this.bytecode.addIndex(4);
      } 
      this.bytecode.addIconst(0);
    } else if (i == 67) {
      fatal();
    } else {
      paramExpr.oprand1().accept(this);
      int j = typePrecedence(this.exprType);
      if (this.arrayDim > 0)
        badType(paramExpr); 
      if (i == 45) {
        if (j == 0) {
          this.bytecode.addOpcode(119);
        } else if (j == 1) {
          this.bytecode.addOpcode(118);
        } else if (j == 2) {
          this.bytecode.addOpcode(117);
        } else if (j == 3) {
          this.bytecode.addOpcode(116);
          this.exprType = 324;
        } else {
          badType(paramExpr);
        } 
      } else if (i == 126) {
        if (j == 3) {
          this.bytecode.addIconst(-1);
          this.bytecode.addOpcode(130);
          this.exprType = 324;
        } else if (j == 2) {
          this.bytecode.addLconst(-1L);
          this.bytecode.addOpcode(131);
        } else {
          badType(paramExpr);
        } 
      } else if (i == 43) {
        if (j == -1)
          badType(paramExpr); 
      } else {
        fatal();
      } 
    } 
  }
  
  protected static void badType(Expr paramExpr) throws CompileError {
    throw new CompileError("invalid type for " + paramExpr.getName());
  }
  
  public abstract void atCallExpr(CallExpr paramCallExpr) throws CompileError;
  
  protected abstract void atFieldRead(ASTree paramASTree) throws CompileError;
  
  public void atClassObject(Expr paramExpr) throws CompileError {
    ASTree aSTree = paramExpr.oprand1();
    if (!(aSTree instanceof Symbol))
      throw new CompileError("fatal error: badly parsed .class expr"); 
    String str = ((Symbol)aSTree).get();
    if (str.startsWith("[")) {
      int i = str.indexOf("[L");
      if (i >= 0) {
        String str1 = str.substring(i + 2, str.length() - 1);
        String str2 = resolveClassName(str1);
        if (!str1.equals(str2)) {
          str2 = MemberResolver.jvmToJavaName(str2);
          StringBuffer stringBuffer = new StringBuffer();
          while (i-- >= 0)
            stringBuffer.append('['); 
          stringBuffer.append('L').append(str2).append(';');
          str = stringBuffer.toString();
        } 
      } 
    } else {
      str = resolveClassName(MemberResolver.javaToJvmName(str));
      str = MemberResolver.jvmToJavaName(str);
    } 
    atClassObject2(str);
    this.exprType = 307;
    this.arrayDim = 0;
    this.className = "java/lang/Class";
  }
  
  protected void atClassObject2(String paramString) throws CompileError {
    int i = this.bytecode.currentPc();
    this.bytecode.addLdc(paramString);
    this.bytecode.addInvokestatic("java.lang.Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;");
    int j = this.bytecode.currentPc();
    this.bytecode.addOpcode(167);
    int k = this.bytecode.currentPc();
    this.bytecode.addIndex(0);
    this.bytecode.addExceptionHandler(i, j, this.bytecode.currentPc(), "java.lang.ClassNotFoundException");
    this.bytecode.growStack(1);
    this.bytecode.addInvokestatic("javassist.runtime.DotClass", "fail", "(Ljava/lang/ClassNotFoundException;)Ljava/lang/NoClassDefFoundError;");
    this.bytecode.addOpcode(191);
    this.bytecode.write16bit(k, this.bytecode.currentPc() - k + 1);
  }
  
  public void atArrayRead(ASTree paramASTree1, ASTree paramASTree2) throws CompileError {
    arrayAccess(paramASTree1, paramASTree2);
    this.bytecode.addOpcode(getArrayReadOp(this.exprType, this.arrayDim));
  }
  
  protected void arrayAccess(ASTree paramASTree1, ASTree paramASTree2) throws CompileError {
    paramASTree1.accept(this);
    int i = this.exprType;
    int j = this.arrayDim;
    if (j == 0)
      throw new CompileError("bad array access"); 
    String str = this.className;
    paramASTree2.accept(this);
    if (typePrecedence(this.exprType) != 3 || this.arrayDim > 0)
      throw new CompileError("bad array index"); 
    this.exprType = i;
    this.arrayDim = j - 1;
    this.className = str;
  }
  
  protected static int getArrayReadOp(int paramInt1, int paramInt2) {
    if (paramInt2 > 0)
      return 50; 
    switch (paramInt1) {
      case 312:
        return 49;
      case 317:
        return 48;
      case 326:
        return 47;
      case 324:
        return 46;
      case 334:
        return 53;
      case 306:
        return 52;
      case 301:
      case 303:
        return 51;
    } 
    return 50;
  }
  
  protected static int getArrayWriteOp(int paramInt1, int paramInt2) {
    if (paramInt2 > 0)
      return 83; 
    switch (paramInt1) {
      case 312:
        return 82;
      case 317:
        return 81;
      case 326:
        return 80;
      case 324:
        return 79;
      case 334:
        return 86;
      case 306:
        return 85;
      case 301:
      case 303:
        return 84;
    } 
    return 83;
  }
  
  private void atPlusPlus(int paramInt, ASTree paramASTree, Expr paramExpr, boolean paramBoolean) throws CompileError {
    boolean bool = (paramASTree == null) ? true : false;
    if (bool)
      paramASTree = paramExpr.oprand2(); 
    if (paramASTree instanceof Variable) {
      Declarator declarator = ((Variable)paramASTree).getDeclarator();
      int i = this.exprType = declarator.getType();
      this.arrayDim = declarator.getArrayDim();
      int j = getLocalVar(declarator);
      if (this.arrayDim > 0)
        badType(paramExpr); 
      if (i == 312) {
        this.bytecode.addDload(j);
        if (paramBoolean && bool)
          this.bytecode.addOpcode(92); 
        this.bytecode.addDconst(1.0D);
        this.bytecode.addOpcode((paramInt == 362) ? 99 : 103);
        if (paramBoolean && !bool)
          this.bytecode.addOpcode(92); 
        this.bytecode.addDstore(j);
      } else if (i == 326) {
        this.bytecode.addLload(j);
        if (paramBoolean && bool)
          this.bytecode.addOpcode(92); 
        this.bytecode.addLconst(1L);
        this.bytecode.addOpcode((paramInt == 362) ? 97 : 101);
        if (paramBoolean && !bool)
          this.bytecode.addOpcode(92); 
        this.bytecode.addLstore(j);
      } else if (i == 317) {
        this.bytecode.addFload(j);
        if (paramBoolean && bool)
          this.bytecode.addOpcode(89); 
        this.bytecode.addFconst(1.0F);
        this.bytecode.addOpcode((paramInt == 362) ? 98 : 102);
        if (paramBoolean && !bool)
          this.bytecode.addOpcode(89); 
        this.bytecode.addFstore(j);
      } else if (i == 303 || i == 306 || i == 334 || i == 324) {
        if (paramBoolean && bool)
          this.bytecode.addIload(j); 
        boolean bool1 = (paramInt == 362) ? true : true;
        if (j > 255) {
          this.bytecode.addOpcode(196);
          this.bytecode.addOpcode(132);
          this.bytecode.addIndex(j);
          this.bytecode.addIndex(bool1);
        } else {
          this.bytecode.addOpcode(132);
          this.bytecode.add(j);
          this.bytecode.add(bool1);
        } 
        if (paramBoolean && !bool)
          this.bytecode.addIload(j); 
      } else {
        badType(paramExpr);
      } 
    } else {
      if (paramASTree instanceof Expr) {
        Expr expr = (Expr)paramASTree;
        if (expr.getOperator() == 65) {
          atArrayPlusPlus(paramInt, bool, expr, paramBoolean);
          return;
        } 
      } 
      atFieldPlusPlus(paramInt, bool, paramASTree, paramExpr, paramBoolean);
    } 
  }
  
  public void atArrayPlusPlus(int paramInt, boolean paramBoolean1, Expr paramExpr, boolean paramBoolean2) throws CompileError {
    arrayAccess(paramExpr.oprand1(), paramExpr.oprand2());
    int i = this.exprType;
    int j = this.arrayDim;
    if (j > 0)
      badType(paramExpr); 
    this.bytecode.addOpcode(92);
    this.bytecode.addOpcode(getArrayReadOp(i, this.arrayDim));
    byte b = is2word(i, j) ? 94 : 91;
    atPlusPlusCore(b, paramBoolean2, paramInt, paramBoolean1, paramExpr);
    this.bytecode.addOpcode(getArrayWriteOp(i, j));
  }
  
  protected void atPlusPlusCore(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, Expr paramExpr) throws CompileError {
    int i = this.exprType;
    if (paramBoolean1 && paramBoolean2)
      this.bytecode.addOpcode(paramInt1); 
    if (i == 324 || i == 303 || i == 306 || i == 334) {
      this.bytecode.addIconst(1);
      this.bytecode.addOpcode((paramInt2 == 362) ? 96 : 100);
      this.exprType = 324;
    } else if (i == 326) {
      this.bytecode.addLconst(1L);
      this.bytecode.addOpcode((paramInt2 == 362) ? 97 : 101);
    } else if (i == 317) {
      this.bytecode.addFconst(1.0F);
      this.bytecode.addOpcode((paramInt2 == 362) ? 98 : 102);
    } else if (i == 312) {
      this.bytecode.addDconst(1.0D);
      this.bytecode.addOpcode((paramInt2 == 362) ? 99 : 103);
    } else {
      badType(paramExpr);
    } 
    if (paramBoolean1 && !paramBoolean2)
      this.bytecode.addOpcode(paramInt1); 
  }
  
  protected abstract void atFieldPlusPlus(int paramInt, boolean paramBoolean1, ASTree paramASTree, Expr paramExpr, boolean paramBoolean2) throws CompileError;
  
  public abstract void atMember(Member paramMember) throws CompileError;
  
  public void atVariable(Variable paramVariable) throws CompileError {
    Declarator declarator = paramVariable.getDeclarator();
    this.exprType = declarator.getType();
    this.arrayDim = declarator.getArrayDim();
    this.className = declarator.getClassName();
    int i = getLocalVar(declarator);
    if (this.arrayDim > 0) {
      this.bytecode.addAload(i);
    } else {
      switch (this.exprType) {
        case 307:
          this.bytecode.addAload(i);
          return;
        case 326:
          this.bytecode.addLload(i);
          return;
        case 317:
          this.bytecode.addFload(i);
          return;
        case 312:
          this.bytecode.addDload(i);
          return;
      } 
      this.bytecode.addIload(i);
    } 
  }
  
  public void atKeyword(Keyword paramKeyword) throws CompileError {
    this.arrayDim = 0;
    int i = paramKeyword.get();
    switch (i) {
      case 410:
        this.bytecode.addIconst(1);
        this.exprType = 301;
        return;
      case 411:
        this.bytecode.addIconst(0);
        this.exprType = 301;
        return;
      case 412:
        this.bytecode.addOpcode(1);
        this.exprType = 412;
        return;
      case 336:
      case 339:
        if (this.inStaticMethod)
          throw new CompileError("not-available: " + ((i == 339) ? "this" : "super")); 
        this.bytecode.addAload(0);
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
    this.bytecode.addLdc(paramStringL.get());
  }
  
  public void atIntConst(IntConst paramIntConst) throws CompileError {
    this.arrayDim = 0;
    long l = paramIntConst.get();
    int i = paramIntConst.getType();
    if (i == 402 || i == 401) {
      this.exprType = (i == 402) ? 324 : 306;
      this.bytecode.addIconst((int)l);
    } else {
      this.exprType = 326;
      this.bytecode.addLconst(l);
    } 
  }
  
  public void atDoubleConst(DoubleConst paramDoubleConst) throws CompileError {
    this.arrayDim = 0;
    if (paramDoubleConst.getType() == 405) {
      this.exprType = 312;
      this.bytecode.addDconst(paramDoubleConst.get());
    } else {
      this.exprType = 317;
      this.bytecode.addFconst((float)paramDoubleConst.get());
    } 
  }
}
