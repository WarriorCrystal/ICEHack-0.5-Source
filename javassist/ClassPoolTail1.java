package javassist;

final class ClassPoolTail1 {
  ClassPoolTail1 next;
  
  ClassPath path;
  
  ClassPoolTail1(ClassPath paramClassPath, ClassPoolTail1 paramClassPoolTail1) {
    this.next = paramClassPoolTail1;
    this.path = paramClassPath;
  }
}
