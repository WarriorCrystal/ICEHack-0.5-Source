package javassist.compiler;

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

public final class Parser implements TokenId {
  private Lex lex;
  
  public Parser(Lex paramLex) {
    this.lex = paramLex;
  }
  
  public boolean hasMore() {
    return (this.lex.lookAhead() >= 0);
  }
  
  public ASTList parseMember(SymbolTable paramSymbolTable) throws CompileError {
    ASTList aSTList = parseMember1(paramSymbolTable);
    if (aSTList instanceof MethodDecl)
      return (ASTList)parseMethod2(paramSymbolTable, (MethodDecl)aSTList); 
    return aSTList;
  }
  
  public ASTList parseMember1(SymbolTable paramSymbolTable) throws CompileError {
    Declarator declarator;
    String str;
    ASTList aSTList = parseMemberMods();
    boolean bool = false;
    if (this.lex.lookAhead() == 400 && this.lex.lookAhead(1) == 40) {
      declarator = new Declarator(344, 0);
      bool = true;
    } else {
      declarator = parseFormalType(paramSymbolTable);
    } 
    if (this.lex.get() != 400)
      throw new SyntaxError(this.lex); 
    if (bool) {
      str = "<init>";
    } else {
      str = this.lex.getString();
    } 
    declarator.setVariable(new Symbol(str));
    if (bool || this.lex.lookAhead() == 40)
      return (ASTList)parseMethod1(paramSymbolTable, bool, aSTList, declarator); 
    return (ASTList)parseField(paramSymbolTable, aSTList, declarator);
  }
  
  private FieldDecl parseField(SymbolTable paramSymbolTable, ASTList paramASTList, Declarator paramDeclarator) throws CompileError {
    ASTree aSTree = null;
    if (this.lex.lookAhead() == 61) {
      this.lex.get();
      aSTree = parseExpression(paramSymbolTable);
    } 
    int i = this.lex.get();
    if (i == 59)
      return new FieldDecl((ASTree)paramASTList, new ASTList((ASTree)paramDeclarator, new ASTList(aSTree))); 
    if (i == 44)
      throw new CompileError("only one field can be declared in one declaration", this.lex); 
    throw new SyntaxError(this.lex);
  }
  
  private MethodDecl parseMethod1(SymbolTable paramSymbolTable, boolean paramBoolean, ASTList paramASTList, Declarator paramDeclarator) throws CompileError {
    if (this.lex.get() != 40)
      throw new SyntaxError(this.lex); 
    ASTList aSTList1 = null;
    if (this.lex.lookAhead() != 41)
      while (true) {
        aSTList1 = ASTList.append(aSTList1, (ASTree)parseFormalParam(paramSymbolTable));
        int i = this.lex.lookAhead();
        if (i == 44) {
          this.lex.get();
          continue;
        } 
        if (i == 41)
          break; 
      }  
    this.lex.get();
    paramDeclarator.addArrayDim(parseArrayDimension());
    if (paramBoolean && paramDeclarator.getArrayDim() > 0)
      throw new SyntaxError(this.lex); 
    ASTList aSTList2 = null;
    if (this.lex.lookAhead() == 341) {
      this.lex.get();
      while (true) {
        aSTList2 = ASTList.append(aSTList2, (ASTree)parseClassType(paramSymbolTable));
        if (this.lex.lookAhead() == 44) {
          this.lex.get();
          continue;
        } 
        break;
      } 
    } 
    return new MethodDecl((ASTree)paramASTList, new ASTList((ASTree)paramDeclarator, 
          ASTList.make((ASTree)aSTList1, (ASTree)aSTList2, null)));
  }
  
  public MethodDecl parseMethod2(SymbolTable paramSymbolTable, MethodDecl paramMethodDecl) throws CompileError {
    Stmnt stmnt = null;
    if (this.lex.lookAhead() == 59) {
      this.lex.get();
    } else {
      stmnt = parseBlock(paramSymbolTable);
      if (stmnt == null)
        stmnt = new Stmnt(66); 
    } 
    paramMethodDecl.sublist(4).setHead((ASTree)stmnt);
    return paramMethodDecl;
  }
  
  private ASTList parseMemberMods() {
    ASTList aSTList = null;
    while (true) {
      int i = this.lex.lookAhead();
      if (i == 300 || i == 315 || i == 332 || i == 331 || i == 330 || i == 338 || i == 335 || i == 345 || i == 342 || i == 347) {
        aSTList = new ASTList((ASTree)new Keyword(this.lex.get()), aSTList);
        continue;
      } 
      break;
    } 
    return aSTList;
  }
  
