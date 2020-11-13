package javassist.bytecode.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class SubroutineScanner implements Opcode {
  private Subroutine[] subroutines;
  
  Map subTable = new HashMap<Object, Object>();
  
  Set done = new HashSet();
  
  public Subroutine[] scan(MethodInfo paramMethodInfo) throws BadBytecode {
    CodeAttribute codeAttribute = paramMethodInfo.getCodeAttribute();
    CodeIterator codeIterator = codeAttribute.iterator();
    this.subroutines = new Subroutine[codeAttribute.getCodeLength()];
    this.subTable.clear();
    this.done.clear();
    scan(0, codeIterator, null);
    ExceptionTable exceptionTable = codeAttribute.getExceptionTable();
    for (byte b = 0; b < exceptionTable.size(); b++) {
      int i = exceptionTable.handlerPc(b);
      scan(i, codeIterator, this.subroutines[exceptionTable.startPc(b)]);
    } 
    return this.subroutines;
  }
  
  private void scan(int paramInt, CodeIterator paramCodeIterator, Subroutine paramSubroutine) throws BadBytecode {
    boolean bool;
    if (this.done.contains(new Integer(paramInt)))
      return; 
    this.done.add(new Integer(paramInt));
    int i = paramCodeIterator.lookAhead();
    paramCodeIterator.move(paramInt);
    do {
      paramInt = paramCodeIterator.next();
      bool = (scanOp(paramInt, paramCodeIterator, paramSubroutine) && paramCodeIterator.hasNext()) ? true : false;
    } while (bool);
    paramCodeIterator.move(i);
  }
  
  private boolean scanOp(int paramInt, CodeIterator paramCodeIterator, Subroutine paramSubroutine) throws BadBytecode {
    this.subroutines[paramInt] = paramSubroutine;
    int i = paramCodeIterator.byteAt(paramInt);
    if (i == 170) {
      scanTableSwitch(paramInt, paramCodeIterator, paramSubroutine);
      return false;
    } 
    if (i == 171) {
      scanLookupSwitch(paramInt, paramCodeIterator, paramSubroutine);
      return false;
    } 
    if (Util.isReturn(i) || i == 169 || i == 191)
      return false; 
    if (Util.isJumpInstruction(i)) {
      int j = Util.getJumpTarget(paramInt, paramCodeIterator);
      if (i == 168 || i == 201) {
        Subroutine subroutine = (Subroutine)this.subTable.get(new Integer(j));
        if (subroutine == null) {
          subroutine = new Subroutine(j, paramInt);
          this.subTable.put(new Integer(j), subroutine);
          scan(j, paramCodeIterator, subroutine);
        } else {
          subroutine.addCaller(paramInt);
        } 
      } else {
        scan(j, paramCodeIterator, paramSubroutine);
        if (Util.isGoto(i))
          return false; 
      } 
    } 
    return true;
  }
  
  private void scanLookupSwitch(int paramInt, CodeIterator paramCodeIterator, Subroutine paramSubroutine) throws BadBytecode {
    int i = (paramInt & 0xFFFFFFFC) + 4;
    scan(paramInt + paramCodeIterator.s32bitAt(i), paramCodeIterator, paramSubroutine);
    i += 4;
    int j = paramCodeIterator.s32bitAt(i);
    i += 4;
    int k = j * 8 + i;
    for (i += 4; i < k; i += 8) {
      int m = paramCodeIterator.s32bitAt(i) + paramInt;
      scan(m, paramCodeIterator, paramSubroutine);
    } 
  }
  
  private void scanTableSwitch(int paramInt, CodeIterator paramCodeIterator, Subroutine paramSubroutine) throws BadBytecode {
    int i = (paramInt & 0xFFFFFFFC) + 4;
    scan(paramInt + paramCodeIterator.s32bitAt(i), paramCodeIterator, paramSubroutine);
    i += 4;
    int j = paramCodeIterator.s32bitAt(i);
    i += 4;
    int k = paramCodeIterator.s32bitAt(i);
    i += 4;
    int m = (k - j + 1) * 4 + i;
    for (; i < m; i += 4) {
      int n = paramCodeIterator.s32bitAt(i) + paramInt;
      scan(n, paramCodeIterator, paramSubroutine);
    } 
  }
}
