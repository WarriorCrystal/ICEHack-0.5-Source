package org.yaml.snakeyaml.representer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.reader.StreamReader;

class SafeRepresenter extends BaseRepresenter {
  protected Map<Class<? extends Object>, Tag> classTags;
  
  protected TimeZone timeZone = null;
  
  public SafeRepresenter() {
    this.nullRepresenter = new RepresentNull();
    this.representers.put(String.class, new RepresentString());
    this.representers.put(Boolean.class, new RepresentBoolean());
    this.representers.put(Character.class, new RepresentString());
    this.representers.put(UUID.class, new RepresentUuid());
    this.representers.put(byte[].class, new RepresentByteArray());
    RepresentPrimitiveArray representPrimitiveArray = new RepresentPrimitiveArray();
    this.representers.put(short[].class, representPrimitiveArray);
    this.representers.put(int[].class, representPrimitiveArray);
    this.representers.put(long[].class, representPrimitiveArray);
    this.representers.put(float[].class, representPrimitiveArray);
    this.representers.put(double[].class, representPrimitiveArray);
    this.representers.put(char[].class, representPrimitiveArray);
    this.representers.put(boolean[].class, representPrimitiveArray);
    this.multiRepresenters.put(Number.class, new RepresentNumber());
    this.multiRepresenters.put(List.class, new RepresentList());
    this.multiRepresenters.put(Map.class, new RepresentMap());
    this.multiRepresenters.put(Set.class, new RepresentSet());
    this.multiRepresenters.put(Iterator.class, new RepresentIterator());
    this.multiRepresenters.put((new Object[0]).getClass(), new RepresentArray());
    this.multiRepresenters.put(Date.class, new RepresentDate());
    this.multiRepresenters.put(Enum.class, new RepresentEnum());
    this.multiRepresenters.put(Calendar.class, new RepresentDate());
    this.classTags = new HashMap<Class<? extends Object>, Tag>();
  }
  
  protected Tag getTag(Class<?> paramClass, Tag paramTag) {
    if (this.classTags.containsKey(paramClass))
      return this.classTags.get(paramClass); 
    return paramTag;
  }
  
  public Tag addClassTag(Class<? extends Object> paramClass, Tag paramTag) {
    if (paramTag == null)
      throw new NullPointerException("Tag must be provided."); 
    return this.classTags.put(paramClass, paramTag);
  }
  
  protected class RepresentNull implements Represent {
    public Node representData(Object param1Object) {
      return SafeRepresenter.this.representScalar(Tag.NULL, "null");
    }
  }
  
  public static Pattern MULTILINE_PATTERN = Pattern.compile("\n|| | ");
  
  protected class RepresentString implements Represent {
    public Node representData(Object param1Object) {
      Tag tag = Tag.STR;
      Character character = null;
      String str = param1Object.toString();
      if (!StreamReader.isPrintable(str)) {
        char[] arrayOfChar;
        tag = Tag.BINARY;
        try {
          byte[] arrayOfByte = str.getBytes("UTF-8");
          String str1 = new String(arrayOfByte, "UTF-8");
          if (!str1.equals(str))
            throw new YAMLException("invalid string value has occurred"); 
          arrayOfChar = Base64Coder.encode(arrayOfByte);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          throw new YAMLException(unsupportedEncodingException);
        } 
        str = String.valueOf(arrayOfChar);
        character = Character.valueOf('|');
      } 
      if (SafeRepresenter.this.defaultScalarStyle == null && SafeRepresenter.MULTILINE_PATTERN.matcher(str).find())
        character = Character.valueOf('|'); 
      return SafeRepresenter.this.representScalar(tag, str, character);
    }
  }
  
  protected class RepresentBoolean implements Represent {
    public Node representData(Object param1Object) {
      String str;
      if (Boolean.TRUE.equals(param1Object)) {
        str = "true";
      } else {
        str = "false";
      } 
      return SafeRepresenter.this.representScalar(Tag.BOOL, str);
    }
  }
  
