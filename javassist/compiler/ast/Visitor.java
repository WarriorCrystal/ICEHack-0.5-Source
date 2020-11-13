package javassist.compiler.ast;

import javassist.compiler.CompileError;

public class Visitor {
  public void atASTList(ASTList paramASTList) throws CompileError {}
  
  public void atPair(Pair paramPair) throws CompileError {}
  
  public void atFieldDecl(FieldDecl paramFieldDecl) throws CompileError {}
  
  public void atMethodDecl(MethodDecl paramMethodDecl) throws CompileError {}
  
  public void atStmnt(Stmnt paramStmnt) throws CompileError {}
  
  public void atDeclarator(Declarator paramDeclarator) throws CompileError {}
  
  public void atAssignExpr(AssignExpr paramAssignExpr) throws CompileError {}
  
  public void atCondExpr(CondExpr paramCondExpr) throws CompileError {}
  
  public void atBinExpr(BinExpr paramBinExpr) throws CompileError {}
  
  public void atExpr(Expr paramExpr) throws CompileError {}
  
  public void atCallExpr(CallExpr paramCallExpr) throws CompileError {}
  
  public void atCastExpr(CastExpr paramCastExpr) throws CompileError {}
  
  public void atInstanceOfExpr(InstanceOfExpr paramInstanceOfExpr) throws CompileError {}
  
  public void atNewExpr(NewExpr paramNewExpr) throws CompileError {}
  
  public void atSymbol(Symbol paramSymbol) throws CompileError {}
  
  public void atMember(Member paramMember) throws CompileError {}
  
  public void atVariable(Variable paramVariable) throws CompileError {}
  
  public void atKeyword(Keyword paramKeyword) throws CompileError {}
  
  public void atStringL(StringL paramStringL) throws CompileError {}
  
  public void atIntConst(IntConst paramIntConst) throws CompileError {}
  
  public void atDoubleConst(DoubleConst paramDoubleConst) throws CompileError {}
  
  public void atArrayInit(ArrayInit paramArrayInit) throws CompileError {}
}
