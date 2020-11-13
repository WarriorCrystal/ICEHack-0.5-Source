//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class RenderUtil {
  public static void enableDefaults() {
    GL11.glDisable(3042);
    GL11.glEnable(3553);
    GL11.glEnable(2896);
  }
  
  public static double getAlphaFromHex(int paramInt) {
    return ((paramInt >> 24 & 0xFF) / 255.0F);
  }
  
  public static void drawRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt, float paramFloat5) {
    float f1 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f3 = (paramInt & 0xFF) / 255.0F;
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(paramFloat1, paramFloat4, 0.0D).color(f1, f2, f3, paramFloat5).endVertex();
    bufferBuilder.pos(paramFloat3, paramFloat4, 0.0D).color(f1, f2, f3, paramFloat5).endVertex();
    bufferBuilder.pos(paramFloat3, paramFloat2, 0.0D).color(f1, f2, f3, paramFloat5).endVertex();
    bufferBuilder.pos(paramFloat1, paramFloat2, 0.0D).color(f1, f2, f3, paramFloat5).endVertex();
    tessellator.draw();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static void drawTexture(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(4, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos((paramFloat1 + paramFloat3), paramFloat2, 0.0D).tex(paramFloat7, paramFloat6).endVertex();
    bufferBuilder.pos(paramFloat1, paramFloat2, 0.0D).tex(paramFloat5, paramFloat6).endVertex();
    bufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat4), 0.0D).tex(paramFloat5, paramFloat8).endVertex();
    bufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat4), 0.0D).tex(paramFloat5, paramFloat8).endVertex();
    bufferBuilder.pos((paramFloat1 + paramFloat3), (paramFloat2 + paramFloat4), 0.0D).tex(paramFloat7, paramFloat8).endVertex();
    bufferBuilder.pos((paramFloat1 + paramFloat3), paramFloat2, 0.0D).tex(paramFloat7, paramFloat6).endVertex();
    tessellator.draw();
  }
  
  public static void drawColorBox(AxisAlignedBB paramAxisAlignedBB, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    tessellator.draw();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    tessellator.draw();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    tessellator.draw();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    tessellator.draw();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    tessellator.draw();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat1, paramFloat2, paramFloat3, paramFloat4).endVertex();
    tessellator.draw();
  }
  
  public static void drawTriangle(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt) {
    GL11.glTranslated(paramFloat1, paramFloat2, 0.0D);
    GL11.glRotatef(180.0F + paramFloat4, 0.0F, 0.0F, 1.0F);
    float f1 = (paramInt >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt & 0xFF) / 255.0F;
    GL11.glColor4f(f2, f3, f4, f1);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glBlendFunc(770, 771);
    GL11.glLineWidth(1.0F);
    GL11.glBegin(6);
    GL11.glVertex2d(0.0D, (1.0F * paramFloat3));
    GL11.glVertex2d((1.0F * paramFloat3), -(1.0F * paramFloat3));
    GL11.glVertex2d(-(1.0F * paramFloat3), -(1.0F * paramFloat3));
    GL11.glEnd();
    GL11.glDisable(2848);
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glRotatef(-180.0F - paramFloat4, 0.0F, 0.0F, 1.0F);
    GL11.glTranslated(-paramFloat1, -paramFloat2, 0.0D);
  }
  
  public static void drawRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt) {
    float f1 = (paramInt >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt & 0xFF) / 255.0F;
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(paramFloat1, paramFloat4, 0.0D).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramFloat3, paramFloat4, 0.0D).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramFloat3, paramFloat2, 0.0D).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramFloat1, paramFloat2, 0.0D).color(f2, f3, f4, f1).endVertex();
    tessellator.draw();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static void drawLine3D(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, int paramInt) {
    float f1 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f3 = (paramInt & 0xFF) / 255.0F;
    float f4 = (paramInt >> 24 & 0xFF) / 255.0F;
    GlStateManager.pushMatrix();
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.shadeModel(7425);
    GL11.glLineWidth(paramFloat7);
    GL11.glEnable(2848);
    GL11.glHint(3154, 4354);
    GlStateManager.disableDepth();
    GL11.glEnable(34383);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(paramFloat1, paramFloat2, paramFloat3).color(f1, f2, f3, f4).endVertex();
    bufferBuilder.pos(paramFloat4, paramFloat5, paramFloat6).color(f1, f2, f3, f4).endVertex();
    tessellator.draw();
    GlStateManager.shadeModel(7424);
    GL11.glDisable(2848);
    GlStateManager.enableDepth();
    GL11.glDisable(34383);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
    GlStateManager.popMatrix();
  }
  
  public static void DrawPolygon(double paramDouble1, double paramDouble2, int paramInt1, int paramInt2, int paramInt3) {
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(6, DefaultVertexFormats.POSITION);
    float f1 = (paramInt3 >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt3 >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt3 >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt3 & 0xFF) / 255.0F;
    bufferBuilder.pos(paramDouble1, paramDouble2, 0.0D).endVertex();
    double d = 6.283185307179586D;
    for (byte b = 0; b <= paramInt2; b++) {
      double d1 = 6.283185307179586D * b / paramInt2 + Math.toRadians(180.0D);
      bufferBuilder.pos(paramDouble1 + Math.sin(d1) * paramInt1, paramDouble2 + Math.cos(d1) * paramInt1, 0.0D).endVertex();
    } 
    tessellator.draw();
    GL11.glEnable(3553);
    GL11.glDisable(3042);
  }
  
  public static int getScreenWidth() {
    IntBuffer intBuffer = BufferUtils.createIntBuffer(16);
    GL11.glGetInteger(2978, intBuffer);
    return Math.round(intBuffer.get(2));
  }
  
  public static void drawCircle(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    setupOverlayRendering();
    disableDefaults();
    GL11.glColor4d(getRedFromHex(paramInt4), getGreenFromHex(paramInt4), getBlueFromHex(paramInt4), getAlphaFromHex(paramInt4));
    GL11.glBegin(9);
    for (byte b = 0; b <= 'Ũ'; b++)
      GL11.glVertex2d(paramInt1 + Math.sin(b * 3.141526D / 180.0D) * paramInt3, paramInt2 + Math.cos(b * 3.141526D / 180.0D) * paramInt3); 
    GL11.glEnd();
    enableDefaults();
  }
  
  public static void glScissor(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, ScaledResolution paramScaledResolution) {
    GL11.glScissor((int)(paramFloat1 * paramScaledResolution.getScaleFactor()), (int)((Minecraft.getMinecraft()).displayHeight - paramFloat4 * paramScaledResolution.getScaleFactor()), (int)((paramFloat3 - paramFloat1) * paramScaledResolution.getScaleFactor()), (int)((paramFloat4 - paramFloat2) * paramScaledResolution.getScaleFactor()));
  }
  
  public static void drawQuarterCircle(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    disableDefaults();
    GL11.glColor4d(getRedFromHex(paramInt5), getGreenFromHex(paramInt5), getBlueFromHex(paramInt5), (paramInt6 > 0) ? paramInt6 : getAlphaFromHex(paramInt5));
    GL11.glBegin(9);
    GL11.glVertex2d(paramInt1, paramInt2);
    if (paramInt4 == 0) {
      for (byte b = 0; b <= 90; b++)
        GL11.glVertex2d(paramInt1 + Math.sin(b * 3.141526D / 180.0D) * (paramInt3 * -1), paramInt2 + Math.cos(b * 3.141526D / 180.0D) * (paramInt3 * -1)); 
    } else if (paramInt4 == 1) {
      for (byte b = 90; b <= '´'; b++)
        GL11.glVertex2d(paramInt1 + Math.sin(b * 3.141526D / 180.0D) * paramInt3, paramInt2 + Math.cos(b * 3.141526D / 180.0D) * paramInt3); 
    } else if (paramInt4 == 2) {
      for (byte b = 90; b <= '´'; b++)
        GL11.glVertex2d(paramInt1 + Math.sin(b * 3.141526D / 180.0D) * (paramInt3 * -1), paramInt2 + Math.cos(b * 3.141526D / 180.0D) * (paramInt3 * -1)); 
    } else if (paramInt4 == 3) {
      for (byte b = 0; b <= 90; b++)
        GL11.glVertex2d(paramInt1 + Math.sin(b * 3.141526D / 180.0D) * paramInt3, paramInt2 + Math.cos(b * 3.141526D / 180.0D) * paramInt3); 
    } 
    GL11.glEnd();
    enableDefaults();
  }
  
  public static void drawRoundedRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
    disableDefaults();
    float f1 = Math.abs(paramInt1 + paramInt5);
    float f2 = Math.abs(paramInt2 + paramInt5);
    float f3 = Math.abs(paramInt3 - paramInt5);
    float f4 = Math.abs(paramInt4 - paramInt5);
    drawRect(f1, f2, f3, f4, paramInt6);
    drawRect(paramInt1, f2, f1, f4, paramInt6);
    drawRect(f3, f2, paramInt3, f4, paramInt6);
    drawRect(f1, paramInt2, f3, f2, paramInt6);
    drawRect(f1, f4, f3, paramInt4, paramInt6);
    drawQuarterCircle((int)f1, (int)f2, paramInt5, 0, paramInt6, paramInt7);
    drawQuarterCircle((int)f3, (int)f2, paramInt5, 1, paramInt6, paramInt7);
    drawQuarterCircle((int)f1, (int)f4, paramInt5, 2, paramInt6, paramInt7);
    drawQuarterCircle((int)f3, (int)f4, paramInt5, 3, paramInt6, paramInt7);
    enableDefaults();
  }
  
  public static void glBillboardDistanceScaled(float paramFloat1, float paramFloat2, float paramFloat3, EntityPlayer paramEntityPlayer, float paramFloat4) {
    glBillboard(paramFloat1, paramFloat2, paramFloat3);
    int i = (int)paramEntityPlayer.getDistance(paramFloat1, paramFloat2, paramFloat3);
    float f = i / 2.0F / (2.0F + 2.0F - paramFloat4);
    if (f < 1.0F)
      f = 1.0F; 
    GlStateManager.scale(f, f, f);
  }
  
  public static void drawPlane(AxisAlignedBB paramAxisAlignedBB, float paramFloat, int paramInt) {
    GlStateManager.pushMatrix();
    GlStateManager.glLineWidth(paramFloat);
    GlStateManager.enableBlend();
    GlStateManager.disableDepth();
    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
    GlStateManager.disableTexture2D();
    GlStateManager.depthMask(false);
    GL11.glEnable(2848);
    GL11.glHint(3154, 4354);
    drawPlane(paramAxisAlignedBB, paramInt);
    GL11.glDisable(2848);
    GlStateManager.depthMask(true);
    GlStateManager.enableDepth();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
  }
  
  public static void updateModelViewProjectionMatrix() {
    GL11.glGetFloat(2982, MODELVIEW);
    GL11.glGetFloat(2983, PROJECTION);
    GL11.glGetInteger(2978, VIEWPORT);
    ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
    ProjUtil.getInstance().updateMatrices(VIEWPORT, MODELVIEW, PROJECTION, (scaledResolution.getScaledWidth() / (Minecraft.getMinecraft()).displayWidth), (scaledResolution.getScaledHeight() / (Minecraft.getMinecraft()).displayHeight));
  }
  
  public static String trimStringToWidth(String paramString, int paramInt, boolean paramBoolean) {
    return (Minecraft.getMinecraft()).fontRenderer.trimStringToWidth(paramString, paramInt, paramBoolean);
  }
  
  public static void drawBorderedRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float paramFloat, int paramInt6) {
    drawRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    setupOverlayRendering();
    disableDefaults();
    GL11.glColor4d(getRedFromHex(paramInt6), getGreenFromHex(paramInt6), getBlueFromHex(paramInt6), getAlphaFromHex(paramInt6));
    GL11.glLineWidth(paramFloat);
    GL11.glBegin(1);
    GL11.glVertex2d(paramInt1, paramInt2);
    GL11.glVertex2d(paramInt1, paramInt4);
    GL11.glVertex2d(paramInt3, paramInt4);
    GL11.glVertex2d(paramInt3, paramInt2);
    GL11.glVertex2d(paramInt1, paramInt2);
    GL11.glVertex2d(paramInt3, paramInt2);
    GL11.glVertex2d(paramInt1, paramInt4);
    GL11.glVertex2d(paramInt3, paramInt4);
    GL11.glEnd();
    enableDefaults();
  }
  
  public static void disableLighting() {
    GL11.glDisable(2896);
  }
  
  public static void setupGradient() {
    GL11.glDisable(3553);
    GL11.glEnable(3042);
    GL11.glDisable(3008);
    GL11.glShadeModel(7425);
  }
  
  public static int getScreenHeight() {
    IntBuffer intBuffer = BufferUtils.createIntBuffer(16);
    GL11.glGetInteger(2978, intBuffer);
    return Math.round(intBuffer.get(3));
  }
  
  public static void drawLine(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, int paramInt) {
    float f1 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f3 = (paramInt & 0xFF) / 255.0F;
    float f4 = (paramInt >> 24 & 0xFF) / 255.0F;
    GlStateManager.pushMatrix();
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.shadeModel(7425);
    GL11.glLineWidth(paramFloat5);
    GL11.glEnable(2848);
    GL11.glHint(3154, 4354);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(paramFloat1, paramFloat2, 0.0D).color(f1, f2, f3, f4).endVertex();
    bufferBuilder.pos(paramFloat3, paramFloat4, 0.0D).color(f1, f2, f3, f4).endVertex();
    tessellator.draw();
    GlStateManager.shadeModel(7424);
    GL11.glDisable(2848);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
    GlStateManager.popMatrix();
  }
  
  public static double getBlueFromHex(int paramInt) {
    return ((paramInt & 0xFF) / 255.0F);
  }
  
  public static double getGreenFromHex(int paramInt) {
    return ((paramInt >> 8 & 0xFF) / 255.0F);
  }
  
  public static void drawBorderedCircle(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat, int paramInt5) {
    drawCircle(paramInt1, paramInt2, paramInt3, paramInt4);
    drawUnfilledCircle(paramInt1, paramInt2, paramInt3, paramFloat, paramInt5);
  }
  
  public static void drawBoundingBox(AxisAlignedBB paramAxisAlignedBB, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5) {
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    GlStateManager.disableDepth();
    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
    GlStateManager.disableTexture2D();
    GlStateManager.depthMask(false);
    GL11.glEnable(2848);
    GL11.glHint(3154, 4354);
    GL11.glLineWidth(paramFloat1);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat2, paramFloat3, paramFloat4, 0.0F).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat2, paramFloat3, paramFloat4, paramFloat5).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat2, paramFloat3, paramFloat4, paramFloat5).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat2, paramFloat3, paramFloat4, paramFloat5).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat2, paramFloat3, paramFloat4, paramFloat5).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat2, paramFloat3, paramFloat4, paramFloat5).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat2, paramFloat3, paramFloat4, paramFloat5).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat2, paramFloat3, paramFloat4, paramFloat5).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat2, paramFloat3, paramFloat4, paramFloat5).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat2, paramFloat3, paramFloat4, paramFloat5).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat2, paramFloat3, paramFloat4, paramFloat5).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat2, paramFloat3, paramFloat4, 0.0F).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat2, paramFloat3, paramFloat4, paramFloat5).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramFloat2, paramFloat3, paramFloat4, 0.0F).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramFloat2, paramFloat3, paramFloat4, paramFloat5).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramFloat2, paramFloat3, paramFloat4, 0.0F).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat2, paramFloat3, paramFloat4, paramFloat5).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramFloat2, paramFloat3, paramFloat4, 0.0F).endVertex();
    tessellator.draw();
    GL11.glDisable(2848);
    GlStateManager.depthMask(true);
    GlStateManager.enableDepth();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
  }
  
  public static void drawSplitString(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    (Minecraft.getMinecraft()).fontRenderer.drawSplitString(paramString, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static void drawTexture(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) {
    float f1 = 0.00390625F;
    float f2 = 0.00390625F;
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat6), 0.0D).tex((paramFloat3 * f1), ((paramFloat4 + paramFloat6) * f2)).endVertex();
    bufferBuilder.pos((paramFloat1 + paramFloat5), (paramFloat2 + paramFloat6), 0.0D).tex(((paramFloat3 + paramFloat5) * f1), ((paramFloat4 + paramFloat6) * f2)).endVertex();
    bufferBuilder.pos((paramFloat1 + paramFloat5), paramFloat2, 0.0D).tex(((paramFloat3 + paramFloat5) * f1), (paramFloat4 * f2)).endVertex();
    bufferBuilder.pos(paramFloat1, paramFloat2, 0.0D).tex((paramFloat3 * f1), (paramFloat4 * f2)).endVertex();
    tessellator.draw();
  }
  
  public static void glBillboard(float paramFloat1, float paramFloat2, float paramFloat3) {
    float f = 0.02666667F;
    GlStateManager.translate(paramFloat1 - (Minecraft.getMinecraft().getRenderManager()).renderPosX, paramFloat2 - (Minecraft.getMinecraft().getRenderManager()).renderPosY, paramFloat3 - (Minecraft.getMinecraft().getRenderManager()).renderPosZ);
    GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(-(Minecraft.getMinecraft()).player.rotationYaw, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate((Minecraft.getMinecraft()).player.rotationPitch, ((Minecraft.getMinecraft()).gameSettings.thirdPersonView == 2) ? -1.0F : 1.0F, 0.0F, 0.0F);
    GlStateManager.scale(-f, -f, f);
  }
  
  public static double getRedFromHex(int paramInt) {
    return ((paramInt >> 16 & 0xFF) / 255.0F);
  }
  
  public static void drawColorBox(AxisAlignedBB paramAxisAlignedBB, Color paramColor) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    tessellator.draw();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    tessellator.draw();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    tessellator.draw();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    tessellator.draw();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    tessellator.draw();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramColor.getRed(), paramColor.getGreen(), paramColor.getBlue(), paramColor.getAlpha()).endVertex();
    tessellator.draw();
  }
  
  public static void drawPlane(AxisAlignedBB paramAxisAlignedBB, int paramInt) {
    float f1 = (paramInt >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt & 0xFF) / 255.0F;
    double d1 = paramAxisAlignedBB.minX;
    double d2 = paramAxisAlignedBB.minY;
    double d3 = paramAxisAlignedBB.minZ;
    double d4 = paramAxisAlignedBB.maxX;
    double d5 = paramAxisAlignedBB.maxY;
    double d6 = paramAxisAlignedBB.maxZ;
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(d1, d2, d3).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(d4, d2, d6).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(d1, d2, d6).color(f2, f3, f4, 0.0F).endVertex();
    bufferBuilder.pos(d6, d2, d3).color(f2, f3, f4, f1).endVertex();
    tessellator.draw();
  }
  
  public static void drawUnfilledCircle(int paramInt1, int paramInt2, int paramInt3, float paramFloat, int paramInt4) {
    setupOverlayRendering();
    disableDefaults();
    GL11.glColor4d(getRedFromHex(paramInt4), getGreenFromHex(paramInt4), getBlueFromHex(paramInt4), getAlphaFromHex(paramInt4));
    GL11.glLineWidth(paramFloat);
    GL11.glEnable(2848);
    GL11.glBegin(2);
    for (byte b = 0; b <= 'Ũ'; b++)
      GL11.glVertex2d(paramInt1 + Math.sin(b * 3.141526D / 180.0D) * paramInt3, paramInt2 + Math.cos(b * 3.141526D / 180.0D) * paramInt3); 
    GL11.glEnd();
    GL11.glDisable(2848);
    enableDefaults();
  }
  
  public static void drawBoundingBox(AxisAlignedBB paramAxisAlignedBB, float paramFloat, int paramInt) {
    float f1 = (paramInt >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt & 0xFF) / 255.0F;
    drawBoundingBox(paramAxisAlignedBB, paramFloat, f2, f3, f4, f1);
  }
  
  public static void drawGradientRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2) {
    float f1 = (paramInt1 >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt1 >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt1 >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt1 & 0xFF) / 255.0F;
    float f5 = (paramInt2 >> 24 & 0xFF) / 255.0F;
    float f6 = (paramInt2 >> 16 & 0xFF) / 255.0F;
    float f7 = (paramInt2 >> 8 & 0xFF) / 255.0F;
    float f8 = (paramInt2 & 0xFF) / 255.0F;
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    GlStateManager.shadeModel(7425);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(paramFloat3, paramFloat2, 0.0D).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramFloat1, paramFloat2, 0.0D).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramFloat1, paramFloat4, 0.0D).color(f6, f7, f8, f5).endVertex();
    bufferBuilder.pos(paramFloat3, paramFloat4, 0.0D).color(f6, f7, f8, f5).endVertex();
    tessellator.draw();
    GlStateManager.shadeModel(7424);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
  }
  
  public static void setupOverlayRendering() {
    GL11.glClear(256);
    GL11.glMatrixMode(5889);
    GL11.glLoadIdentity();
    GL11.glOrtho(0.0D, getScreenWidth(), getScreenHeight(), 0.0D, 1000.0D, 3000.0D);
    GL11.glMatrixMode(5888);
    GL11.glLoadIdentity();
    GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
  }
  
  public static void drawLine2D(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float paramFloat) {
    setupOverlayRendering();
    disableDefaults();
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(2848);
    GL11.glColor4d(getRedFromHex(paramInt5), getGreenFromHex(paramInt5), getBlueFromHex(paramInt5), getAlphaFromHex(paramInt5));
    GL11.glBegin(1);
    GL11.glVertex2i(paramInt1, paramInt2);
    GL11.glVertex2i(paramInt3, paramInt4);
    GL11.glEnd();
    GL11.glDisable(2848);
    enableDefaults();
  }
  
  public static void drawPlane(double paramDouble1, double paramDouble2, double paramDouble3, AxisAlignedBB paramAxisAlignedBB, float paramFloat, int paramInt) {
    GL11.glPushMatrix();
    GL11.glTranslated(paramDouble1, paramDouble2, paramDouble3);
    drawPlane(paramAxisAlignedBB, paramFloat, paramInt);
    GL11.glPopMatrix();
  }
  
  public static final void DrawNodusBetterRect(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt1, int paramInt2) {
    GL11.glEnable(3042);
    GL11.glEnable(2848);
    drawRect((int)paramDouble1, (int)paramDouble2, (int)paramDouble3, (int)paramDouble4, paramInt2);
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    drawRect(((int)paramDouble1 * 2 - 1), ((int)paramDouble2 * 2), ((int)paramDouble1 * 2), ((int)paramDouble4 * 2 - 1), paramInt1);
    drawRect(((int)paramDouble1 * 2), ((int)paramDouble2 * 2 - 1), ((int)paramDouble3 * 2), ((int)paramDouble2 * 2), paramInt1);
    drawRect(((int)paramDouble3 * 2), ((int)paramDouble2 * 2), ((int)paramDouble3 * 2 + 1), ((int)paramDouble4 * 2 - 1), paramInt1);
    drawRect(((int)paramDouble1 * 2), ((int)paramDouble4 * 2 - 1), ((int)paramDouble3 * 2), ((int)paramDouble4 * 2), paramInt1);
    GL11.glDisable(3042);
    GL11.glScalef(2.0F, 2.0F, 2.0F);
  }
  
  public static final void DrawNodusRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt) {
    if (paramFloat1 < paramFloat3) {
      float f = paramFloat1;
      paramFloat1 = paramFloat3;
      paramFloat3 = f;
    } 
    if (paramFloat2 < paramFloat4) {
      float f = paramFloat2;
      paramFloat2 = paramFloat4;
      paramFloat4 = f;
    } 
    float f1 = (paramInt >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt & 0xFF) / 255.0F;
    Tessellator tessellator = Tessellator.getInstance();
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glDisable(2896);
    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    GL11.glColor4f(f2, f3, f4, f1);
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(paramFloat1, paramFloat4, 0.0D).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramFloat3, paramFloat4, 0.0D).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramFloat3, paramFloat2, 0.0D).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramFloat1, paramFloat2, 0.0D).color(f2, f3, f4, f1).endVertex();
    tessellator.draw();
    GL11.glEnable(3553);
    GL11.glDisable(3042);
  }
  
  public static void drawFilledBox(AxisAlignedBB paramAxisAlignedBB, int paramInt) {
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    GlStateManager.disableDepth();
    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
    GlStateManager.disableTexture2D();
    GlStateManager.depthMask(false);
    float f1 = (paramInt >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt & 0xFF) / 255.0F;
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(f2, f3, f4, f1).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(f2, f3, f4, f1).endVertex();
    tessellator.draw();
    GlStateManager.depthMask(true);
    GlStateManager.enableDepth();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
  }
  
  public static void drawOutlineRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, int paramInt) {
    drawRect(paramFloat1, paramFloat2, paramFloat1 - paramFloat5, paramFloat4, paramInt);
    drawRect(paramFloat3 + paramFloat5, paramFloat2, paramFloat3, paramFloat4, paramInt);
    drawRect(paramFloat1, paramFloat2, paramFloat3, paramFloat2 - paramFloat5, paramInt);
    drawRect(paramFloat1, paramFloat4 + paramFloat5, paramFloat3, paramFloat4, paramInt);
  }
  
  public static void disableDefaults() {
    GL11.glEnable(3042);
    GL11.glDisable(2896);
    GL11.glDisable(3553);
  }
  
  public static void unsetupGradient() {
    GL11.glShadeModel(7424);
    GL11.glDisable(3042);
    GL11.glEnable(3008);
    GL11.glEnable(3553);
  }
  
  static {
    MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
    PROJECTION = GLAllocation.createDirectFloatBuffer(16);
  }
  
  public static void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    setupOverlayRendering();
    disableDefaults();
    GL11.glColor4d(getRedFromHex(paramInt5), getGreenFromHex(paramInt5), getBlueFromHex(paramInt5), (paramInt6 > 0) ? paramInt6 : getAlphaFromHex(paramInt5));
    GL11.glBegin(7);
    GL11.glVertex2i(paramInt3, paramInt2);
    GL11.glVertex2i(paramInt1, paramInt2);
    GL11.glVertex2i(paramInt1, paramInt4);
    GL11.glVertex2i(paramInt3, paramInt4);
    GL11.glEnd();
    enableDefaults();
  }
  
  public static String trimStringToWidth(String paramString, int paramInt) {
    return (Minecraft.getMinecraft()).fontRenderer.trimStringToWidth(paramString, paramInt);
  }
}
