package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javassist.bytecode.annotation.AnnotationsWriter;
import javassist.bytecode.annotation.TypeAnnotationsWriter;

public class TypeAnnotationsAttribute extends AttributeInfo {
  public static final String visibleTag = "RuntimeVisibleTypeAnnotations";
  
  public static final String invisibleTag = "RuntimeInvisibleTypeAnnotations";
  
  public TypeAnnotationsAttribute(ConstPool14 paramConstPool14, String paramString, byte[] paramArrayOfbyte) {
    super(paramConstPool14, paramString, paramArrayOfbyte);
  }
  
  TypeAnnotationsAttribute(ConstPool14 paramConstPool14, int paramInt, DataInputStream paramDataInputStream) throws IOException {
    super(paramConstPool14, paramInt, paramDataInputStream);
  }
  
  public int numAnnotations() {
    return ByteArray.readU16bit(this.info, 0);
  }
  
  public AttributeInfo copy(ConstPool14 paramConstPool14, Map paramMap) {
    Copier copier = new Copier(this.info, this.constPool, paramConstPool14, paramMap);
    try {
      copier.annotationArray();
      return new TypeAnnotationsAttribute(paramConstPool14, getName(), copier.close());
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  void renameClass(String paramString1, String paramString2) {
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    hashMap.put(paramString1, paramString2);
    renameClass(hashMap);
  }
  
  void renameClass(Map paramMap) {
    Renamer renamer = new Renamer(this.info, getConstPool(), paramMap);
    try {
      renamer.annotationArray();
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  void getRefClasses(Map paramMap) {
    renameClass(paramMap);
  }
  
  static class TAWalker extends AnnotationsAttribute.Walker {
    TypeAnnotationsAttribute.SubWalker subWalker;
    
    TAWalker(byte[] param1ArrayOfbyte) {
      super(param1ArrayOfbyte);
      this.subWalker = new TypeAnnotationsAttribute.SubWalker(param1ArrayOfbyte);
    }
    
    int annotationArray(int param1Int1, int param1Int2) throws Exception {
      for (byte b = 0; b < param1Int2; b++) {
        int i = this.info[param1Int1] & 0xFF;
        param1Int1 = this.subWalker.targetInfo(param1Int1 + 1, i);
        param1Int1 = this.subWalker.typePath(param1Int1);
        param1Int1 = annotation(param1Int1);
      } 
      return param1Int1;
    }
  }
  
  static class SubWalker {
    byte[] info;
    
    SubWalker(byte[] param1ArrayOfbyte) {
      this.info = param1ArrayOfbyte;
    }
    
    final int targetInfo(int param1Int1, int param1Int2) throws Exception {
      int i;
      int j;
      switch (param1Int2) {
        case 0:
        case 1:
          i = this.info[param1Int1] & 0xFF;
          typeParameterTarget(param1Int1, param1Int2, i);
          return param1Int1 + 1;
        case 16:
          i = ByteArray.readU16bit(this.info, param1Int1);
          supertypeTarget(param1Int1, i);
          return param1Int1 + 2;
        case 17:
        case 18:
          i = this.info[param1Int1] & 0xFF;
          j = this.info[param1Int1 + 1] & 0xFF;
          typeParameterBoundTarget(param1Int1, param1Int2, i, j);
          return param1Int1 + 2;
        case 19:
        case 20:
        case 21:
          emptyTarget(param1Int1, param1Int2);
          return param1Int1;
        case 22:
          i = this.info[param1Int1] & 0xFF;
          formalParameterTarget(param1Int1, i);
          return param1Int1 + 1;
        case 23:
          i = ByteArray.readU16bit(this.info, param1Int1);
          throwsTarget(param1Int1, i);
          return param1Int1 + 2;
        case 64:
        case 65:
          i = ByteArray.readU16bit(this.info, param1Int1);
          return localvarTarget(param1Int1 + 2, param1Int2, i);
        case 66:
          i = ByteArray.readU16bit(this.info, param1Int1);
          catchTarget(param1Int1, i);
          return param1Int1 + 2;
        case 67:
        case 68:
        case 69:
        case 70:
          i = ByteArray.readU16bit(this.info, param1Int1);
          offsetTarget(param1Int1, param1Int2, i);
          return param1Int1 + 2;
        case 71:
        case 72:
        case 73:
        case 74:
        case 75:
          i = ByteArray.readU16bit(this.info, param1Int1);
          j = this.info[param1Int1 + 2] & 0xFF;
          typeArgumentTarget(param1Int1, param1Int2, i, j);
          return param1Int1 + 3;
      } 
      throw new RuntimeException("invalid target type: " + param1Int2);
    }
    
    void typeParameterTarget(int param1Int1, int param1Int2, int param1Int3) throws Exception {}
    
    void supertypeTarget(int param1Int1, int param1Int2) throws Exception {}
    
    void typeParameterBoundTarget(int param1Int1, int param1Int2, int param1Int3, int param1Int4) throws Exception {}
    
    void emptyTarget(int param1Int1, int param1Int2) throws Exception {}
    
    void formalParameterTarget(int param1Int1, int param1Int2) throws Exception {}
    
    void throwsTarget(int param1Int1, int param1Int2) throws Exception {}
    
    int localvarTarget(int param1Int1, int param1Int2, int param1Int3) throws Exception {
      for (byte b = 0; b < param1Int3; b++) {
        int i = ByteArray.readU16bit(this.info, param1Int1);
        int j = ByteArray.readU16bit(this.info, param1Int1 + 2);
        int k = ByteArray.readU16bit(this.info, param1Int1 + 4);
        localvarTarget(param1Int1, param1Int2, i, j, k);
        param1Int1 += 6;
      } 
      return param1Int1;
    }
    
    void localvarTarget(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) throws Exception {}
    
    void catchTarget(int param1Int1, int param1Int2) throws Exception {}
    
    void offsetTarget(int param1Int1, int param1Int2, int param1Int3) throws Exception {}
    
    void typeArgumentTarget(int param1Int1, int param1Int2, int param1Int3, int param1Int4) throws Exception {}
    
    final int typePath(int param1Int) throws Exception {
      int i = this.info[param1Int++] & 0xFF;
      return typePath(param1Int, i);
    }
    
    int typePath(int param1Int1, int param1Int2) throws Exception {
      for (byte b = 0; b < param1Int2; b++) {
        int i = this.info[param1Int1] & 0xFF;
        int j = this.info[param1Int1 + 1] & 0xFF;
        typePath(param1Int1, i, j);
        param1Int1 += 2;
      } 
      return param1Int1;
    }
    
    void typePath(int param1Int1, int param1Int2, int param1Int3) throws Exception {}
  }
  
  static class Renamer extends AnnotationsAttribute.Renamer {
    TypeAnnotationsAttribute.SubWalker sub;
    
    Renamer(byte[] param1ArrayOfbyte, ConstPool14 param1ConstPool14, Map param1Map) {
      super(param1ArrayOfbyte, param1ConstPool14, param1Map);
      this.sub = new TypeAnnotationsAttribute.SubWalker(param1ArrayOfbyte);
    }
    
    int annotationArray(int param1Int1, int param1Int2) throws Exception {
      for (byte b = 0; b < param1Int2; b++) {
        int i = this.info[param1Int1] & 0xFF;
        param1Int1 = this.sub.targetInfo(param1Int1 + 1, i);
        param1Int1 = this.sub.typePath(param1Int1);
        param1Int1 = annotation(param1Int1);
      } 
      return param1Int1;
    }
  }
  
  static class Copier extends AnnotationsAttribute.Copier {
    TypeAnnotationsAttribute.SubCopier sub;
    
    Copier(byte[] param1ArrayOfbyte, ConstPool14 param1ConstPool141, ConstPool14 param1ConstPool142, Map param1Map) {
      super(param1ArrayOfbyte, param1ConstPool141, param1ConstPool142, param1Map, false);
      TypeAnnotationsWriter typeAnnotationsWriter = new TypeAnnotationsWriter(this.output, param1ConstPool142);
      this.writer = (AnnotationsWriter)typeAnnotationsWriter;
      this.sub = new TypeAnnotationsAttribute.SubCopier(param1ArrayOfbyte, param1ConstPool141, param1ConstPool142, param1Map, typeAnnotationsWriter);
    }
    
    int annotationArray(int param1Int1, int param1Int2) throws Exception {
      this.writer.numAnnotations(param1Int2);
      for (byte b = 0; b < param1Int2; b++) {
        int i = this.info[param1Int1] & 0xFF;
        param1Int1 = this.sub.targetInfo(param1Int1 + 1, i);
        param1Int1 = this.sub.typePath(param1Int1);
        param1Int1 = annotation(param1Int1);
      } 
      return param1Int1;
    }
  }
  
  static class SubCopier extends SubWalker {
    ConstPool14 srcPool;
    
    ConstPool14 destPool;
    
    Map classnames;
    
    TypeAnnotationsWriter writer;
    
    SubCopier(byte[] param1ArrayOfbyte, ConstPool14 param1ConstPool141, ConstPool14 param1ConstPool142, Map param1Map, TypeAnnotationsWriter param1TypeAnnotationsWriter) {
      super(param1ArrayOfbyte);
      this.srcPool = param1ConstPool141;
      this.destPool = param1ConstPool142;
      this.classnames = param1Map;
      this.writer = param1TypeAnnotationsWriter;
    }
    
    void typeParameterTarget(int param1Int1, int param1Int2, int param1Int3) throws Exception {
      this.writer.typeParameterTarget(param1Int2, param1Int3);
    }
    
    void supertypeTarget(int param1Int1, int param1Int2) throws Exception {
      this.writer.supertypeTarget(param1Int2);
    }
    
    void typeParameterBoundTarget(int param1Int1, int param1Int2, int param1Int3, int param1Int4) throws Exception {
      this.writer.typeParameterBoundTarget(param1Int2, param1Int3, param1Int4);
    }
    
    void emptyTarget(int param1Int1, int param1Int2) throws Exception {
      this.writer.emptyTarget(param1Int2);
    }
    
    void formalParameterTarget(int param1Int1, int param1Int2) throws Exception {
      this.writer.formalParameterTarget(param1Int2);
    }
    
    void throwsTarget(int param1Int1, int param1Int2) throws Exception {
      this.writer.throwsTarget(param1Int2);
    }
    
    int localvarTarget(int param1Int1, int param1Int2, int param1Int3) throws Exception {
      this.writer.localVarTarget(param1Int2, param1Int3);
      return super.localvarTarget(param1Int1, param1Int2, param1Int3);
    }
    
    void localvarTarget(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) throws Exception {
      this.writer.localVarTargetTable(param1Int3, param1Int4, param1Int5);
    }
    
    void catchTarget(int param1Int1, int param1Int2) throws Exception {
      this.writer.catchTarget(param1Int2);
    }
    
    void offsetTarget(int param1Int1, int param1Int2, int param1Int3) throws Exception {
      this.writer.offsetTarget(param1Int2, param1Int3);
    }
    
    void typeArgumentTarget(int param1Int1, int param1Int2, int param1Int3, int param1Int4) throws Exception {
      this.writer.typeArgumentTarget(param1Int2, param1Int3, param1Int4);
    }
    
    int typePath(int param1Int1, int param1Int2) throws Exception {
      this.writer.typePath(param1Int2);
      return super.typePath(param1Int1, param1Int2);
    }
    
    void typePath(int param1Int1, int param1Int2, int param1Int3) throws Exception {
      this.writer.typePathPath(param1Int2, param1Int3);
    }
  }
}
