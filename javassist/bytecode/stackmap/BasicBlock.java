package javassist.bytecode.stackmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;

public class BasicBlock {
  protected int position;
  
  protected int length;
  
  protected int incoming;
  
  protected BasicBlock[] exit;
  
  protected boolean stop;
  
  protected Catch toCatch;
  
  static class JsrBytecode extends BadBytecode {
    JsrBytecode() {
      super("JSR");
    }
  }
  
  protected BasicBlock(int paramInt) {
    this.position = paramInt;
    this.length = 0;
    this.incoming = 0;
  }
  
  public static BasicBlock find(BasicBlock[] paramArrayOfBasicBlock, int paramInt) throws BadBytecode {
    for (byte b = 0; b < paramArrayOfBasicBlock.length; b++) {
      int i = (paramArrayOfBasicBlock[b]).position;
      if (i <= paramInt && paramInt < i + (paramArrayOfBasicBlock[b]).length)
        return paramArrayOfBasicBlock[b]; 
    } 
    throw new BadBytecode("no basic block at " + paramInt);
  }
  
  public static class Catch {
    public Catch next;
    
    public BasicBlock body;
    
    public int typeIndex;
    
    Catch(BasicBlock param1BasicBlock, int param1Int, Catch param1Catch) {
      this.body = param1BasicBlock;
      this.typeIndex = param1Int;
      this.next = param1Catch;
    }
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    String str = getClass().getName();
    int i = str.lastIndexOf('.');
    stringBuffer.append((i < 0) ? str : str.substring(i + 1));
    stringBuffer.append("[");
    toString2(stringBuffer);
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  protected void toString2(StringBuffer paramStringBuffer) {
    paramStringBuffer.append("pos=").append(this.position).append(", len=")
      .append(this.length).append(", in=").append(this.incoming)
      .append(", exit{");
    if (this.exit != null)
      for (byte b = 0; b < this.exit.length; b++)
        paramStringBuffer.append((this.exit[b]).position).append(",");  
    paramStringBuffer.append("}, {");
    Catch catch_ = this.toCatch;
    while (catch_ != null) {
      paramStringBuffer.append("(").append(catch_.body.position).append(", ")
        .append(catch_.typeIndex).append("), ");
      catch_ = catch_.next;
    } 
    paramStringBuffer.append("}");
  }
  
  static class Mark implements Comparable {
    int position;
    
    BasicBlock block;
    
    BasicBlock[] jump;
    
    boolean alwaysJmp;
    
    int size;
    
    BasicBlock.Catch catcher;
    
    Mark(int param1Int) {
      this.position = param1Int;
      this.block = null;
      this.jump = null;
      this.alwaysJmp = false;
      this.size = 0;
      this.catcher = null;
    }
    
    public int compareTo(Object param1Object) {
      if (param1Object instanceof Mark) {
        int i = ((Mark)param1Object).position;
        return this.position - i;
      } 
      return -1;
    }
    
    void setJump(BasicBlock[] param1ArrayOfBasicBlock, int param1Int, boolean param1Boolean) {
      this.jump = param1ArrayOfBasicBlock;
      this.size = param1Int;
      this.alwaysJmp = param1Boolean;
    }
  }
  
  public static class Maker {
    protected BasicBlock makeBlock(int param1Int) {
      return new BasicBlock(param1Int);
    }
    
    protected BasicBlock[] makeArray(int param1Int) {
      return new BasicBlock[param1Int];
    }
    
    private BasicBlock[] makeArray(BasicBlock param1BasicBlock) {
      BasicBlock[] arrayOfBasicBlock = makeArray(1);
      arrayOfBasicBlock[0] = param1BasicBlock;
      return arrayOfBasicBlock;
    }
    
    private BasicBlock[] makeArray(BasicBlock param1BasicBlock1, BasicBlock param1BasicBlock2) {
      BasicBlock[] arrayOfBasicBlock = makeArray(2);
      arrayOfBasicBlock[0] = param1BasicBlock1;
      arrayOfBasicBlock[1] = param1BasicBlock2;
      return arrayOfBasicBlock;
    }
    
    public BasicBlock[] make(MethodInfo param1MethodInfo) throws BadBytecode {
      CodeAttribute codeAttribute = param1MethodInfo.getCodeAttribute();
      if (codeAttribute == null)
        return null; 
      CodeIterator codeIterator = codeAttribute.iterator();
      return make(codeIterator, 0, codeIterator.getCodeLength(), codeAttribute.getExceptionTable());
    }
    
