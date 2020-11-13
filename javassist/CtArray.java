package javassist;

final class CtArray extends CtClass {
  protected ClassPool pool;
  
  private CtClass[] interfaces;
  
  CtArray(String paramString, ClassPool paramClassPool) {
    super(paramString);
    this.interfaces = null;
    this.pool = paramClassPool;
  }
  
  public ClassPool getClassPool() {
    return this.pool;
  }
  
  public boolean isArray() {
    return true;
  }
  
  public int getModifiers() {
    int i = 16;
    try {
      i |= getComponentType().getModifiers() & 0x7;
    } catch (NotFoundException notFoundException) {}
    return i;
  }
  
  public CtClass[] getInterfaces() throws NotFoundException {
    if (this.interfaces == null) {
      Class[] arrayOfClass = Object[].class.getInterfaces();
      this.interfaces = new CtClass[arrayOfClass.length];
      for (byte b = 0; b < arrayOfClass.length; b++)
        this.interfaces[b] = this.pool.get(arrayOfClass[b].getName()); 
    } 
    return this.interfaces;
  }
  
  public boolean subtypeOf(CtClass paramCtClass) throws NotFoundException {
    if (super.subtypeOf(paramCtClass))
      return true; 
    String str = paramCtClass.getName();
    if (str.equals("java.lang.Object"))
      return true; 
    CtClass[] arrayOfCtClass = getInterfaces();
    for (byte b = 0; b < arrayOfCtClass.length; b++) {
      if (arrayOfCtClass[b].subtypeOf(paramCtClass))
        return true; 
    } 
    return (paramCtClass.isArray() && 
      getComponentType().subtypeOf(paramCtClass.getComponentType()));
  }
  
  public CtClass getComponentType() throws NotFoundException {
    String str = getName();
    return this.pool.get(str.substring(0, str.length() - 2));
  }
  
  public CtClass getSuperclass() throws NotFoundException {
    return this.pool.get("java.lang.Object");
  }
  
  public CtMethod[] getMethods() {
    try {
      return getSuperclass().getMethods();
    } catch (NotFoundException notFoundException) {
      return super.getMethods();
    } 
  }
  
  public CtMethod getMethod(String paramString1, String paramString2) throws NotFoundException {
    return getSuperclass().getMethod(paramString1, paramString2);
  }
  
  public CtConstructor[] getConstructors() {
    try {
      return getSuperclass().getConstructors();
    } catch (NotFoundException notFoundException) {
      return super.getConstructors();
    } 
  }
}
