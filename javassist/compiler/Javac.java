package javassist.compiler;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode1;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.CallExpr;
import javassist.compiler.ast.Declarator;
import javassist.compiler.ast.Expr;
import javassist.compiler.ast.FieldDecl;
import javassist.compiler.ast.Member;
import javassist.compiler.ast.MethodDecl;
import javassist.compiler.ast.Stmnt;
import javassist.compiler.ast.Symbol;

public class Javac {
  JvstCodeGen gen;
  
  SymbolTable stable;
  
  private Bytecode1 bytecode;
  
  public static final String param0Name = "$0";
  
  public static final String resultVarName = "$_";
  
  public static final String proceedName = "$proceed";
  
  public Javac(CtClass paramCtClass) {
    this(new Bytecode1(paramCtClass.getClassFile2().getConstPool(), 0, 0), paramCtClass);
  }
  
  public Javac(Bytecode1 paramBytecode1, CtClass paramCtClass) {
    this.gen = new JvstCodeGen(paramBytecode1, paramCtClass, paramCtClass.getClassPool());
    this.stable = new SymbolTable();
    this.bytecode = paramBytecode1;
  }
  
  public Bytecode1 getBytecode() {
    return this.bytecode;
  }
  
  public CtMember compile(String paramString) throws CompileError {
    Parser parser = new Parser(new Lex(paramString));
    ASTList aSTList = parser.parseMember1(this.stable);
    try {
      if (aSTList instanceof FieldDecl)
        return (CtMember)compileField((FieldDecl)aSTList); 
      CtBehavior ctBehavior = compileMethod(parser, (MethodDecl)aSTList);
      CtClass ctClass = ctBehavior.getDeclaringClass();
      ctBehavior.getMethodInfo2()
        .rebuildStackMapIf6(ctClass.getClassPool(), ctClass
          .getClassFile2());
      return (CtMember)ctBehavior;
    } catch (BadBytecode badBytecode) {
      throw new CompileError(badBytecode.getMessage());
    } catch (CannotCompileException cannotCompileException) {
      throw new CompileError(cannotCompileException.getMessage());
    } 
  }
  
  public static class CtFieldWithInit extends CtField {
    private ASTree init;
    
    CtFieldWithInit(CtClass param1CtClass1, String param1String, CtClass param1CtClass2) throws CannotCompileException {
      super(param1CtClass1, param1String, param1CtClass2);
      this.init = null;
    }
    
    protected void setInit(ASTree param1ASTree) {
      this.init = param1ASTree;
    }
    
    protected ASTree getInitAST() {
      return this.init;
    }
  }
  
  private CtField compileField(FieldDecl paramFieldDecl) throws CompileError, CannotCompileException {
    Declarator declarator = paramFieldDecl.getDeclarator();
    CtFieldWithInit ctFieldWithInit = new CtFieldWithInit(this.gen.resolver.lookupClass(declarator), declarator.getVariable().get(), this.gen.getThisClass());
    ctFieldWithInit.setModifiers(MemberResolver.getModifiers(paramFieldDecl.getModifiers()));
    if (paramFieldDecl.getInit() != null)
      ctFieldWithInit.setInit(paramFieldDecl.getInit()); 
    return ctFieldWithInit;
  }
  
  private CtBehavior compileMethod(Parser paramParser, MethodDecl paramMethodDecl) throws CompileError {
    int i = MemberResolver.getModifiers(paramMethodDecl.getModifiers());
    CtClass[] arrayOfCtClass1 = this.gen.makeParamList(paramMethodDecl);
    CtClass[] arrayOfCtClass2 = this.gen.makeThrowsList(paramMethodDecl);
    recordParams(arrayOfCtClass1, Modifier.isStatic(i));
    paramMethodDecl = paramParser.parseMethod2(this.stable, paramMethodDecl);
    try {
      if (paramMethodDecl.isConstructor()) {
        CtConstructor ctConstructor = new CtConstructor(arrayOfCtClass1, this.gen.getThisClass());
        ctConstructor.setModifiers(i);
        paramMethodDecl.accept(this.gen);
        ctConstructor.getMethodInfo().setCodeAttribute(this.bytecode
            .toCodeAttribute());
        ctConstructor.setExceptionTypes(arrayOfCtClass2);
        return (CtBehavior)ctConstructor;
      } 
      Declarator declarator = paramMethodDecl.getReturn();
      CtClass ctClass = this.gen.resolver.lookupClass(declarator);
      recordReturnType(ctClass, false);
      CtMethod ctMethod = new CtMethod(ctClass, declarator.getVariable().get(), arrayOfCtClass1, this.gen.getThisClass());
      ctMethod.setModifiers(i);
      this.gen.setThisMethod(ctMethod);
      paramMethodDecl.accept(this.gen);
      if (paramMethodDecl.getBody() != null) {
        ctMethod.getMethodInfo().setCodeAttribute(this.bytecode
            .toCodeAttribute());
      } else {
        ctMethod.setModifiers(i | 0x400);
      } 
      ctMethod.setExceptionTypes(arrayOfCtClass2);
      return (CtBehavior)ctMethod;
    } catch (NotFoundException notFoundException) {
      throw new CompileError(notFoundException.toString());
    } 
  }
  