  private Declarator parseFormalType(SymbolTable paramSymbolTable) throws CompileError {
    int i = this.lex.lookAhead();
    if (isBuiltinType(i) || i == 344) {
      this.lex.get();
      int k = parseArrayDimension();
      return new Declarator(i, k);
    } 
    ASTList aSTList = parseClassType(paramSymbolTable);
    int j = parseArrayDimension();
    return new Declarator(aSTList, j);
  }
  
  private static boolean isBuiltinType(int paramInt) {
    return (paramInt == 301 || paramInt == 303 || paramInt == 306 || paramInt == 334 || paramInt == 324 || paramInt == 326 || paramInt == 317 || paramInt == 312);
  }
  
  private Declarator parseFormalParam(SymbolTable paramSymbolTable) throws CompileError {
    Declarator declarator = parseFormalType(paramSymbolTable);
    if (this.lex.get() != 400)
      throw new SyntaxError(this.lex); 
    String str = this.lex.getString();
    declarator.setVariable(new Symbol(str));
    declarator.addArrayDim(parseArrayDimension());
    paramSymbolTable.append(str, declarator);
    return declarator;
  }
  
  public Stmnt parseStatement(SymbolTable paramSymbolTable) throws CompileError {
    int i = this.lex.lookAhead();
    if (i == 123)
      return parseBlock(paramSymbolTable); 
    if (i == 59) {
      this.lex.get();
      return new Stmnt(66);
    } 
    if (i == 400 && this.lex.lookAhead(1) == 58) {
      this.lex.get();
      String str = this.lex.getString();
      this.lex.get();
      return Stmnt.make(76, (ASTree)new Symbol(str), (ASTree)parseStatement(paramSymbolTable));
    } 
    if (i == 320)
      return parseIf(paramSymbolTable); 
    if (i == 346)
      return parseWhile(paramSymbolTable); 
    if (i == 311)
      return parseDo(paramSymbolTable); 
    if (i == 318)
      return parseFor(paramSymbolTable); 
    if (i == 343)
      return parseTry(paramSymbolTable); 
    if (i == 337)
      return parseSwitch(paramSymbolTable); 
    if (i == 338)
      return parseSynchronized(paramSymbolTable); 
    if (i == 333)
      return parseReturn(paramSymbolTable); 
    if (i == 340)
      return parseThrow(paramSymbolTable); 
    if (i == 302)
      return parseBreak(paramSymbolTable); 
    if (i == 309)
      return parseContinue(paramSymbolTable); 
    return parseDeclarationOrExpression(paramSymbolTable, false);
  }
  
  private Stmnt parseBlock(SymbolTable paramSymbolTable) throws CompileError {
    if (this.lex.get() != 123)
      throw new SyntaxError(this.lex); 
    Stmnt stmnt = null;
    SymbolTable symbolTable = new SymbolTable(paramSymbolTable);
    while (this.lex.lookAhead() != 125) {
      Stmnt stmnt1 = parseStatement(symbolTable);
      if (stmnt1 != null)
        stmnt = (Stmnt)ASTList.concat((ASTList)stmnt, (ASTList)new Stmnt(66, (ASTree)stmnt1)); 
    } 
    this.lex.get();
    if (stmnt == null)
      return new Stmnt(66); 
    return stmnt;
  }
  
  private Stmnt parseIf(SymbolTable paramSymbolTable) throws CompileError {
    ASTree aSTree2;
    int i = this.lex.get();
    ASTree aSTree1 = parseParExpression(paramSymbolTable);
    Stmnt stmnt = parseStatement(paramSymbolTable);
    if (this.lex.lookAhead() == 313) {
      this.lex.get();
      aSTree2 = (ASTree)parseStatement(paramSymbolTable);
    } else {
      aSTree2 = null;
    } 
    return new Stmnt(i, aSTree1, new ASTList((ASTree)stmnt, new ASTList(aSTree2)));
  }
  
  private Stmnt parseWhile(SymbolTable paramSymbolTable) throws CompileError {
    int i = this.lex.get();
    ASTree aSTree = parseParExpression(paramSymbolTable);
    Stmnt stmnt = parseStatement(paramSymbolTable);
    return new Stmnt(i, aSTree, (ASTList)stmnt);
  }
  
  private Stmnt parseDo(SymbolTable paramSymbolTable) throws CompileError {
    int i = this.lex.get();
    Stmnt stmnt = parseStatement(paramSymbolTable);
    if (this.lex.get() != 346 || this.lex.get() != 40)
      throw new SyntaxError(this.lex); 
    ASTree aSTree = parseExpression(paramSymbolTable);
    if (this.lex.get() != 41 || this.lex.get() != 59)
      throw new SyntaxError(this.lex); 
    return new Stmnt(i, aSTree, (ASTList)stmnt);
  }
  
