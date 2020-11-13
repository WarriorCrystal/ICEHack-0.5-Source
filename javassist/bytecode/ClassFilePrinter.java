package javassist.bytecode;

import java.io.PrintWriter;
import java.util.List;
import javassist.Modifier;

public class ClassFilePrinter {
  public static void print(ClassFile paramClassFile) {
    print(paramClassFile, new PrintWriter(System.out, true));
  }
  
  public static void print(ClassFile paramClassFile, PrintWriter paramPrintWriter) {
    int j = AccessFlag.toModifier(paramClassFile.getAccessFlags() & 0xFFFFFFDF);
    paramPrintWriter.println("major: " + paramClassFile.major + ", minor: " + paramClassFile.minor + " modifiers: " + 
        Integer.toHexString(paramClassFile.getAccessFlags()));
    paramPrintWriter.println(Modifier.toString(j) + " class " + paramClassFile
        .getName() + " extends " + paramClassFile.getSuperclass());
    String[] arrayOfString = paramClassFile.getInterfaces();
    if (arrayOfString != null && arrayOfString.length > 0) {
      paramPrintWriter.print("    implements ");
      paramPrintWriter.print(arrayOfString[0]);
      for (byte b1 = 1; b1 < arrayOfString.length; b1++)
        paramPrintWriter.print(", " + arrayOfString[b1]); 
      paramPrintWriter.println();
    } 
    paramPrintWriter.println();
    List<FieldInfo> list = paramClassFile.getFields();
    int i = list.size();
    byte b;
    for (b = 0; b < i; b++) {
      FieldInfo fieldInfo = list.get(b);
      int k = fieldInfo.getAccessFlags();
      paramPrintWriter.println(Modifier.toString(AccessFlag.toModifier(k)) + " " + fieldInfo
          .getName() + "\t" + fieldInfo
          .getDescriptor());
      printAttributes(fieldInfo.getAttributes(), paramPrintWriter, 'f');
    } 
    paramPrintWriter.println();
    list = paramClassFile.getMethods();
    i = list.size();
    for (b = 0; b < i; b++) {
      MethodInfo methodInfo = (MethodInfo)list.get(b);
      int k = methodInfo.getAccessFlags();
      paramPrintWriter.println(Modifier.toString(AccessFlag.toModifier(k)) + " " + methodInfo
          .getName() + "\t" + methodInfo
          .getDescriptor());
      printAttributes(methodInfo.getAttributes(), paramPrintWriter, 'm');
      paramPrintWriter.println();
    } 
    paramPrintWriter.println();
    printAttributes(paramClassFile.getAttributes(), paramPrintWriter, 'c');
  }
  
  static void printAttributes(List<AttributeInfo> paramList, PrintWriter paramPrintWriter, char paramChar) {
    if (paramList == null)
      return; 
    int i = paramList.size();
    for (byte b = 0; b < i; b++) {
      AttributeInfo attributeInfo = paramList.get(b);
      if (attributeInfo instanceof CodeAttribute) {
        CodeAttribute codeAttribute = (CodeAttribute)attributeInfo;
        paramPrintWriter.println("attribute: " + attributeInfo.getName() + ": " + attributeInfo
            .getClass().getName());
        paramPrintWriter.println("max stack " + codeAttribute.getMaxStack() + ", max locals " + codeAttribute
            .getMaxLocals() + ", " + codeAttribute
            .getExceptionTable().size() + " catch blocks");
        paramPrintWriter.println("<code attribute begin>");
        printAttributes(codeAttribute.getAttributes(), paramPrintWriter, paramChar);
        paramPrintWriter.println("<code attribute end>");
      } else if (attributeInfo instanceof AnnotationsAttribute) {
        paramPrintWriter.println("annnotation: " + attributeInfo.toString());
      } else if (attributeInfo instanceof ParameterAnnotationsAttribute) {
        paramPrintWriter.println("parameter annnotations: " + attributeInfo.toString());
      } else if (attributeInfo instanceof StackMapTable) {
        paramPrintWriter.println("<stack map table begin>");
        StackMapTable.Printer.print((StackMapTable)attributeInfo, paramPrintWriter);
        paramPrintWriter.println("<stack map table end>");
      } else if (attributeInfo instanceof StackMap) {
        paramPrintWriter.println("<stack map begin>");
        ((StackMap)attributeInfo).print(paramPrintWriter);
        paramPrintWriter.println("<stack map end>");
      } else if (attributeInfo instanceof SignatureAttribute) {
        SignatureAttribute signatureAttribute = (SignatureAttribute)attributeInfo;
        String str = signatureAttribute.getSignature();
        paramPrintWriter.println("signature: " + str);
        try {
          String str1;
          if (paramChar == 'c') {
            str1 = SignatureAttribute.toClassSignature(str).toString();
          } else if (paramChar == 'm') {
            str1 = SignatureAttribute.toMethodSignature(str).toString();
          } else {
            str1 = SignatureAttribute.toFieldSignature(str).toString();
          } 
          paramPrintWriter.println("           " + str1);
        } catch (BadBytecode badBytecode) {
          paramPrintWriter.println("           syntax error");
        } 
      } else {
        paramPrintWriter.println("attribute: " + attributeInfo.getName() + " (" + (attributeInfo
            .get()).length + " byte): " + attributeInfo
            .getClass().getName());
      } 
    } 
  }
}