  public Bytecode1 compileBody(CtBehavior paramCtBehavior, String paramString) throws CompileError {
    try {
      CtClass ctClass;
      int i = paramCtBehavior.getModifiers();
      recordParams(paramCtBehavior.getParameterTypes(), Modifier.isStatic(i));
      if (paramCtBehavior instanceof CtMethod) {
        this.gen.setThisMethod((CtMethod)paramCtBehavior);
        ctClass = ((CtMethod)paramCtBehavior).getReturnType();
      } else {
        ctClass = CtClass.voidType;
      } 
      recordReturnType(ctClass, false);
      boolean bool = (ctClass == CtClass.voidType) ? true : false;
      if (paramString == null) {
        makeDefaultBody(this.bytecode, ctClass);
      } else {
        Parser parser = new Parser(new Lex(paramString));
        SymbolTable symbolTable = new SymbolTable(this.stable);
        Stmnt stmnt = parser.parseStatement(symbolTable);
        if (parser.hasMore())
          throw new CompileError("the method/constructor body must be surrounded by {}"); 
        boolean bool1 = false;
        if (paramCtBehavior instanceof CtConstructor)
          bool1 = !((CtConstructor)paramCtBehavior).isClassInitializer() ? true : false; 
        this.gen.atMethodBody(stmnt, bool1, bool);
      } 
      return this.bytecode;
    } catch (NotFoundException notFoundException) {
      throw new CompileError(notFoundException.toString());
    } 
  }
  
  private static void makeDefaultBody(Bytecode1 paramBytecode1, CtClass paramCtClass) {
    char c;
    boolean bool;
    if (paramCtClass instanceof CtPrimitiveType) {
      CtPrimitiveType ctPrimitiveType = (CtPrimitiveType)paramCtClass;
      c = ctPrimitiveType.getReturnOp();
      if (c == '¯') {
        bool = true;
      } else if (c == '®') {
        bool = true;
      } else if (c == '­') {
        bool = true;
      } else if (c == '±') {
        bool = false;
      } else {
        bool = true;
      } 
    } else {
      c = '°';
      bool = true;
    } 
    if (bool)
      paramBytecode1.addOpcode(bool); 
    paramBytecode1.addOpcode(c);
  }
  
  public boolean recordLocalVariables(CodeAttribute paramCodeAttribute, int paramInt) throws CompileError {
    LocalVariableAttribute localVariableAttribute = (LocalVariableAttribute)paramCodeAttribute.getAttribute("LocalVariableTable");
    if (localVariableAttribute == null)
      return false; 
    int i = localVariableAttribute.tableLength();
    for (byte b = 0; b < i; b++) {
      int j = localVariableAttribute.startPc(b);
      int k = localVariableAttribute.codeLength(b);
      if (j <= paramInt && paramInt < j + k)
        this.gen.recordVariable(localVariableAttribute.descriptor(b), localVariableAttribute.variableName(b), localVariableAttribute
            .index(b), this.stable); 
    } 
    return true;
  }
  
  public boolean recordParamNames(CodeAttribute paramCodeAttribute, int paramInt) throws CompileError {
    LocalVariableAttribute localVariableAttribute = (LocalVariableAttribute)paramCodeAttribute.getAttribute("LocalVariableTable");
    if (localVariableAttribute == null)
      return false; 
    int i = localVariableAttribute.tableLength();
    for (byte b = 0; b < i; b++) {
      int j = localVariableAttribute.index(b);
      if (j < paramInt)
        this.gen.recordVariable(localVariableAttribute.descriptor(b), localVariableAttribute.variableName(b), j, this.stable); 
    } 
    return true;
  }
  
  public int recordParams(CtClass[] paramArrayOfCtClass, boolean paramBoolean) throws CompileError {
    return this.gen.recordParams(paramArrayOfCtClass, paramBoolean, "$", "$args", "$$", this.stable);
  }
  
  public int recordParams(String paramString, CtClass[] paramArrayOfCtClass, boolean paramBoolean1, int paramInt, boolean paramBoolean2) throws CompileError {
    return this.gen.recordParams(paramArrayOfCtClass, paramBoolean2, "$", "$args", "$$", paramBoolean1, paramInt, paramString, this.stable);
  }
  
  public void setMaxLocals(int paramInt) {
    this.gen.setMaxLocals(paramInt);
  }
  
  public int recordReturnType(CtClass paramCtClass, boolean paramBoolean) throws CompileError {
    this.gen.recordType(paramCtClass);
    return this.gen.recordReturnType(paramCtClass, "$r", paramBoolean ? "$_" : null, this.stable);
  }
  