  private Stmnt parseFor(SymbolTable paramSymbolTable) throws CompileError {
    Stmnt stmnt1, stmnt2;
    ASTree aSTree;
    int i = this.lex.get();
    SymbolTable symbolTable = new SymbolTable(paramSymbolTable);
    if (this.lex.get() != 40)
      throw new SyntaxError(this.lex); 
    if (this.lex.lookAhead() == 59) {
      this.lex.get();
      stmnt1 = null;
    } else {
      stmnt1 = parseDeclarationOrExpression(symbolTable, true);
    } 
    if (this.lex.lookAhead() == 59) {
      aSTree = null;
    } else {
      aSTree = parseExpression(symbolTable);
    } 
    if (this.lex.get() != 59)
      throw new CompileError("; is missing", this.lex); 
    if (this.lex.lookAhead() == 41) {
      stmnt2 = null;
    } else {
      stmnt2 = parseExprList(symbolTable);
    } 
    if (this.lex.get() != 41)
      throw new CompileError(") is missing", this.lex); 
    Stmnt stmnt3 = parseStatement(symbolTable);
    return new Stmnt(i, (ASTree)stmnt1, new ASTList(aSTree, new ASTList((ASTree)stmnt2, (ASTList)stmnt3)));
  }
  
  private Stmnt parseSwitch(SymbolTable paramSymbolTable) throws CompileError {
    int i = this.lex.get();
    ASTree aSTree = parseParExpression(paramSymbolTable);
    Stmnt stmnt = parseSwitchBlock(paramSymbolTable);
    return new Stmnt(i, aSTree, (ASTList)stmnt);
  }
  
  private Stmnt parseSwitchBlock(SymbolTable paramSymbolTable) throws CompileError {
    if (this.lex.get() != 123)
      throw new SyntaxError(this.lex); 
    SymbolTable symbolTable = new SymbolTable(paramSymbolTable);
    Stmnt stmnt1 = parseStmntOrCase(symbolTable);
    if (stmnt1 == null)
      throw new CompileError("empty switch block", this.lex); 
    int i = stmnt1.getOperator();
    if (i != 304 && i != 310)
      throw new CompileError("no case or default in a switch block", this.lex); 
    Stmnt stmnt2 = new Stmnt(66, (ASTree)stmnt1);
    while (this.lex.lookAhead() != 125) {
      Stmnt stmnt = parseStmntOrCase(symbolTable);
      if (stmnt != null) {
        int j = stmnt.getOperator();
        if (j == 304 || j == 310) {
          stmnt2 = (Stmnt)ASTList.concat((ASTList)stmnt2, (ASTList)new Stmnt(66, (ASTree)stmnt));
          stmnt1 = stmnt;
          continue;
        } 
        stmnt1 = (Stmnt)ASTList.concat((ASTList)stmnt1, (ASTList)new Stmnt(66, (ASTree)stmnt));
      } 
    } 
    this.lex.get();
    return stmnt2;
  }
  
  private Stmnt parseStmntOrCase(SymbolTable paramSymbolTable) throws CompileError {
    Stmnt stmnt;
    int i = this.lex.lookAhead();
    if (i != 304 && i != 310)
      return parseStatement(paramSymbolTable); 
    this.lex.get();
    if (i == 304) {
      stmnt = new Stmnt(i, parseExpression(paramSymbolTable));
    } else {
      stmnt = new Stmnt(310);
    } 
    if (this.lex.get() != 58)
      throw new CompileError(": is missing", this.lex); 
    return stmnt;
  }
  
  private Stmnt parseSynchronized(SymbolTable paramSymbolTable) throws CompileError {
    int i = this.lex.get();
    if (this.lex.get() != 40)
      throw new SyntaxError(this.lex); 
    ASTree aSTree = parseExpression(paramSymbolTable);
    if (this.lex.get() != 41)
      throw new SyntaxError(this.lex); 
    Stmnt stmnt = parseBlock(paramSymbolTable);
    return new Stmnt(i, aSTree, (ASTList)stmnt);
  }
  
  private Stmnt parseTry(SymbolTable paramSymbolTable) throws CompileError {
    this.lex.get();
    Stmnt stmnt1 = parseBlock(paramSymbolTable);
    ASTList aSTList = null;
    while (this.lex.lookAhead() == 305) {
      this.lex.get();
      if (this.lex.get() != 40)
        throw new SyntaxError(this.lex); 
      SymbolTable symbolTable = new SymbolTable(paramSymbolTable);
      Declarator declarator = parseFormalParam(symbolTable);
      if (declarator.getArrayDim() > 0 || declarator.getType() != 307)
        throw new SyntaxError(this.lex); 
      if (this.lex.get() != 41)
        throw new SyntaxError(this.lex); 
      Stmnt stmnt = parseBlock(symbolTable);
      aSTList = ASTList.append(aSTList, (ASTree)new Pair((ASTree)declarator, (ASTree)stmnt));
    } 
    Stmnt stmnt2 = null;
    if (this.lex.lookAhead() == 316) {
      this.lex.get();
      stmnt2 = parseBlock(paramSymbolTable);
    } 
    return Stmnt.make(343, (ASTree)stmnt1, (ASTree)aSTList, (ASTree)stmnt2);
  }
  