  protected class RepresentNumber implements Represent {
    public Node representData(Object param1Object) {
      Tag tag;
      String str;
      if (param1Object instanceof Byte || param1Object instanceof Short || param1Object instanceof Integer || param1Object instanceof Long || param1Object instanceof java.math.BigInteger) {
        tag = Tag.INT;
        str = param1Object.toString();
      } else {
        Number number = (Number)param1Object;
        tag = Tag.FLOAT;
        if (number.equals(Double.valueOf(Double.NaN))) {
          str = ".NaN";
        } else if (number.equals(Double.valueOf(Double.POSITIVE_INFINITY))) {
          str = ".inf";
        } else if (number.equals(Double.valueOf(Double.NEGATIVE_INFINITY))) {
          str = "-.inf";
        } else {
          str = number.toString();
        } 
      } 
      return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(param1Object.getClass(), tag), str);
    }
  }
  
  protected class RepresentList implements Represent {
    public Node representData(Object param1Object) {
      return SafeRepresenter.this.representSequence(SafeRepresenter.this.getTag(param1Object.getClass(), Tag.SEQ), (List)param1Object, null);
    }
  }
  
  protected class RepresentIterator implements Represent {
    public Node representData(Object param1Object) {
      Iterator<Object> iterator = (Iterator)param1Object;
      return SafeRepresenter.this.representSequence(SafeRepresenter.this.getTag(param1Object.getClass(), Tag.SEQ), new SafeRepresenter.IteratorWrapper(iterator), null);
    }
  }
  
  private static class IteratorWrapper implements Iterable<Object> {
    private Iterator<Object> iter;
    
    public IteratorWrapper(Iterator<Object> param1Iterator) {
      this.iter = param1Iterator;
    }
    
    public Iterator<Object> iterator() {
      return this.iter;
    }
  }
  
  protected class RepresentArray implements Represent {
    public Node representData(Object param1Object) {
      Object[] arrayOfObject = (Object[])param1Object;
      List<?> list = Arrays.asList(arrayOfObject);
      return SafeRepresenter.this.representSequence(Tag.SEQ, list, null);
    }
  }
  
  protected class RepresentPrimitiveArray implements Represent {
    public Node representData(Object param1Object) {
      Class<?> clazz = param1Object.getClass().getComponentType();
      if (byte.class == clazz)
        return SafeRepresenter.this.representSequence(Tag.SEQ, asByteList(param1Object), null); 
      if (short.class == clazz)
        return SafeRepresenter.this.representSequence(Tag.SEQ, asShortList(param1Object), null); 
      if (int.class == clazz)
        return SafeRepresenter.this.representSequence(Tag.SEQ, asIntList(param1Object), null); 
      if (long.class == clazz)
        return SafeRepresenter.this.representSequence(Tag.SEQ, asLongList(param1Object), null); 
      if (float.class == clazz)
        return SafeRepresenter.this.representSequence(Tag.SEQ, asFloatList(param1Object), null); 
      if (double.class == clazz)
        return SafeRepresenter.this.representSequence(Tag.SEQ, asDoubleList(param1Object), null); 
      if (char.class == clazz)
        return SafeRepresenter.this.representSequence(Tag.SEQ, asCharList(param1Object), null); 
      if (boolean.class == clazz)
        return SafeRepresenter.this.representSequence(Tag.SEQ, asBooleanList(param1Object), null); 
      throw new YAMLException("Unexpected primitive '" + clazz.getCanonicalName() + "'");
    }
    
    private List<Byte> asByteList(Object param1Object) {
      byte[] arrayOfByte = (byte[])param1Object;
      ArrayList<Byte> arrayList = new ArrayList(arrayOfByte.length);
      for (byte b = 0; b < arrayOfByte.length; b++)
        arrayList.add(Byte.valueOf(arrayOfByte[b])); 
      return arrayList;
    }
    
    private List<Short> asShortList(Object param1Object) {
      short[] arrayOfShort = (short[])param1Object;
      ArrayList<Short> arrayList = new ArrayList(arrayOfShort.length);
      for (byte b = 0; b < arrayOfShort.length; b++)
        arrayList.add(Short.valueOf(arrayOfShort[b])); 
      return arrayList;
    }
    
    private List<Integer> asIntList(Object param1Object) {
      int[] arrayOfInt = (int[])param1Object;
      ArrayList<Integer> arrayList = new ArrayList(arrayOfInt.length);
      for (byte b = 0; b < arrayOfInt.length; b++)
        arrayList.add(Integer.valueOf(arrayOfInt[b])); 
      return arrayList;
    }
    
    private List<Long> asLongList(Object param1Object) {
      long[] arrayOfLong = (long[])param1Object;
      ArrayList<Long> arrayList = new ArrayList(arrayOfLong.length);
      for (byte b = 0; b < arrayOfLong.length; b++)
        arrayList.add(Long.valueOf(arrayOfLong[b])); 
      return arrayList;
    }
    
    private List<Float> asFloatList(Object param1Object) {
      float[] arrayOfFloat = (float[])param1Object;
      ArrayList<Float> arrayList = new ArrayList(arrayOfFloat.length);
      for (byte b = 0; b < arrayOfFloat.length; b++)
        arrayList.add(Float.valueOf(arrayOfFloat[b])); 
      return arrayList;
    }
    
    private List<Double> asDoubleList(Object param1Object) {
      double[] arrayOfDouble = (double[])param1Object;
      ArrayList<Double> arrayList = new ArrayList(arrayOfDouble.length);
      for (byte b = 0; b < arrayOfDouble.length; b++)
        arrayList.add(Double.valueOf(arrayOfDouble[b])); 
      return arrayList;
    }
    
    private List<Character> asCharList(Object param1Object) {
      char[] arrayOfChar = (char[])param1Object;
      ArrayList<Character> arrayList = new ArrayList(arrayOfChar.length);
      for (byte b = 0; b < arrayOfChar.length; b++)
        arrayList.add(Character.valueOf(arrayOfChar[b])); 
      return arrayList;
    }
    
    private List<Boolean> asBooleanList(Object param1Object) {
      boolean[] arrayOfBoolean = (boolean[])param1Object;
      ArrayList<Boolean> arrayList = new ArrayList(arrayOfBoolean.length);
      for (byte b = 0; b < arrayOfBoolean.length; b++)
        arrayList.add(Boolean.valueOf(arrayOfBoolean[b])); 
      return arrayList;
    }
  }
  
  protected class RepresentMap implements Represent {
    public Node representData(Object param1Object) {
      return SafeRepresenter.this.representMapping(SafeRepresenter.this.getTag(param1Object.getClass(), Tag.MAP), (Map<?, ?>)param1Object, null);
    }
  }
  
  protected class RepresentSet implements Represent {
    public Node representData(Object param1Object) {
      LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<Object, Object>();
      Set set = (Set)param1Object;
      for (Object object : set)
        linkedHashMap.put(object, null); 
      return SafeRepresenter.this.representMapping(SafeRepresenter.this.getTag(param1Object.getClass(), Tag.SET), linkedHashMap, null);
    }
  }
  
  protected class RepresentDate implements Represent {
    public Node representData(Object param1Object) {
      Calendar calendar;
      if (param1Object instanceof Calendar) {
        calendar = (Calendar)param1Object;
      } else {
        calendar = Calendar.getInstance((SafeRepresenter.this.getTimeZone() == null) ? TimeZone.getTimeZone("UTC") : SafeRepresenter.this.timeZone);
        calendar.setTime((Date)param1Object);
      } 
      int i = calendar.get(1);
      int j = calendar.get(2) + 1;
      int k = calendar.get(5);
      int m = calendar.get(11);
      int n = calendar.get(12);
      int i1 = calendar.get(13);
      int i2 = calendar.get(14);
      StringBuilder stringBuilder = new StringBuilder(String.valueOf(i));
      while (stringBuilder.length() < 4)
        stringBuilder.insert(0, "0"); 
      stringBuilder.append("-");
      if (j < 10)
        stringBuilder.append("0"); 
      stringBuilder.append(String.valueOf(j));
      stringBuilder.append("-");
      if (k < 10)
        stringBuilder.append("0"); 
      stringBuilder.append(String.valueOf(k));
      stringBuilder.append("T");
      if (m < 10)
        stringBuilder.append("0"); 
      stringBuilder.append(String.valueOf(m));
      stringBuilder.append(":");
      if (n < 10)
        stringBuilder.append("0"); 
      stringBuilder.append(String.valueOf(n));
      stringBuilder.append(":");
      if (i1 < 10)
        stringBuilder.append("0"); 
      stringBuilder.append(String.valueOf(i1));
      if (i2 > 0) {
        if (i2 < 10) {
          stringBuilder.append(".00");
        } else if (i2 < 100) {
          stringBuilder.append(".0");
        } else {
          stringBuilder.append(".");
        } 
        stringBuilder.append(String.valueOf(i2));
      } 
      int i3 = calendar.getTimeZone().getOffset(calendar.get(0), calendar
          .get(1), calendar.get(2), calendar
          .get(5), calendar.get(7), calendar
          .get(14));
      if (i3 == 0) {
        stringBuilder.append('Z');
      } else {
        if (i3 < 0) {
          stringBuilder.append('-');
          i3 *= -1;
        } else {
          stringBuilder.append('+');
        } 
        int i4 = i3 / 60000;
        int i5 = i4 / 60;
        int i6 = i4 % 60;
        if (i5 < 10)
          stringBuilder.append('0'); 
        stringBuilder.append(i5);
        stringBuilder.append(':');
        if (i6 < 10)
          stringBuilder.append('0'); 
        stringBuilder.append(i6);
      } 
      return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(param1Object.getClass(), Tag.TIMESTAMP), stringBuilder.toString(), null);
    }
  }
  
  protected class RepresentEnum implements Represent {
    public Node representData(Object param1Object) {
      Tag tag = new Tag(param1Object.getClass());
      return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(param1Object.getClass(), tag), ((Enum)param1Object).name());
    }
  }
  
  protected class RepresentByteArray implements Represent {
    public Node representData(Object param1Object) {
      char[] arrayOfChar = Base64Coder.encode((byte[])param1Object);
      return SafeRepresenter.this.representScalar(Tag.BINARY, String.valueOf(arrayOfChar), Character.valueOf('|'));
    }
  }
  
  public TimeZone getTimeZone() {
    return this.timeZone;
  }
  
  public void setTimeZone(TimeZone paramTimeZone) {
    this.timeZone = paramTimeZone;
  }
  
  protected class RepresentUuid implements Represent {
    public Node representData(Object param1Object) {
      return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(param1Object.getClass(), new Tag(UUID.class)), param1Object.toString());
    }
  }
}