    public BasicBlock[] make(CodeIterator param1CodeIterator, int param1Int1, int param1Int2, ExceptionTable param1ExceptionTable) throws BadBytecode {
      HashMap hashMap = makeMarks(param1CodeIterator, param1Int1, param1Int2, param1ExceptionTable);
      BasicBlock[] arrayOfBasicBlock = makeBlocks(hashMap);
      addCatchers(arrayOfBasicBlock, param1ExceptionTable);
      return arrayOfBasicBlock;
    }
    
    private BasicBlock.Mark makeMark(HashMap param1HashMap, int param1Int) {
      return makeMark0(param1HashMap, param1Int, true, true);
    }
    
    private BasicBlock.Mark makeMark(HashMap param1HashMap, int param1Int1, BasicBlock[] param1ArrayOfBasicBlock, int param1Int2, boolean param1Boolean) {
      BasicBlock.Mark mark = makeMark0(param1HashMap, param1Int1, false, false);
      mark.setJump(param1ArrayOfBasicBlock, param1Int2, param1Boolean);
      return mark;
    }
    
    private BasicBlock.Mark makeMark0(HashMap<Integer, BasicBlock.Mark> param1HashMap, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      Integer integer = new Integer(param1Int);
      BasicBlock.Mark mark = (BasicBlock.Mark)param1HashMap.get(integer);
      if (mark == null) {
        mark = new BasicBlock.Mark(param1Int);
        param1HashMap.put(integer, mark);
      } 
      if (param1Boolean1) {
        if (mark.block == null)
          mark.block = makeBlock(param1Int); 
        if (param1Boolean2)
          mark.block.incoming++; 
      } 
      return mark;
    }
    
    private HashMap makeMarks(CodeIterator param1CodeIterator, int param1Int1, int param1Int2, ExceptionTable param1ExceptionTable) throws BadBytecode {
      param1CodeIterator.begin();
      param1CodeIterator.move(param1Int1);
      HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
      while (param1CodeIterator.hasNext()) {
        int i = param1CodeIterator.next();
        if (i >= param1Int2)
          break; 
        int j = param1CodeIterator.byteAt(i);
        if ((153 <= j && j <= 166) || j == 198 || j == 199) {
          BasicBlock.Mark mark1 = makeMark(hashMap, i + param1CodeIterator.s16bitAt(i + 1));
          BasicBlock.Mark mark2 = makeMark(hashMap, i + 3);
          makeMark(hashMap, i, makeArray(mark1.block, mark2.block), 3, false);
          continue;
        } 
        if (167 <= j && j <= 171) {
          int k;
          int m;
          int n;
          BasicBlock[] arrayOfBasicBlock1;
          int i1;
          BasicBlock[] arrayOfBasicBlock2;
          int i2;
          int i3;
          int i4;
          byte b;
          switch (j) {
            case 167:
              makeGoto(hashMap, i, i + param1CodeIterator.s16bitAt(i + 1), 3);
              continue;
            case 168:
              makeJsr(hashMap, i, i + param1CodeIterator.s16bitAt(i + 1), 3);
              continue;
            case 169:
              makeMark(hashMap, i, null, 2, true);
              continue;
            case 170:
              k = (i & 0xFFFFFFFC) + 4;
              m = param1CodeIterator.s32bitAt(k + 4);
              n = param1CodeIterator.s32bitAt(k + 8);
              i1 = n - m + 1;
              arrayOfBasicBlock2 = makeArray(i1 + 1);
              arrayOfBasicBlock2[0] = (makeMark(hashMap, i + param1CodeIterator.s32bitAt(k))).block;
              i3 = k + 12;
              i4 = i3 + i1 * 4;
              b = 1;
              while (i3 < i4) {
                arrayOfBasicBlock2[b++] = (makeMark(hashMap, i + param1CodeIterator.s32bitAt(i3))).block;
                i3 += 4;
              } 
              makeMark(hashMap, i, arrayOfBasicBlock2, i4 - i, true);
              continue;
            case 171:
              k = (i & 0xFFFFFFFC) + 4;
              m = param1CodeIterator.s32bitAt(k + 4);
              arrayOfBasicBlock1 = makeArray(m + 1);
              arrayOfBasicBlock1[0] = (makeMark(hashMap, i + param1CodeIterator.s32bitAt(k))).block;
              i1 = k + 8 + 4;
              i2 = i1 + m * 8 - 4;
              i3 = 1;
              while (i1 < i2) {
                arrayOfBasicBlock1[i3++] = (makeMark(hashMap, i + param1CodeIterator.s32bitAt(i1))).block;
                i1 += 8;
              } 
              makeMark(hashMap, i, arrayOfBasicBlock1, i2 - i, true);
              continue;
          } 
          continue;
        } 
        if ((172 <= j && j <= 177) || j == 191) {
          makeMark(hashMap, i, null, 1, true);
          continue;
        } 
        if (j == 200) {
          makeGoto(hashMap, i, i + param1CodeIterator.s32bitAt(i + 1), 5);
          continue;
        } 
        if (j == 201) {
          makeJsr(hashMap, i, i + param1CodeIterator.s32bitAt(i + 1), 5);
          continue;
        } 
        if (j == 196 && param1CodeIterator.byteAt(i + 1) == 169)
          makeMark(hashMap, i, null, 4, true); 
      } 
      if (param1ExceptionTable != null) {
        int i = param1ExceptionTable.size();
        while (--i >= 0) {
          makeMark0(hashMap, param1ExceptionTable.startPc(i), true, false);
          makeMark(hashMap, param1ExceptionTable.handlerPc(i));
        } 
      } 
      return hashMap;
    }
    