  private Stmnt parseReturn(SymbolTable paramSymbolTable) throws CompileError {
    int i = this.lex.get();
    Stmnt stmnt = new Stmnt(i);
    if (this.lex.lookAhead() != 59)
      stmnt.setLeft(parseExpression(paramSymbolTable)); 
    if (this.lex.get() != 59)
      throw new CompileError("; is missing", this.lex); 
    return stmnt;
  }
  
  private Stmnt parseThrow(SymbolTable paramSymbolTable) throws CompileError {
    int i = this.lex.get();
    ASTree aSTree = parseExpression(paramSymbolTable);
    if (this.lex.get() != 59)
      throw new CompileError("; is missing", this.lex); 
    return new Stmnt(i, aSTree);
  }
  
  private Stmnt parseBreak(SymbolTable paramSymbolTable) throws CompileError {
    return parseContinue(paramSymbolTable);
  }
  
  private Stmnt parseContinue(SymbolTable paramSymbolTable) throws CompileError {
    int i = this.lex.get();
    Stmnt stmnt = new Stmnt(i);
    int j = this.lex.get();
    if (j == 400) {
      stmnt.setLeft((ASTree)new Symbol(this.lex.getString()));
      j = this.lex.get();
    } 
    if (j != 59)
      throw new CompileError("; is missing", this.lex); 
    return stmnt;
  }
  
  private Stmnt parseDeclarationOrExpression(SymbolTable paramSymbolTable, boolean paramBoolean) throws CompileError {
    Stmnt stmnt;
    int i = this.lex.lookAhead();
    while (i == 315) {
      this.lex.get();
      i = this.lex.lookAhead();
    } 
    if (isBuiltinType(i)) {
      i = this.lex.get();
      int j = parseArrayDimension();
      return parseDeclarators(paramSymbolTable, new Declarator(i, j));
    } 
    if (i == 400) {
      int j = nextIsClassType(0);
      if (j >= 0 && 
        this.lex.lookAhead(j) == 400) {
        ASTList aSTList = parseClassType(paramSymbolTable);
        int k = parseArrayDimension();
        return parseDeclarators(paramSymbolTable, new Declarator(aSTList, k));
      } 
    } 
    if (paramBoolean) {
      stmnt = parseExprList(paramSymbolTable);
    } else {
      stmnt = new Stmnt(69, parseExpression(paramSymbolTable));
    } 
    if (this.lex.get() != 59)
      throw new CompileError("; is missing", this.lex); 
    return stmnt;
  }
  
  private Stmnt parseExprList(SymbolTable paramSymbolTable) throws CompileError {
    Stmnt stmnt = null;
    while (true) {
      Stmnt stmnt1 = new Stmnt(69, parseExpression(paramSymbolTable));
      stmnt = (Stmnt)ASTList.concat((ASTList)stmnt, (ASTList)new Stmnt(66, (ASTree)stmnt1));
      if (this.lex.lookAhead() == 44) {
        this.lex.get();
        continue;
      } 
      break;
    } 
    return stmnt;
  }
  
  private Stmnt parseDeclarators(SymbolTable paramSymbolTable, Declarator paramDeclarator) throws CompileError {
    Stmnt stmnt = null;
    while (true) {
      stmnt = (Stmnt)ASTList.concat((ASTList)stmnt, (ASTList)new Stmnt(68, (ASTree)
            parseDeclarator(paramSymbolTable, paramDeclarator)));
      int i = this.lex.get();
      if (i == 59)
        return stmnt; 
      if (i != 44)
        throw new CompileError("; is missing", this.lex); 
    } 
  }
  
  private Declarator parseDeclarator(SymbolTable paramSymbolTable, Declarator paramDeclarator) throws CompileError {
    if (this.lex.get() != 400 || paramDeclarator.getType() == 344)
      throw new SyntaxError(this.lex); 
    String str = this.lex.getString();
    Symbol symbol = new Symbol(str);
    int i = parseArrayDimension();
    ASTree aSTree = null;
    if (this.lex.lookAhead() == 61) {
      this.lex.get();
      aSTree = parseInitializer(paramSymbolTable);
    } 
    Declarator declarator = paramDeclarator.make(symbol, i, aSTree);
    paramSymbolTable.append(str, declarator);
    return declarator;
  }
  
