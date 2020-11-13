package javassist;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;

public class SerialVersionUID {
  public static void setSerialVersionUID(CtClass paramCtClass) throws CannotCompileException, NotFoundException {
    try {
      paramCtClass.getDeclaredField("serialVersionUID");
      return;
    } catch (NotFoundException notFoundException) {
      if (!isSerializable(paramCtClass))
        return; 
      CtField ctField = new CtField(CtClass.longType, "serialVersionUID", paramCtClass);
      ctField.setModifiers(26);
      paramCtClass.addField(ctField, calculateDefault(paramCtClass) + "L");
      return;
    } 
  }
  
  private static boolean isSerializable(CtClass paramCtClass) throws NotFoundException {
    ClassPool classPool = paramCtClass.getClassPool();
    return paramCtClass.subtypeOf(classPool.get("java.io.Serializable"));
  }
  
  public static long calculateDefault(CtClass paramCtClass) throws CannotCompileException {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
      ClassFile classFile = paramCtClass.getClassFile();
      String str = javaName(paramCtClass);
      dataOutputStream.writeUTF(str);
      CtMethod[] arrayOfCtMethod = paramCtClass.getDeclaredMethods();
      int i = paramCtClass.getModifiers();
      if ((i & 0x200) != 0)
        if (arrayOfCtMethod.length > 0) {
          i |= 0x400;
        } else {
          i &= 0xFFFFFBFF;
        }  
      dataOutputStream.writeInt(i);
      String[] arrayOfString = classFile.getInterfaces();
      byte b1;
      for (b1 = 0; b1 < arrayOfString.length; b1++)
        arrayOfString[b1] = javaName(arrayOfString[b1]); 
      Arrays.sort((Object[])arrayOfString);
      for (b1 = 0; b1 < arrayOfString.length; b1++)
        dataOutputStream.writeUTF(arrayOfString[b1]); 
      CtField[] arrayOfCtField = paramCtClass.getDeclaredFields();
      Arrays.sort(arrayOfCtField, new Comparator<CtField>() {
            public int compare(Object param1Object1, Object param1Object2) {
              CtField ctField1 = (CtField)param1Object1;
              CtField ctField2 = (CtField)param1Object2;
              return ctField1.getName().compareTo(ctField2.getName());
            }
          });
      for (byte b2 = 0; b2 < arrayOfCtField.length; b2++) {
        CtField ctField = arrayOfCtField[b2];
        int k = ctField.getModifiers();
        if ((k & 0x2) == 0 || (k & 0x88) == 0) {
          dataOutputStream.writeUTF(ctField.getName());
          dataOutputStream.writeInt(k);
          dataOutputStream.writeUTF(ctField.getFieldInfo2().getDescriptor());
        } 
      } 
      if (classFile.getStaticInitializer() != null) {
        dataOutputStream.writeUTF("<clinit>");
        dataOutputStream.writeInt(8);
        dataOutputStream.writeUTF("()V");
      } 
      CtConstructor[] arrayOfCtConstructor = paramCtClass.getDeclaredConstructors();
      Arrays.sort(arrayOfCtConstructor, new Comparator<CtConstructor>() {
            public int compare(Object param1Object1, Object param1Object2) {
              CtConstructor ctConstructor1 = (CtConstructor)param1Object1;
              CtConstructor ctConstructor2 = (CtConstructor)param1Object2;
              return ctConstructor1.getMethodInfo2().getDescriptor().compareTo(ctConstructor2
                  .getMethodInfo2().getDescriptor());
            }
          });
      byte b3;
      for (b3 = 0; b3 < arrayOfCtConstructor.length; b3++) {
        CtConstructor ctConstructor = arrayOfCtConstructor[b3];
        int k = ctConstructor.getModifiers();
        if ((k & 0x2) == 0) {
          dataOutputStream.writeUTF("<init>");
          dataOutputStream.writeInt(k);
          dataOutputStream.writeUTF(ctConstructor.getMethodInfo2()
              .getDescriptor().replace('/', '.'));
        } 
      } 
      Arrays.sort(arrayOfCtMethod, new Comparator<CtMethod>() {
            public int compare(Object param1Object1, Object param1Object2) {
              CtMethod ctMethod1 = (CtMethod)param1Object1;
              CtMethod ctMethod2 = (CtMethod)param1Object2;
              int i = ctMethod1.getName().compareTo(ctMethod2.getName());
              if (i == 0)
                i = ctMethod1.getMethodInfo2().getDescriptor().compareTo(ctMethod2.getMethodInfo2().getDescriptor()); 
              return i;
            }
          });
      for (b3 = 0; b3 < arrayOfCtMethod.length; b3++) {
        CtMethod ctMethod = arrayOfCtMethod[b3];
        int k = ctMethod.getModifiers() & 0xD3F;
        if ((k & 0x2) == 0) {
          dataOutputStream.writeUTF(ctMethod.getName());
          dataOutputStream.writeInt(k);
          dataOutputStream.writeUTF(ctMethod.getMethodInfo2()
              .getDescriptor().replace('/', '.'));
        } 
      } 
      dataOutputStream.flush();
      MessageDigest messageDigest = MessageDigest.getInstance("SHA");
      byte[] arrayOfByte = messageDigest.digest(byteArrayOutputStream.toByteArray());
      long l = 0L;
      for (int j = Math.min(arrayOfByte.length, 8) - 1; j >= 0; j--)
        l = l << 8L | (arrayOfByte[j] & 0xFF); 
      return l;
    } catch (IOException iOException) {
      throw new CannotCompileException(iOException);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new CannotCompileException(noSuchAlgorithmException);
    } 
  }
  
  private static String javaName(CtClass paramCtClass) {
    return Descriptor.toJavaName(Descriptor.toJvmName(paramCtClass));
  }
  
  private static String javaName(String paramString) {
    return Descriptor.toJavaName(Descriptor.toJvmName(paramString));
  }
}
