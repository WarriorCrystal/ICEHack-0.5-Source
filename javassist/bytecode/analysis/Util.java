package javassist.bytecode.analysis;

import javassist.bytecode.CodeIterator;
import javassist.bytecode.Opcode;

public class Util implements Opcode {
  public static int getJumpTarget(int paramInt, CodeIterator paramCodeIterator) {
    int i = paramCodeIterator.byteAt(paramInt);
    paramInt += (i == 201 || i == 200) ? paramCodeIterator.s32bitAt(paramInt + 1) : paramCodeIterator.s16bitAt(paramInt + 1);
    return paramInt;
  }
  
  public static boolean isJumpInstruction(int paramInt) {
    return ((paramInt >= 153 && paramInt <= 168) || paramInt == 198 || paramInt == 199 || paramInt == 201 || paramInt == 200);
  }
  
  public static boolean isGoto(int paramInt) {
    return (paramInt == 167 || paramInt == 200);
  }
  
  public static boolean isJsr(int paramInt) {
    return (paramInt == 168 || paramInt == 201);
  }
  
  public static boolean isReturn(int paramInt) {
    return (paramInt >= 172 && paramInt <= 177);
  }
}