  private ASTree parseInitializer(SymbolTable paramSymbolTable) throws CompileError {
    if (this.lex.lookAhead() == 123)
      return (ASTree)parseArrayInitializer(paramSymbolTable); 
    return parseExpression(paramSymbolTable);
  }
  
  private ArrayInit parseArrayInitializer(SymbolTable paramSymbolTable) throws CompileError {
    this.lex.get();
    ASTree aSTree = parseExpression(paramSymbolTable);
    ArrayInit arrayInit = new ArrayInit(aSTree);
    while (this.lex.lookAhead() == 44) {
      this.lex.get();
      aSTree = parseExpression(paramSymbolTable);
      ASTList.append((ASTList)arrayInit, aSTree);
    } 
    if (this.lex.get() != 125)
      throw new SyntaxError(this.lex); 
    return arrayInit;
  }
  
  private ASTree parseParExpression(SymbolTable paramSymbolTable) throws CompileError {
    if (this.lex.get() != 40)
      throw new SyntaxError(this.lex); 
    ASTree aSTree = parseExpression(paramSymbolTable);
    if (this.lex.get() != 41)
      throw new SyntaxError(this.lex); 
    return aSTree;
  }
  
  public ASTree parseExpression(SymbolTable paramSymbolTable) throws CompileError {
    ASTree aSTree1 = parseConditionalExpr(paramSymbolTable);
    if (!isAssignOp(this.lex.lookAhead()))
      return aSTree1; 
    int i = this.lex.get();
    ASTree aSTree2 = parseExpression(paramSymbolTable);
    return (ASTree)AssignExpr.makeAssign(i, aSTree1, aSTree2);
  }
  
  private static boolean isAssignOp(int paramInt) {
    return (paramInt == 61 || paramInt == 351 || paramInt == 352 || paramInt == 353 || paramInt == 354 || paramInt == 355 || paramInt == 356 || paramInt == 360 || paramInt == 361 || paramInt == 365 || paramInt == 367 || paramInt == 371);
  }
  
  private ASTree parseConditionalExpr(SymbolTable paramSymbolTable) throws CompileError {
    ASTree aSTree = parseBinaryExpr(paramSymbolTable);
    if (this.lex.lookAhead() == 63) {
      this.lex.get();
      ASTree aSTree1 = parseExpression(paramSymbolTable);
      if (this.lex.get() != 58)
        throw new CompileError(": is missing", this.lex); 
      ASTree aSTree2 = parseExpression(paramSymbolTable);
      return (ASTree)new CondExpr(aSTree, aSTree1, aSTree2);
    } 
    return aSTree;
  }
  
  private ASTree parseBinaryExpr(SymbolTable paramSymbolTable) throws CompileError {
    ASTree aSTree = parseUnaryExpr(paramSymbolTable);
    while (true) {
      int i = this.lex.lookAhead();
      int j = getOpPrecedence(i);
      if (j == 0)
        return aSTree; 
      aSTree = binaryExpr2(paramSymbolTable, aSTree, j);
    } 
  }
  
  private ASTree parseInstanceOf(SymbolTable paramSymbolTable, ASTree paramASTree) throws CompileError {
    int i = this.lex.lookAhead();
    if (isBuiltinType(i)) {
      this.lex.get();
      int k = parseArrayDimension();
      return (ASTree)new InstanceOfExpr(i, k, paramASTree);
    } 
    ASTList aSTList = parseClassType(paramSymbolTable);
    int j = parseArrayDimension();
    return (ASTree)new InstanceOfExpr(aSTList, j, paramASTree);
  }
  
  private ASTree binaryExpr2(SymbolTable paramSymbolTable, ASTree paramASTree, int paramInt) throws CompileError {
    int i = this.lex.get();
    if (i == 323)
      return parseInstanceOf(paramSymbolTable, paramASTree); 
    ASTree aSTree = parseUnaryExpr(paramSymbolTable);
    while (true) {
      int j = this.lex.lookAhead();
      int k = getOpPrecedence(j);
      if (k != 0 && paramInt > k) {
        aSTree = binaryExpr2(paramSymbolTable, aSTree, k);
        continue;
      } 
      break;
    } 
    return (ASTree)BinExpr.makeBin(i, paramASTree, aSTree);
  }
  
  private static final int[] binaryOpPrecedence = new int[] { 
      0, 0, 0, 0, 1, 6, 0, 0, 0, 1, 
      2, 0, 2, 0, 1, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 4, 0, 4, 
      0 };
  
