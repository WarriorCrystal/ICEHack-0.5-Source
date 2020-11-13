package javassist.bytecode;

class CodeAnalyzer implements Opcode {
  private ConstPool14 constPool;
  
  private CodeAttribute codeAttr;
  
  public CodeAnalyzer(CodeAttribute paramCodeAttribute) {
    this.codeAttr = paramCodeAttribute;
    this.constPool = paramCodeAttribute.getConstPool();
  }
  
  public int computeMaxStack() throws BadBytecode {
    boolean bool;
    CodeIterator codeIterator = this.codeAttr.iterator();
    int i = codeIterator.getCodeLength();
    int[] arrayOfInt = new int[i];
    this.constPool = this.codeAttr.getConstPool();
    initStack(arrayOfInt, this.codeAttr);
    do {
      bool = false;
      for (byte b1 = 0; b1 < i; b1++) {
        if (arrayOfInt[b1] < 0) {
          bool = true;
          visitBytecode(codeIterator, arrayOfInt, b1);
        } 
      } 
    } while (bool);
    int j = 1;
    for (byte b = 0; b < i; b++) {
      if (arrayOfInt[b] > j)
        j = arrayOfInt[b]; 
    } 
    return j - 1;
  }
  
  private void initStack(int[] paramArrayOfint, CodeAttribute paramCodeAttribute) {
    paramArrayOfint[0] = -1;
    ExceptionTable exceptionTable = paramCodeAttribute.getExceptionTable();
    if (exceptionTable != null) {
      int i = exceptionTable.size();
      for (byte b = 0; b < i; b++)
        paramArrayOfint[exceptionTable.handlerPc(b)] = -2; 
    } 
  }
  
  private void visitBytecode(CodeIterator paramCodeIterator, int[] paramArrayOfint, int paramInt) throws BadBytecode {
    int i = paramArrayOfint.length;
    paramCodeIterator.move(paramInt);
    int j = -paramArrayOfint[paramInt];
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = -1;
    while (paramCodeIterator.hasNext()) {
      paramInt = paramCodeIterator.next();
      paramArrayOfint[paramInt] = j;
      int k = paramCodeIterator.byteAt(paramInt);
      j = visitInst(k, paramCodeIterator, paramInt, j);
      if (j < 1)
        throw new BadBytecode("stack underflow at " + paramInt); 
      if (processBranch(k, paramCodeIterator, paramInt, i, paramArrayOfint, j, arrayOfInt))
        break; 
      if (isEnd(k))
        break; 
      if (k == 168 || k == 201)
        j--; 
    } 
  }
  
  private boolean processBranch(int paramInt1, CodeIterator paramCodeIterator, int paramInt2, int paramInt3, int[] paramArrayOfint1, int paramInt4, int[] paramArrayOfint2) throws BadBytecode {
    if ((153 <= paramInt1 && paramInt1 <= 166) || paramInt1 == 198 || paramInt1 == 199) {
      int i = paramInt2 + paramCodeIterator.s16bitAt(paramInt2 + 1);
      checkTarget(paramInt2, i, paramInt3, paramArrayOfint1, paramInt4);
    } else {
      int i;
      int j;
      switch (paramInt1) {
        case 167:
          i = paramInt2 + paramCodeIterator.s16bitAt(paramInt2 + 1);
          checkTarget(paramInt2, i, paramInt3, paramArrayOfint1, paramInt4);
          return true;
        case 200:
          i = paramInt2 + paramCodeIterator.s32bitAt(paramInt2 + 1);
          checkTarget(paramInt2, i, paramInt3, paramArrayOfint1, paramInt4);
          return true;
        case 168:
        case 201:
          if (paramInt1 == 168) {
            i = paramInt2 + paramCodeIterator.s16bitAt(paramInt2 + 1);
          } else {
            i = paramInt2 + paramCodeIterator.s32bitAt(paramInt2 + 1);
          } 
          checkTarget(paramInt2, i, paramInt3, paramArrayOfint1, paramInt4);
          if (paramArrayOfint2[0] < 0) {
            paramArrayOfint2[0] = paramInt4;
            return false;
          } 
          if (paramInt4 == paramArrayOfint2[0])
            return false; 
          throw new BadBytecode("sorry, cannot compute this data flow due to JSR: " + paramInt4 + "," + paramArrayOfint2[0]);
        case 169:
          if (paramArrayOfint2[0] < 0) {
            paramArrayOfint2[0] = paramInt4 + 1;
            return false;
          } 
          if (paramInt4 + 1 == paramArrayOfint2[0])
            return true; 
          throw new BadBytecode("sorry, cannot compute this data flow due to RET: " + paramInt4 + "," + paramArrayOfint2[0]);
        case 170:
        case 171:
          j = (paramInt2 & 0xFFFFFFFC) + 4;
          i = paramInt2 + paramCodeIterator.s32bitAt(j);
          checkTarget(paramInt2, i, paramInt3, paramArrayOfint1, paramInt4);
          if (paramInt1 == 171) {
            int k = paramCodeIterator.s32bitAt(j + 4);
            j += 12;
            for (byte b = 0; b < k; b++) {
              i = paramInt2 + paramCodeIterator.s32bitAt(j);
              checkTarget(paramInt2, i, paramInt3, paramArrayOfint1, paramInt4);
              j += 8;
            } 
          } else {
            int k = paramCodeIterator.s32bitAt(j + 4);
            int m = paramCodeIterator.s32bitAt(j + 8);
            int n = m - k + 1;
            j += 12;
            for (byte b = 0; b < n; b++) {
              i = paramInt2 + paramCodeIterator.s32bitAt(j);
              checkTarget(paramInt2, i, paramInt3, paramArrayOfint1, paramInt4);
              j += 4;
            } 
          } 
          return true;
      } 
    } 
    return false;
  }
  
