//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

public class Outline {
  public static void setColor(Color paramColor) {
    GL11.glColor4d((paramColor.getRed() / 255.0F), (paramColor.getGreen() / 255.0F), (paramColor.getBlue() / 255.0F), (paramColor.getAlpha() / 255.0F));
  }
  
  public static void renderTwo() {
    GL11.glStencilFunc(512, 0, 15);
    GL11.glStencilOp(7681, 7681, 7681);
    GL11.glPolygonMode(1032, 6914);
  }
  
  public static void renderOne(float paramFloat) {
    checkSetupFBO();
    GL11.glPushAttrib(1048575);
    GL11.glDisable(3008);
    GL11.glDisable(3553);
    GL11.glDisable(2896);
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glLineWidth(paramFloat);
    GL11.glEnable(2848);
    GL11.glEnable(2960);
    GL11.glClear(1024);
    GL11.glClearStencil(15);
    GL11.glStencilFunc(512, 1, 15);
    GL11.glStencilOp(7681, 7681, 7681);
    GL11.glPolygonMode(1032, 6913);
  }
  
  public static void renderFive() {
    GL11.glPolygonOffset(1.0F, 2000000.0F);
    GL11.glDisable(10754);
    GL11.glEnable(2929);
    GL11.glDepthMask(true);
    GL11.glDisable(2960);
    GL11.glDisable(2848);
    GL11.glHint(3154, 4352);
    GL11.glEnable(3042);
    GL11.glEnable(2896);
    GL11.glEnable(3553);
    GL11.glEnable(3008);
    GL11.glPopAttrib();
  }
  
  static {
  
  }
  
  private static void setupFBO(Framebuffer paramFramebuffer) {
    EXTFramebufferObject.glDeleteRenderbuffersEXT(paramFramebuffer.depthBuffer);
    int i = EXTFramebufferObject.glGenRenderbuffersEXT();
    EXTFramebufferObject.glBindRenderbufferEXT(36161, i);
    EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, (Minecraft.getMinecraft()).displayWidth, (Minecraft.getMinecraft()).displayHeight);
    EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, i);
    EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, i);
  }
  
  public static void renderFour(Color paramColor) {
    setColor(paramColor);
    GL11.glDepthMask(false);
    GL11.glDisable(2929);
    GL11.glEnable(10754);
    GL11.glPolygonOffset(1.0F, -2000000.0F);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
  }
  
  public static void checkSetupFBO() {
    Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
    if (framebuffer != null && framebuffer.depthBuffer > -1) {
      setupFBO(framebuffer);
      framebuffer.depthBuffer = -1;
    } 
  }
  
  public static void renderThree() {
    GL11.glStencilFunc(514, 1, 15);
    GL11.glStencilOp(7680, 7680, 7680);
    GL11.glPolygonMode(1032, 6913);
  }
}