  private int getOpPrecedence(int paramInt) {
    if (33 <= paramInt && paramInt <= 63)
      return binaryOpPrecedence[paramInt - 33]; 
    if (paramInt == 94)
      return 7; 
    if (paramInt == 124)
      return 8; 
    if (paramInt == 369)
      return 9; 
    if (paramInt == 368)
      return 10; 
    if (paramInt == 358 || paramInt == 350)
      return 5; 
    if (paramInt == 357 || paramInt == 359 || paramInt == 323)
      return 4; 
    if (paramInt == 364 || paramInt == 366 || paramInt == 370)
      return 3; 
    return 0;
  }
  
  private ASTree parseUnaryExpr(SymbolTable paramSymbolTable) throws CompileError {
    int i;
    switch (this.lex.lookAhead()) {
      case 33:
      case 43:
      case 45:
      case 126:
      case 362:
      case 363:
        i = this.lex.get();
        if (i == 45) {
          int j = this.lex.lookAhead();
          switch (j) {
            case 401:
            case 402:
            case 403:
              this.lex.get();
              return (ASTree)new IntConst(-this.lex.getLong(), j);
            case 404:
            case 405:
              this.lex.get();
              return (ASTree)new DoubleConst(-this.lex.getDouble(), j);
          } 
        } 
        return (ASTree)Expr.make(i, parseUnaryExpr(paramSymbolTable));
      case 40:
        return parseCast(paramSymbolTable);
    } 
    return parsePostfix(paramSymbolTable);
  }
  
  private ASTree parseCast(SymbolTable paramSymbolTable) throws CompileError {
    int i = this.lex.lookAhead(1);
    if (isBuiltinType(i) && nextIsBuiltinCast()) {
      this.lex.get();
      this.lex.get();
      int j = parseArrayDimension();
      if (this.lex.get() != 41)
        throw new CompileError(") is missing", this.lex); 
      return (ASTree)new CastExpr(i, j, parseUnaryExpr(paramSymbolTable));
    } 
    if (i == 400 && nextIsClassCast()) {
      this.lex.get();
      ASTList aSTList = parseClassType(paramSymbolTable);
      int j = parseArrayDimension();
      if (this.lex.get() != 41)
        throw new CompileError(") is missing", this.lex); 
      return (ASTree)new CastExpr(aSTList, j, parseUnaryExpr(paramSymbolTable));
    } 
    return parsePostfix(paramSymbolTable);
  }
  
  private boolean nextIsBuiltinCast() {
    byte b = 2;
    int i;
    while ((i = this.lex.lookAhead(b++)) == 91) {
      if (this.lex.lookAhead(b++) != 93)
        return false; 
    } 
    return (this.lex.lookAhead(b - 1) == 41);
  }
  
  private boolean nextIsClassCast() {
    int i = nextIsClassType(1);
    if (i < 0)
      return false; 
    int j = this.lex.lookAhead(i);
    if (j != 41)
      return false; 
    j = this.lex.lookAhead(i + 1);
    return (j == 40 || j == 412 || j == 406 || j == 400 || j == 339 || j == 336 || j == 328 || j == 410 || j == 411 || j == 403 || j == 402 || j == 401 || j == 405 || j == 404);
  }
  
  private int nextIsClassType(int paramInt) {
    while (this.lex.lookAhead(++paramInt) == 46) {
      if (this.lex.lookAhead(++paramInt) != 400)
        return -1; 
    } 
    int i;
    while ((i = this.lex.lookAhead(paramInt++)) == 91) {
      if (this.lex.lookAhead(paramInt++) != 93)
        return -1; 
    } 
    return paramInt - 1;
  }
  
  private int parseArrayDimension() throws CompileError {
    byte b = 0;
    while (this.lex.lookAhead() == 91) {
      b++;
      this.lex.get();
      if (this.lex.get() != 93)
        throw new CompileError("] is missing", this.lex); 
    } 
    return b;
  }
  
  private ASTList parseClassType(SymbolTable paramSymbolTable) throws CompileError {
    ASTList aSTList = null;
    while (true) {
      if (this.lex.get() != 400)
        throw new SyntaxError(this.lex); 
      aSTList = ASTList.append(aSTList, (ASTree)new Symbol(this.lex.getString()));
      if (this.lex.lookAhead() == 46) {
        this.lex.get();
        continue;
      } 
      break;
    } 
    return aSTList;
  }
  
