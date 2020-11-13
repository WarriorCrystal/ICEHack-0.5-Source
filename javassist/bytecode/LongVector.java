package javassist.bytecode;

final class LongVector {
  static final int ASIZE = 128;
  
  static final int ABITS = 7;
  
  static final int VSIZE = 8;
  
  private ConstPool13[][] objects;
  
  private int elements;
  
  public LongVector() {
    this.objects = new ConstPool13[8][];
    this.elements = 0;
  }
  
  public LongVector(int paramInt) {
    int i = (paramInt >> 7 & 0xFFFFFFF8) + 8;
    this.objects = new ConstPool13[i][];
    this.elements = 0;
  }
  
  public int size() {
    return this.elements;
  }
  
  public int capacity() {
    return this.objects.length * 128;
  }
  
  public ConstPool13 elementAt(int paramInt) {
    if (paramInt < 0 || this.elements <= paramInt)
      return null; 
    return this.objects[paramInt >> 7][paramInt & 0x7F];
  }
  
  public void addElement(ConstPool13 paramConstPool13) {
    int i = this.elements >> 7;
    int j = this.elements & 0x7F;
    int k = this.objects.length;
    if (i >= k) {
      ConstPool13[][] arrayOfConstPool13 = new ConstPool13[k + 8][];
      System.arraycopy(this.objects, 0, arrayOfConstPool13, 0, k);
      this.objects = arrayOfConstPool13;
    } 
    if (this.objects[i] == null)
      this.objects[i] = new ConstPool13[128]; 
    this.objects[i][j] = paramConstPool13;
    this.elements++;
  }
}