  public void recordType(CtClass paramCtClass) {
    this.gen.recordType(paramCtClass);
  }
  
  public int recordVariable(CtClass paramCtClass, String paramString) throws CompileError {
    return this.gen.recordVariable(paramCtClass, paramString, this.stable);
  }
  
  public void recordProceed(String paramString1, String paramString2) throws CompileError {
    Parser parser = new Parser(new Lex(paramString1));
    final ASTree texpr = parser.parseExpression(this.stable);
    final String m = paramString2;
    ProceedHandler proceedHandler = new ProceedHandler() {
        public void doit(JvstCodeGen param1JvstCodeGen, Bytecode1 param1Bytecode1, ASTList param1ASTList) throws CompileError {
          Expr expr;
          Member member = new Member(m);
          if (texpr != null)
            expr = Expr.make(46, texpr, (ASTree)member); 
          CallExpr callExpr = CallExpr.makeCall((ASTree)expr, (ASTree)param1ASTList);
          param1JvstCodeGen.compileExpr((ASTree)callExpr);
          param1JvstCodeGen.addNullIfVoid();
        }
        
        public void setReturnType(JvstTypeChecker param1JvstTypeChecker, ASTList param1ASTList) throws CompileError {
          Expr expr;
          Member member = new Member(m);
          if (texpr != null)
            expr = Expr.make(46, texpr, (ASTree)member); 
          CallExpr callExpr = CallExpr.makeCall((ASTree)expr, (ASTree)param1ASTList);
          callExpr.accept(param1JvstTypeChecker);
          param1JvstTypeChecker.addNullIfVoid();
        }
      };
    this.gen.setProceedHandler(proceedHandler, "$proceed");
  }
  
  public void recordStaticProceed(String paramString1, String paramString2) throws CompileError {
    final String c = paramString1;
    final String m = paramString2;
    ProceedHandler proceedHandler = new ProceedHandler() {
        public void doit(JvstCodeGen param1JvstCodeGen, Bytecode1 param1Bytecode1, ASTList param1ASTList) throws CompileError {
          Expr expr = Expr.make(35, (ASTree)new Symbol(c), (ASTree)new Member(m));
          CallExpr callExpr = CallExpr.makeCall((ASTree)expr, (ASTree)param1ASTList);
          param1JvstCodeGen.compileExpr((ASTree)callExpr);
          param1JvstCodeGen.addNullIfVoid();
        }
        
        public void setReturnType(JvstTypeChecker param1JvstTypeChecker, ASTList param1ASTList) throws CompileError {
          Expr expr = Expr.make(35, (ASTree)new Symbol(c), (ASTree)new Member(m));
          CallExpr callExpr = CallExpr.makeCall((ASTree)expr, (ASTree)param1ASTList);
          callExpr.accept(param1JvstTypeChecker);
          param1JvstTypeChecker.addNullIfVoid();
        }
      };
    this.gen.setProceedHandler(proceedHandler, "$proceed");
  }
  
  public void recordSpecialProceed(String paramString1, final String classname, final String methodname, final String descriptor, final int methodIndex) throws CompileError {
    Parser parser = new Parser(new Lex(paramString1));
    final ASTree texpr = parser.parseExpression(this.stable);
    ProceedHandler proceedHandler = new ProceedHandler() {
        public void doit(JvstCodeGen param1JvstCodeGen, Bytecode1 param1Bytecode1, ASTList param1ASTList) throws CompileError {
          param1JvstCodeGen.compileInvokeSpecial(texpr, methodIndex, descriptor, param1ASTList);
        }
        
        public void setReturnType(JvstTypeChecker param1JvstTypeChecker, ASTList param1ASTList) throws CompileError {
          param1JvstTypeChecker.compileInvokeSpecial(texpr, classname, methodname, descriptor, param1ASTList);
        }
      };
    this.gen.setProceedHandler(proceedHandler, "$proceed");
  }
  
  public void recordProceed(ProceedHandler paramProceedHandler) {
    this.gen.setProceedHandler(paramProceedHandler, "$proceed");
  }
  
  public void compileStmnt(String paramString) throws CompileError {
    Parser parser = new Parser(new Lex(paramString));
    SymbolTable symbolTable = new SymbolTable(this.stable);
    while (parser.hasMore()) {
      Stmnt stmnt = parser.parseStatement(symbolTable);
      if (stmnt != null)
        stmnt.accept(this.gen); 
    } 
  }
  
  public void compileExpr(String paramString) throws CompileError {
    ASTree aSTree = parseExpr(paramString, this.stable);
    compileExpr(aSTree);
  }
  
  public static ASTree parseExpr(String paramString, SymbolTable paramSymbolTable) throws CompileError {
    Parser parser = new Parser(new Lex(paramString));
    return parser.parseExpression(paramSymbolTable);
  }
  
  public void compileExpr(ASTree paramASTree) throws CompileError {
    if (paramASTree != null)
      this.gen.compileExpr(paramASTree); 
  }
}