  private ASTree parsePostfix(SymbolTable paramSymbolTable) throws CompileError {
    Expr expr;
    int i = this.lex.lookAhead();
    switch (i) {
      case 401:
      case 402:
      case 403:
        this.lex.get();
        return (ASTree)new IntConst(this.lex.getLong(), i);
      case 404:
      case 405:
        this.lex.get();
        return (ASTree)new DoubleConst(this.lex.getDouble(), i);
    } 
    ASTree aSTree = parsePrimaryExpr(paramSymbolTable);
    while (true) {
      String str;
      ASTree aSTree1;
      Expr expr1;
      ASTree aSTree2;
      int j;
      switch (this.lex.lookAhead()) {
        case 40:
          aSTree = parseMethodCall(paramSymbolTable, aSTree);
          continue;
        case 91:
          if (this.lex.lookAhead(1) == 93) {
            int k = parseArrayDimension();
            if (this.lex.get() != 46 || this.lex.get() != 307)
              throw new SyntaxError(this.lex); 
            aSTree = parseDotClass(aSTree, k);
            continue;
          } 
          aSTree1 = parseArrayIndex(paramSymbolTable);
          if (aSTree1 == null)
            throw new SyntaxError(this.lex); 
          expr1 = Expr.make(65, aSTree, aSTree1);
          continue;
        case 362:
        case 363:
          j = this.lex.get();
          expr1 = Expr.make(j, null, (ASTree)expr1);
          continue;
        case 46:
          this.lex.get();
          j = this.lex.get();
          if (j == 307) {
            aSTree2 = parseDotClass((ASTree)expr1, 0);
            continue;
          } 
          if (j == 336) {
            expr = Expr.make(46, (ASTree)new Symbol(toClassName(aSTree2)), (ASTree)new Keyword(j));
            continue;
          } 
          if (j == 400) {
            String str1 = this.lex.getString();
            expr = Expr.make(46, (ASTree)expr, (ASTree)new Member(str1));
            continue;
          } 
          throw new CompileError("missing member name", this.lex);
        case 35:
          this.lex.get();
          j = this.lex.get();
          if (j != 400)
            throw new CompileError("missing static member name", this.lex); 
          str = this.lex.getString();
          expr = Expr.make(35, (ASTree)new Symbol(toClassName((ASTree)expr)), (ASTree)new Member(str));
          continue;
      } 
      break;
    } 
    return (ASTree)expr;
  }
  
  private ASTree parseDotClass(ASTree paramASTree, int paramInt) throws CompileError {
    String str = toClassName(paramASTree);
    if (paramInt > 0) {
      StringBuffer stringBuffer = new StringBuffer();
      while (paramInt-- > 0)
        stringBuffer.append('['); 
      stringBuffer.append('L').append(str.replace('.', '/')).append(';');
      str = stringBuffer.toString();
    } 
    return (ASTree)Expr.make(46, (ASTree)new Symbol(str), (ASTree)new Member("class"));
  }
  
  private ASTree parseDotClass(int paramInt1, int paramInt2) throws CompileError {
    String str;
    if (paramInt2 > 0) {
      String str1 = CodeGen.toJvmTypeName(paramInt1, paramInt2);
      return (ASTree)Expr.make(46, (ASTree)new Symbol(str1), (ASTree)new Member("class"));
    } 
    switch (paramInt1) {
      case 301:
        str = "java.lang.Boolean";
        return (ASTree)Expr.make(35, (ASTree)new Symbol(str), (ASTree)new Member("TYPE"));
      case 303:
        str = "java.lang.Byte";
        return (ASTree)Expr.make(35, (ASTree)new Symbol(str), (ASTree)new Member("TYPE"));
      case 306:
        str = "java.lang.Character";
        return (ASTree)Expr.make(35, (ASTree)new Symbol(str), (ASTree)new Member("TYPE"));
      case 334:
        str = "java.lang.Short";
        return (ASTree)Expr.make(35, (ASTree)new Symbol(str), (ASTree)new Member("TYPE"));
      case 324:
        str = "java.lang.Integer";
        return (ASTree)Expr.make(35, (ASTree)new Symbol(str), (ASTree)new Member("TYPE"));
      case 326:
        str = "java.lang.Long";
        return (ASTree)Expr.make(35, (ASTree)new Symbol(str), (ASTree)new Member("TYPE"));
      case 317:
        str = "java.lang.Float";
        return (ASTree)Expr.make(35, (ASTree)new Symbol(str), (ASTree)new Member("TYPE"));
      case 312:
        str = "java.lang.Double";
        return (ASTree)Expr.make(35, (ASTree)new Symbol(str), (ASTree)new Member("TYPE"));
      case 344:
        str = "java.lang.Void";
        return (ASTree)Expr.make(35, (ASTree)new Symbol(str), (ASTree)new Member("TYPE"));
    } 
    throw new CompileError("invalid builtin type: " + paramInt1);
  }
  
  private ASTree parseMethodCall(SymbolTable paramSymbolTable, ASTree paramASTree) throws CompileError {
    if (paramASTree instanceof Keyword) {
      int i = ((Keyword)paramASTree).get();
      if (i != 339 && i != 336)
        throw new SyntaxError(this.lex); 
    } else if (!(paramASTree instanceof Symbol)) {
      if (paramASTree instanceof Expr) {
        int i = ((Expr)paramASTree).getOperator();
        if (i != 46 && i != 35)
          throw new SyntaxError(this.lex); 
      } 
    } 
    return (ASTree)CallExpr.makeCall(paramASTree, (ASTree)parseArgumentList(paramSymbolTable));
  }
  