    private void makeGoto(HashMap param1HashMap, int param1Int1, int param1Int2, int param1Int3) {
      BasicBlock.Mark mark = makeMark(param1HashMap, param1Int2);
      BasicBlock[] arrayOfBasicBlock = makeArray(mark.block);
      makeMark(param1HashMap, param1Int1, arrayOfBasicBlock, param1Int3, true);
    }
    
    protected void makeJsr(HashMap param1HashMap, int param1Int1, int param1Int2, int param1Int3) throws BadBytecode {
      throw new BasicBlock.JsrBytecode();
    }
    
    private BasicBlock[] makeBlocks(HashMap param1HashMap) {
      BasicBlock basicBlock;
      BasicBlock.Mark[] arrayOfMark = (BasicBlock.Mark[])param1HashMap.values().toArray((Object[])new BasicBlock.Mark[param1HashMap.size()]);
      Arrays.sort((Object[])arrayOfMark);
      ArrayList<BasicBlock> arrayList = new ArrayList();
      byte b = 0;
      if (arrayOfMark.length > 0 && (arrayOfMark[0]).position == 0 && (arrayOfMark[0]).block != null) {
        basicBlock = getBBlock(arrayOfMark[b++]);
      } else {
        basicBlock = makeBlock(0);
      } 
      arrayList.add(basicBlock);
      while (b < arrayOfMark.length) {
        BasicBlock.Mark mark = arrayOfMark[b++];
        BasicBlock basicBlock1 = getBBlock(mark);
        if (basicBlock1 == null) {
          if (basicBlock.length > 0) {
            basicBlock = makeBlock(basicBlock.position + basicBlock.length);
            arrayList.add(basicBlock);
          } 
          basicBlock.length = mark.position + mark.size - basicBlock.position;
          basicBlock.exit = mark.jump;
          basicBlock.stop = mark.alwaysJmp;
          continue;
        } 
        if (basicBlock.length == 0) {
          basicBlock.length = mark.position - basicBlock.position;
          basicBlock1.incoming++;
          basicBlock.exit = makeArray(basicBlock1);
        } else if (basicBlock.position + basicBlock.length < mark.position) {
          basicBlock = makeBlock(basicBlock.position + basicBlock.length);
          arrayList.add(basicBlock);
          basicBlock.length = mark.position - basicBlock.position;
          basicBlock.stop = true;
          basicBlock.exit = makeArray(basicBlock1);
        } 
        arrayList.add(basicBlock1);
        basicBlock = basicBlock1;
      } 
      return arrayList.<BasicBlock>toArray(makeArray(arrayList.size()));
    }
    
    private static BasicBlock getBBlock(BasicBlock.Mark param1Mark) {
      BasicBlock basicBlock = param1Mark.block;
      if (basicBlock != null && param1Mark.size > 0) {
        basicBlock.exit = param1Mark.jump;
        basicBlock.length = param1Mark.size;
        basicBlock.stop = param1Mark.alwaysJmp;
      } 
      return basicBlock;
    }
    
    private void addCatchers(BasicBlock[] param1ArrayOfBasicBlock, ExceptionTable param1ExceptionTable) throws BadBytecode {
      if (param1ExceptionTable == null)
        return; 
      int i = param1ExceptionTable.size();
      while (--i >= 0) {
        BasicBlock basicBlock = BasicBlock.find(param1ArrayOfBasicBlock, param1ExceptionTable.handlerPc(i));
        int j = param1ExceptionTable.startPc(i);
        int k = param1ExceptionTable.endPc(i);
        int m = param1ExceptionTable.catchType(i);
        basicBlock.incoming--;
        for (byte b = 0; b < param1ArrayOfBasicBlock.length; b++) {
          BasicBlock basicBlock1 = param1ArrayOfBasicBlock[b];
          int n = basicBlock1.position;
          if (j <= n && n < k) {
            basicBlock1.toCatch = new BasicBlock.Catch(basicBlock, m, basicBlock1.toCatch);
            basicBlock.incoming++;
          } 
        } 
      } 
    }
  }
}
