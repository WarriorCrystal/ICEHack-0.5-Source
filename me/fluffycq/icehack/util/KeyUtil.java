package me.fluffycq.icehack.util;

import java.lang.reflect.Field;
import org.lwjgl.input.Keyboard;

public class KeyUtil {
  public static int getKeyCode(String paramString) {
    Class<Keyboard> clazz = Keyboard.class;
    int i = -69;
    try {
      Field field = clazz.getField(paramString);
      i = field.getInt(null);
    } catch (IllegalAccessException illegalAccessException) {
      illegalAccessException.printStackTrace();
    } catch (NoSuchFieldException noSuchFieldException) {
      noSuchFieldException.printStackTrace();
    } 
    return i;
  }
  
  static {
  
  }
}
