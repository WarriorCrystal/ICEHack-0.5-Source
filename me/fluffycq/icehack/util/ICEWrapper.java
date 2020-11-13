//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class ICEWrapper {
  public static Minecraft getMinecraft() {
    return Minecraft.getMinecraft();
  }
  
  static {
  
  }
  
  public static int getKey(String paramString) {
    return Keyboard.getKeyIndex(paramString.toUpperCase());
  }
  
  public static World getWorld() {
    return (World)(getMinecraft()).world;
  }
  
  public static EntityPlayerSP getPlayer() {
    return (getMinecraft()).player;
  }
}
