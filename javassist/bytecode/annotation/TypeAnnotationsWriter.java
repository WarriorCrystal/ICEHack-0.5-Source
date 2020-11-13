package javassist.bytecode.annotation;

import java.io.IOException;
import java.io.OutputStream;
import javassist.bytecode.ConstPool14;

public class TypeAnnotationsWriter extends AnnotationsWriter {
  public TypeAnnotationsWriter(OutputStream paramOutputStream, ConstPool14 paramConstPool14) {
    super(paramOutputStream, paramConstPool14);
  }
  
  public void numAnnotations(int paramInt) throws IOException {
    super.numAnnotations(paramInt);
  }
  
  public void typeParameterTarget(int paramInt1, int paramInt2) throws IOException {
    this.output.write(paramInt1);
    this.output.write(paramInt2);
  }
  
  public void supertypeTarget(int paramInt) throws IOException {
    this.output.write(16);
    write16bit(paramInt);
  }
  
  public void typeParameterBoundTarget(int paramInt1, int paramInt2, int paramInt3) throws IOException {
    this.output.write(paramInt1);
    this.output.write(paramInt2);
    this.output.write(paramInt3);
  }
  
  public void emptyTarget(int paramInt) throws IOException {
    this.output.write(paramInt);
  }
  
  public void formalParameterTarget(int paramInt) throws IOException {
    this.output.write(22);
    this.output.write(paramInt);
  }
  
  public void throwsTarget(int paramInt) throws IOException {
    this.output.write(23);
    write16bit(paramInt);
  }
  
  public void localVarTarget(int paramInt1, int paramInt2) throws IOException {
    this.output.write(paramInt1);
    write16bit(paramInt2);
  }
  
  public void localVarTargetTable(int paramInt1, int paramInt2, int paramInt3) throws IOException {
    write16bit(paramInt1);
    write16bit(paramInt2);
    write16bit(paramInt3);
  }
  
  public void catchTarget(int paramInt) throws IOException {
    this.output.write(66);
    write16bit(paramInt);
  }
  
  public void offsetTarget(int paramInt1, int paramInt2) throws IOException {
    this.output.write(paramInt1);
    write16bit(paramInt2);
  }
  
  public void typeArgumentTarget(int paramInt1, int paramInt2, int paramInt3) throws IOException {
    this.output.write(paramInt1);
    write16bit(paramInt2);
    this.output.write(paramInt3);
  }
  
  public void typePath(int paramInt) throws IOException {
    this.output.write(paramInt);
  }
  
  public void typePathPath(int paramInt1, int paramInt2) throws IOException {
    this.output.write(paramInt1);
    this.output.write(paramInt2);
  }
}
