package javassist.convert;

import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;

public class TransformAfter extends TransformBefore {
  public TransformAfter(Transformer paramTransformer, CtMethod paramCtMethod1, CtMethod paramCtMethod2) throws NotFoundException {
    super(paramTransformer, paramCtMethod1, paramCtMethod2);
  }
  
  protected int match2(int paramInt, CodeIterator paramCodeIterator) throws BadBytecode {
    paramCodeIterator.move(paramInt);
    paramCodeIterator.insert(this.saveCode);
    paramCodeIterator.insert(this.loadCode);
    int i = paramCodeIterator.insertGap(3);
    paramCodeIterator.setMark(i);
    paramCodeIterator.insert(this.loadCode);
    paramInt = paramCodeIterator.next();
    i = paramCodeIterator.getMark();
    paramCodeIterator.writeByte(paramCodeIterator.byteAt(paramInt), i);
    paramCodeIterator.write16bit(paramCodeIterator.u16bitAt(paramInt + 1), i + 1);
    paramCodeIterator.writeByte(184, paramInt);
    paramCodeIterator.write16bit(this.newIndex, paramInt + 1);
    paramCodeIterator.move(i);
    return paramCodeIterator.next();
  }
}
