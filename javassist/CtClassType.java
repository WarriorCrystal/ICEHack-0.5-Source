package javassist;

class CtClassType {
  CtClassType next;
  
  CtField field;
  
  CtField.Initializer init;
  
  CtClassType(CtField paramCtField, CtField.Initializer paramInitializer) {
    this.next = null;
    this.field = paramCtField;
    this.init = paramInitializer;
  }
}
