//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.render;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;

public class BlurGui extends Module {
  public void onDisable() {
    disableBlur();
  }
  
  public void onUpdate() {
    if (mc.currentScreen == null)
      disableBlur(); 
  }
  
  public static void enableBlur() {
    if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof net.minecraft.entity.player.EntityPlayer) {
      if (mc.entityRenderer.getShaderGroup() != null)
        mc.entityRenderer.getShaderGroup().deleteShaderGroup(); 
      mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
    } 
  }
  
  public BlurGui() {
    super("BlurGui", 0, Category.RENDER);
  }
  
  public static void disableBlur() {
    if (mc.entityRenderer.getShaderGroup() != null) {
      mc.entityRenderer.getShaderGroup().deleteShaderGroup();
      mc.entityRenderer.shaderGroup = null;
    } 
  }
  
  public void onEnable() {
    if (mc.currentScreen == ICEHack.clickgui)
      enableBlur(); 
  }
}
