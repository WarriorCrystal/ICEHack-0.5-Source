//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import net.minecraft.client.Minecraft;

public class UIUtil {
  public static int bottomY() {
    return mc.displayWidth / 2;
  }
  
  public static int scaleX(ScreenPos paramScreenPos, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = 0;
    switch (paramScreenPos) {
      case TOP_LEFT:
        i = (0 + paramInt2) / paramInt4;
        break;
      case TOP_RIGHT:
        i = (mc.displayWidth / 2 - paramInt1 * paramInt4 - paramInt2) / paramInt4;
        break;
      case BOTTOM_LEFT:
        i = (0 + paramInt2) / paramInt4;
        break;
      case BOTTOM_RIGHT:
        i = (mc.displayWidth / 2 - paramInt1 * paramInt4 - paramInt2) / paramInt4;
        break;
    } 
    return i;
  }
  
  public static int scaleY(ScreenPos paramScreenPos, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = 0;
    switch (paramScreenPos) {
      case TOP_LEFT:
        i = (0 + paramInt3) / paramInt4;
        break;
      case TOP_RIGHT:
        i = (0 + paramInt3) / paramInt4;
        break;
      case BOTTOM_LEFT:
        i = (mc.displayHeight / 2 - paramInt1 * paramInt4 - paramInt3) / paramInt4;
        break;
      case BOTTOM_RIGHT:
        i = (mc.displayHeight / 2 - paramInt1 * paramInt4 - paramInt3) / paramInt4;
        break;
    } 
    return i;
  }
  
  public enum ScreenPos {
    TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT;
    
    static {
      $VALUES = new ScreenPos[] { TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT };
    }
  }
}
