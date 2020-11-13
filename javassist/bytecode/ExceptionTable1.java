package javassist.bytecode;

class ExceptionTable1 {
  int startPc;
  
  int endPc;
  
  int handlerPc;
  
  int catchType;
  
  ExceptionTable1(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.startPc = paramInt1;
    this.endPc = paramInt2;
    this.handlerPc = paramInt3;
    this.catchType = paramInt4;
  }
}