  private void checkTarget(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfint, int paramInt4) throws BadBytecode {
    if (paramInt2 < 0 || paramInt3 <= paramInt2)
      throw new BadBytecode("bad branch offset at " + paramInt1); 
    int i = paramArrayOfint[paramInt2];
    if (i == 0) {
      paramArrayOfint[paramInt2] = -paramInt4;
    } else if (i != paramInt4 && i != -paramInt4) {
      throw new BadBytecode("verification error (" + paramInt4 + "," + i + ") at " + paramInt1);
    } 
  }
  
  private static boolean isEnd(int paramInt) {
    return ((172 <= paramInt && paramInt <= 177) || paramInt == 191);
  }
  
  private int visitInst(int paramInt1, CodeIterator paramCodeIterator, int paramInt2, int paramInt3) throws BadBytecode {
    String str;
    switch (paramInt1) {
      case 180:
        paramInt3 += getFieldSize(paramCodeIterator, paramInt2) - 1;
        return paramInt3;
      case 181:
        paramInt3 -= getFieldSize(paramCodeIterator, paramInt2) + 1;
        return paramInt3;
      case 178:
        paramInt3 += getFieldSize(paramCodeIterator, paramInt2);
        return paramInt3;
      case 179:
        paramInt3 -= getFieldSize(paramCodeIterator, paramInt2);
        return paramInt3;
      case 182:
      case 183:
        str = this.constPool.getMethodrefType(paramCodeIterator.u16bitAt(paramInt2 + 1));
        paramInt3 += Descriptor.dataSize(str) - 1;
        return paramInt3;
      case 184:
        str = this.constPool.getMethodrefType(paramCodeIterator.u16bitAt(paramInt2 + 1));
        paramInt3 += Descriptor.dataSize(str);
        return paramInt3;
      case 185:
        str = this.constPool.getInterfaceMethodrefType(paramCodeIterator.u16bitAt(paramInt2 + 1));
        paramInt3 += Descriptor.dataSize(str) - 1;
        return paramInt3;
      case 186:
        str = this.constPool.getInvokeDynamicType(paramCodeIterator.u16bitAt(paramInt2 + 1));
        paramInt3 += Descriptor.dataSize(str);
        return paramInt3;
      case 191:
        paramInt3 = 1;
        return paramInt3;
      case 197:
        paramInt3 += 1 - paramCodeIterator.byteAt(paramInt2 + 3);
        return paramInt3;
      case 196:
        paramInt1 = paramCodeIterator.byteAt(paramInt2 + 1);
        break;
    } 
    paramInt3 += STACK_GROW[paramInt1];
    return paramInt3;
  }
  
  private int getFieldSize(CodeIterator paramCodeIterator, int paramInt) {
    String str = this.constPool.getFieldrefType(paramCodeIterator.u16bitAt(paramInt + 1));
    return Descriptor.dataSize(str);
  }
}