  private String toClassName(ASTree paramASTree) throws CompileError {
    StringBuffer stringBuffer = new StringBuffer();
    toClassName(paramASTree, stringBuffer);
    return stringBuffer.toString();
  }
  
  private void toClassName(ASTree paramASTree, StringBuffer paramStringBuffer) throws CompileError {
    if (paramASTree instanceof Symbol) {
      paramStringBuffer.append(((Symbol)paramASTree).get());
      return;
    } 
    if (paramASTree instanceof Expr) {
      Expr expr = (Expr)paramASTree;
      if (expr.getOperator() == 46) {
        toClassName(expr.oprand1(), paramStringBuffer);
        paramStringBuffer.append('.');
        toClassName(expr.oprand2(), paramStringBuffer);
        return;
      } 
    } 
    throw new CompileError("bad static member access", this.lex);
  }
  
  private ASTree parsePrimaryExpr(SymbolTable paramSymbolTable) throws CompileError {
    String str;
    Declarator declarator;
    ASTree aSTree;
    int i;
    switch (i = this.lex.get()) {
      case 336:
      case 339:
      case 410:
      case 411:
      case 412:
        return (ASTree)new Keyword(i);
      case 400:
        str = this.lex.getString();
        declarator = paramSymbolTable.lookup(str);
        if (declarator == null)
          return (ASTree)new Member(str); 
        return (ASTree)new Variable(str, declarator);
      case 406:
        return (ASTree)new StringL(this.lex.getString());
      case 328:
        return (ASTree)parseNew(paramSymbolTable);
      case 40:
        aSTree = parseExpression(paramSymbolTable);
        if (this.lex.get() == 41)
          return aSTree; 
        throw new CompileError(") is missing", this.lex);
    } 
    if (isBuiltinType(i) || i == 344) {
      int j = parseArrayDimension();
      if (this.lex.get() == 46 && this.lex.get() == 307)
        return parseDotClass(i, j); 
    } 
    throw new SyntaxError(this.lex);
  }
  
  private NewExpr parseNew(SymbolTable paramSymbolTable) throws CompileError {
    ArrayInit arrayInit = null;
    int i = this.lex.lookAhead();
    if (isBuiltinType(i)) {
      this.lex.get();
      ASTList aSTList = parseArraySize(paramSymbolTable);
      if (this.lex.lookAhead() == 123)
        arrayInit = parseArrayInitializer(paramSymbolTable); 
      return new NewExpr(i, aSTList, arrayInit);
    } 
    if (i == 400) {
      ASTList aSTList = parseClassType(paramSymbolTable);
      i = this.lex.lookAhead();
      if (i == 40) {
        ASTList aSTList1 = parseArgumentList(paramSymbolTable);
        return new NewExpr(aSTList, aSTList1);
      } 
      if (i == 91) {
        ASTList aSTList1 = parseArraySize(paramSymbolTable);
        if (this.lex.lookAhead() == 123)
          arrayInit = parseArrayInitializer(paramSymbolTable); 
        return NewExpr.makeObjectArray(aSTList, aSTList1, arrayInit);
      } 
    } 
    throw new SyntaxError(this.lex);
  }
  
  private ASTList parseArraySize(SymbolTable paramSymbolTable) throws CompileError {
    ASTList aSTList = null;
    while (this.lex.lookAhead() == 91)
      aSTList = ASTList.append(aSTList, parseArrayIndex(paramSymbolTable)); 
    return aSTList;
  }
  
  private ASTree parseArrayIndex(SymbolTable paramSymbolTable) throws CompileError {
    this.lex.get();
    if (this.lex.lookAhead() == 93) {
      this.lex.get();
      return null;
    } 
    ASTree aSTree = parseExpression(paramSymbolTable);
    if (this.lex.get() != 93)
      throw new CompileError("] is missing", this.lex); 
    return aSTree;
  }
  
  private ASTList parseArgumentList(SymbolTable paramSymbolTable) throws CompileError {
    if (this.lex.get() != 40)
      throw new CompileError("( is missing", this.lex); 
    ASTList aSTList = null;
    if (this.lex.lookAhead() != 41)
      while (true) {
        aSTList = ASTList.append(aSTList, parseExpression(paramSymbolTable));
        if (this.lex.lookAhead() == 44) {
          this.lex.get();
          continue;
        } 
        break;
      }  
    if (this.lex.get() != 41)
      throw new CompileError(") is missing", this.lex); 
    return aSTList;
  }
}
