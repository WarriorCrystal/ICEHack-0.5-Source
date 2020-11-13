package javassist;

public abstract class CtMember {
  CtMember next;
  
  protected CtClass declaringClass;
  
  static class Cache extends CtMember {
    private CtMember methodTail;
    
    private CtMember consTail;
    
    private CtMember fieldTail;
    
    protected void extendToString(StringBuffer param1StringBuffer) {}
    
    public boolean hasAnnotation(String param1String) {
      return false;
    }
    
    public Object getAnnotation(Class param1Class) throws ClassNotFoundException {
      return null;
    }
    
    public Object[] getAnnotations() throws ClassNotFoundException {
      return null;
    }
    
    public byte[] getAttribute(String param1String) {
      return null;
    }
    
    public Object[] getAvailableAnnotations() {
      return null;
    }
    
    public int getModifiers() {
      return 0;
    }
    
    public String getName() {
      return null;
    }
    
    public String getSignature() {
      return null;
    }
    
    public void setAttribute(String param1String, byte[] param1ArrayOfbyte) {}
    
    public void setModifiers(int param1Int) {}
    
    public String getGenericSignature() {
      return null;
    }
    
    public void setGenericSignature(String param1String) {}
    
    Cache(CtClassType1 param1CtClassType1) {
      super(param1CtClassType1);
      this.methodTail = this;
      this.consTail = this;
      this.fieldTail = this;
      this.fieldTail.next = this;
    }
    
    CtMember methodHead() {
      return this;
    }
    
    CtMember lastMethod() {
      return this.methodTail;
    }
    
    CtMember consHead() {
      return this.methodTail;
    }
    
    CtMember lastCons() {
      return this.consTail;
    }
    
    CtMember fieldHead() {
      return this.consTail;
    }
    
    CtMember lastField() {
      return this.fieldTail;
    }
    
    void addMethod(CtMember param1CtMember) {
      param1CtMember.next = this.methodTail.next;
      this.methodTail.next = param1CtMember;
      if (this.methodTail == this.consTail) {
        this.consTail = param1CtMember;
        if (this.methodTail == this.fieldTail)
          this.fieldTail = param1CtMember; 
      } 
      this.methodTail = param1CtMember;
    }
    
    void addConstructor(CtMember param1CtMember) {
      param1CtMember.next = this.consTail.next;
      this.consTail.next = param1CtMember;
      if (this.consTail == this.fieldTail)
        this.fieldTail = param1CtMember; 
      this.consTail = param1CtMember;
    }
    
    void addField(CtMember param1CtMember) {
      param1CtMember.next = this;
      this.fieldTail.next = param1CtMember;
      this.fieldTail = param1CtMember;
    }
    
    static int count(CtMember param1CtMember1, CtMember param1CtMember2) {
      byte b = 0;
      while (param1CtMember1 != param1CtMember2) {
        b++;
        param1CtMember1 = param1CtMember1.next;
      } 
      return b;
    }
    
    void remove(CtMember param1CtMember) {
      Cache cache = this;
      CtMember ctMember;
      while ((ctMember = cache.next) != this) {
        if (ctMember == param1CtMember) {
          cache.next = ctMember.next;
          if (ctMember == this.methodTail)
            this.methodTail = cache; 
          if (ctMember == this.consTail)
            this.consTail = cache; 
          if (ctMember == this.fieldTail)
            this.fieldTail = cache; 
          break;
        } 
        CtMember ctMember1 = cache.next;
      } 
    }
  }
  
  protected CtMember(CtClass paramCtClass) {
    this.declaringClass = paramCtClass;
    this.next = null;
  }
  
  final CtMember next() {
    return this.next;
  }
  
  void nameReplaced() {}
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(getClass().getName());
    stringBuffer.append("@");
    stringBuffer.append(Integer.toHexString(hashCode()));
    stringBuffer.append("[");
    stringBuffer.append(Modifier.toString(getModifiers()));
    extendToString(stringBuffer);
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  protected abstract void extendToString(StringBuffer paramStringBuffer);
  
  public CtClass getDeclaringClass() {
    return this.declaringClass;
  }
  
  public boolean visibleFrom(CtClass paramCtClass) {
    boolean bool;
    int i = getModifiers();
    if (Modifier.isPublic(i))
      return true; 
    if (Modifier.isPrivate(i))
      return (paramCtClass == this.declaringClass); 
    String str1 = this.declaringClass.getPackageName();
    String str2 = paramCtClass.getPackageName();
    if (str1 == null) {
      bool = (str2 == null);
    } else {
      bool = str1.equals(str2);
    } 
    if (!bool && Modifier.isProtected(i))
      return paramCtClass.subclassOf(this.declaringClass); 
    return bool;
  }
  
  public abstract int getModifiers();
  
  public abstract void setModifiers(int paramInt);
  
  public boolean hasAnnotation(Class paramClass) {
    return hasAnnotation(paramClass.getName());
  }
  
  public abstract boolean hasAnnotation(String paramString);
  
  public abstract Object getAnnotation(Class paramClass) throws ClassNotFoundException;
  
  public abstract Object[] getAnnotations() throws ClassNotFoundException;
  
  public abstract Object[] getAvailableAnnotations();
  
  public abstract String getName();
  
  public abstract String getSignature();
  
  public abstract String getGenericSignature();
  
  public abstract void setGenericSignature(String paramString);
  
  public abstract byte[] getAttribute(String paramString);
  
  public abstract void setAttribute(String paramString, byte[] paramArrayOfbyte);
}
