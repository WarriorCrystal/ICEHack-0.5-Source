package javassist.bytecode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ClassFileWriter {
  private ByteStream output;
  
  private ConstPoolWriter constPool;
  
  private FieldWriter fields;
  
  private MethodWriter methods;
  
  int thisClass;
  
  int superClass;
  
  public ClassFileWriter(int paramInt1, int paramInt2) {
    this.output = new ByteStream(512);
    this.output.writeInt(-889275714);
    this.output.writeShort(paramInt2);
    this.output.writeShort(paramInt1);
    this.constPool = new ConstPoolWriter(this.output);
    this.fields = new FieldWriter(this.constPool);
    this.methods = new MethodWriter(this.constPool);
  }
  
  public ConstPoolWriter getConstPool() {
    return this.constPool;
  }
  
  public FieldWriter getFieldWriter() {
    return this.fields;
  }
  
  public MethodWriter getMethodWriter() {
    return this.methods;
  }
  
  public byte[] end(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfint, AttributeWriter paramAttributeWriter) {
    this.constPool.end();
    this.output.writeShort(paramInt1);
    this.output.writeShort(paramInt2);
    this.output.writeShort(paramInt3);
    if (paramArrayOfint == null) {
      this.output.writeShort(0);
    } else {
      int i = paramArrayOfint.length;
      this.output.writeShort(i);
      for (byte b = 0; b < i; b++)
        this.output.writeShort(paramArrayOfint[b]); 
    } 
    this.output.enlarge(this.fields.dataSize() + this.methods.dataSize() + 6);
    try {
      this.output.writeShort(this.fields.size());
      this.fields.write(this.output);
      this.output.writeShort(this.methods.numOfMethods());
      this.methods.write(this.output);
    } catch (IOException iOException) {}
    writeAttribute(this.output, paramAttributeWriter, 0);
    return this.output.toByteArray();
  }
  
  public void end(DataOutputStream paramDataOutputStream, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfint, AttributeWriter paramAttributeWriter) throws IOException {
    this.constPool.end();
    this.output.writeTo(paramDataOutputStream);
    paramDataOutputStream.writeShort(paramInt1);
    paramDataOutputStream.writeShort(paramInt2);
    paramDataOutputStream.writeShort(paramInt3);
    if (paramArrayOfint == null) {
      paramDataOutputStream.writeShort(0);
    } else {
      int i = paramArrayOfint.length;
      paramDataOutputStream.writeShort(i);
      for (byte b = 0; b < i; b++)
        paramDataOutputStream.writeShort(paramArrayOfint[b]); 
    } 
    paramDataOutputStream.writeShort(this.fields.size());
    this.fields.write(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.methods.numOfMethods());
    this.methods.write(paramDataOutputStream);
    if (paramAttributeWriter == null) {
      paramDataOutputStream.writeShort(0);
    } else {
      paramDataOutputStream.writeShort(paramAttributeWriter.size());
      paramAttributeWriter.write(paramDataOutputStream);
    } 
  }
  
  static void writeAttribute(ByteStream paramByteStream, AttributeWriter paramAttributeWriter, int paramInt) {
    if (paramAttributeWriter == null) {
      paramByteStream.writeShort(paramInt);
      return;
    } 
    paramByteStream.writeShort(paramAttributeWriter.size() + paramInt);
    DataOutputStream dataOutputStream = new DataOutputStream(paramByteStream);
    try {
      paramAttributeWriter.write(dataOutputStream);
      dataOutputStream.flush();
    } catch (IOException iOException) {}
  }
  
  public static interface AttributeWriter {
    int size();
    
    void write(DataOutputStream param1DataOutputStream) throws IOException;
  }
  
  public static final class FieldWriter {
    protected ByteStream output;
    
    protected ClassFileWriter.ConstPoolWriter constPool;
    
    private int fieldCount;
    
    FieldWriter(ClassFileWriter.ConstPoolWriter param1ConstPoolWriter) {
      this.output = new ByteStream(128);
      this.constPool = param1ConstPoolWriter;
      this.fieldCount = 0;
    }
    
    public void add(int param1Int, String param1String1, String param1String2, ClassFileWriter.AttributeWriter param1AttributeWriter) {
      int i = this.constPool.addUtf8Info(param1String1);
      int j = this.constPool.addUtf8Info(param1String2);
      add(param1Int, i, j, param1AttributeWriter);
    }
    
    public void add(int param1Int1, int param1Int2, int param1Int3, ClassFileWriter.AttributeWriter param1AttributeWriter) {
      this.fieldCount++;
      this.output.writeShort(param1Int1);
      this.output.writeShort(param1Int2);
      this.output.writeShort(param1Int3);
      ClassFileWriter.writeAttribute(this.output, param1AttributeWriter, 0);
    }
    
    int size() {
      return this.fieldCount;
    }
    
    int dataSize() {
      return this.output.size();
    }
    
    void write(OutputStream param1OutputStream) throws IOException {
      this.output.writeTo(param1OutputStream);
    }
  }
  
  public static final class MethodWriter {
    protected ByteStream output;
    
    protected ClassFileWriter.ConstPoolWriter constPool;
    
    private int methodCount;
    
    protected int codeIndex;
    
    protected int throwsIndex;
    
    protected int stackIndex;
    
    private int startPos;
    
    private boolean isAbstract;
    
    private int catchPos;
    
    private int catchCount;
    
    MethodWriter(ClassFileWriter.ConstPoolWriter param1ConstPoolWriter) {
      this.output = new ByteStream(256);
      this.constPool = param1ConstPoolWriter;
      this.methodCount = 0;
      this.codeIndex = 0;
      this.throwsIndex = 0;
      this.stackIndex = 0;
    }
    
    public void begin(int param1Int, String param1String1, String param1String2, String[] param1ArrayOfString, ClassFileWriter.AttributeWriter param1AttributeWriter) {
      int arrayOfInt[], i = this.constPool.addUtf8Info(param1String1);
      int j = this.constPool.addUtf8Info(param1String2);
      if (param1ArrayOfString == null) {
        arrayOfInt = null;
      } else {
        arrayOfInt = this.constPool.addClassInfo(param1ArrayOfString);
      } 
      begin(param1Int, i, j, arrayOfInt, param1AttributeWriter);
    }
    
    public void begin(int param1Int1, int param1Int2, int param1Int3, int[] param1ArrayOfint, ClassFileWriter.AttributeWriter param1AttributeWriter) {
      this.methodCount++;
      this.output.writeShort(param1Int1);
      this.output.writeShort(param1Int2);
      this.output.writeShort(param1Int3);
      this.isAbstract = ((param1Int1 & 0x400) != 0);
      byte b = this.isAbstract ? 0 : 1;
      if (param1ArrayOfint != null)
        b++; 
      ClassFileWriter.writeAttribute(this.output, param1AttributeWriter, b);
      if (param1ArrayOfint != null)
        writeThrows(param1ArrayOfint); 
      if (!this.isAbstract) {
        if (this.codeIndex == 0)
          this.codeIndex = this.constPool.addUtf8Info("Code"); 
        this.startPos = this.output.getPos();
        this.output.writeShort(this.codeIndex);
        this.output.writeBlank(12);
      } 
      this.catchPos = -1;
      this.catchCount = 0;
    }
    
    private void writeThrows(int[] param1ArrayOfint) {
      if (this.throwsIndex == 0)
        this.throwsIndex = this.constPool.addUtf8Info("Exceptions"); 
      this.output.writeShort(this.throwsIndex);
      this.output.writeInt(param1ArrayOfint.length * 2 + 2);
      this.output.writeShort(param1ArrayOfint.length);
      for (byte b = 0; b < param1ArrayOfint.length; b++)
        this.output.writeShort(param1ArrayOfint[b]); 
    }
    
    public void add(int param1Int) {
      this.output.write(param1Int);
    }
    
    public void add16(int param1Int) {
      this.output.writeShort(param1Int);
    }
    
    public void add32(int param1Int) {
      this.output.writeInt(param1Int);
    }
    
    public void addInvoke(int param1Int, String param1String1, String param1String2, String param1String3) {
      int i = this.constPool.addClassInfo(param1String1);
      int j = this.constPool.addNameAndTypeInfo(param1String2, param1String3);
      int k = this.constPool.addMethodrefInfo(i, j);
      add(param1Int);
      add16(k);
    }
    
    public void codeEnd(int param1Int1, int param1Int2) {
      if (!this.isAbstract) {
        this.output.writeShort(this.startPos + 6, param1Int1);
        this.output.writeShort(this.startPos + 8, param1Int2);
        this.output.writeInt(this.startPos + 10, this.output.getPos() - this.startPos - 14);
        this.catchPos = this.output.getPos();
        this.catchCount = 0;
        this.output.writeShort(0);
      } 
    }
    
    public void addCatch(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this.catchCount++;
      this.output.writeShort(param1Int1);
      this.output.writeShort(param1Int2);
      this.output.writeShort(param1Int3);
      this.output.writeShort(param1Int4);
    }
    
    public void end(StackMapTable.Writer param1Writer, ClassFileWriter.AttributeWriter param1AttributeWriter) {
      if (this.isAbstract)
        return; 
      this.output.writeShort(this.catchPos, this.catchCount);
      boolean bool = (param1Writer == null) ? false : true;
      ClassFileWriter.writeAttribute(this.output, param1AttributeWriter, bool);
      if (param1Writer != null) {
        if (this.stackIndex == 0)
          this.stackIndex = this.constPool.addUtf8Info("StackMapTable"); 
        this.output.writeShort(this.stackIndex);
        byte[] arrayOfByte = param1Writer.toByteArray();
        this.output.writeInt(arrayOfByte.length);
        this.output.write(arrayOfByte);
      } 
      this.output.writeInt(this.startPos + 2, this.output.getPos() - this.startPos - 6);
    }
    
    public int size() {
      return this.output.getPos() - this.startPos - 14;
    }
    
    int numOfMethods() {
      return this.methodCount;
    }
    
    int dataSize() {
      return this.output.size();
    }
    
    void write(OutputStream param1OutputStream) throws IOException {
      this.output.writeTo(param1OutputStream);
    }
  }
  
  public static final class ConstPoolWriter {
    ByteStream output;
    
    protected int startPos;
    
    protected int num;
    
    ConstPoolWriter(ByteStream param1ByteStream) {
      this.output = param1ByteStream;
      this.startPos = param1ByteStream.getPos();
      this.num = 1;
      this.output.writeShort(1);
    }
    
    public int[] addClassInfo(String[] param1ArrayOfString) {
      int i = param1ArrayOfString.length;
      int[] arrayOfInt = new int[i];
      for (byte b = 0; b < i; b++)
        arrayOfInt[b] = addClassInfo(param1ArrayOfString[b]); 
      return arrayOfInt;
    }
    
    public int addClassInfo(String param1String) {
      int i = addUtf8Info(param1String);
      this.output.write(7);
      this.output.writeShort(i);
      return this.num++;
    }
    
    public int addClassInfo(int param1Int) {
      this.output.write(7);
      this.output.writeShort(param1Int);
      return this.num++;
    }
    
    public int addNameAndTypeInfo(String param1String1, String param1String2) {
      return addNameAndTypeInfo(addUtf8Info(param1String1), addUtf8Info(param1String2));
    }
    
    public int addNameAndTypeInfo(int param1Int1, int param1Int2) {
      this.output.write(12);
      this.output.writeShort(param1Int1);
      this.output.writeShort(param1Int2);
      return this.num++;
    }
    
    public int addFieldrefInfo(int param1Int1, int param1Int2) {
      this.output.write(9);
      this.output.writeShort(param1Int1);
      this.output.writeShort(param1Int2);
      return this.num++;
    }
    
    public int addMethodrefInfo(int param1Int1, int param1Int2) {
      this.output.write(10);
      this.output.writeShort(param1Int1);
      this.output.writeShort(param1Int2);
      return this.num++;
    }
    
    public int addInterfaceMethodrefInfo(int param1Int1, int param1Int2) {
      this.output.write(11);
      this.output.writeShort(param1Int1);
      this.output.writeShort(param1Int2);
      return this.num++;
    }
    
    public int addMethodHandleInfo(int param1Int1, int param1Int2) {
      this.output.write(15);
      this.output.write(param1Int1);
      this.output.writeShort(param1Int2);
      return this.num++;
    }
    
    public int addMethodTypeInfo(int param1Int) {
      this.output.write(16);
      this.output.writeShort(param1Int);
      return this.num++;
    }
    
    public int addInvokeDynamicInfo(int param1Int1, int param1Int2) {
      this.output.write(18);
      this.output.writeShort(param1Int1);
      this.output.writeShort(param1Int2);
      return this.num++;
    }
    
    public int addStringInfo(String param1String) {
      int i = addUtf8Info(param1String);
      this.output.write(8);
      this.output.writeShort(i);
      return this.num++;
    }
    
    public int addIntegerInfo(int param1Int) {
      this.output.write(3);
      this.output.writeInt(param1Int);
      return this.num++;
    }
    
    public int addFloatInfo(float param1Float) {
      this.output.write(4);
      this.output.writeFloat(param1Float);
      return this.num++;
    }
    
    public int addLongInfo(long param1Long) {
      this.output.write(5);
      this.output.writeLong(param1Long);
      int i = this.num;
      this.num += 2;
      return i;
    }
    
    public int addDoubleInfo(double param1Double) {
      this.output.write(6);
      this.output.writeDouble(param1Double);
      int i = this.num;
      this.num += 2;
      return i;
    }
    
    public int addUtf8Info(String param1String) {
      this.output.write(1);
      this.output.writeUTF(param1String);
      return this.num++;
    }
    
    void end() {
      this.output.writeShort(this.startPos, this.num);
    }
  }
}
