//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import java.awt.Rectangle;
import javax.vecmath.Vector2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class RenderingMethods {
  public static void drawBorderedRectReliantGui(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, float paramFloat, int paramInt1, int paramInt2) {
    enableGL2D();
    fakeGuiRect(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramInt1);
    glColor(paramInt2);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glLineWidth(paramFloat);
    GL11.glBegin(3);
    GL11.glVertex2d(paramDouble1, paramDouble2);
    GL11.glVertex2d(paramDouble1, paramDouble4);
    GL11.glVertex2d(paramDouble3, paramDouble4 + 0.5D);
    GL11.glVertex2d(paramDouble3, paramDouble2);
    GL11.glVertex2d(paramDouble1, paramDouble2);
    GL11.glEnd();
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    disableGL2D();
  }
  
  public static void enableGL3D(float paramFloat) {
    GL11.glDisable(3008);
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(3553);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    GL11.glEnable(2884);
    GL11.glEnable(2848);
    GL11.glHint(3154, 4354);
    GL11.glHint(3155, 4354);
    GL11.glLineWidth(paramFloat);
  }
  
  public static void drawRectDoublePlayerESP(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt) {
    float f1 = (paramInt >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt & 0xFF) / 255.0F;
    GL11.glBegin(7);
    GL11.glVertex2d(paramDouble1, paramDouble4);
    GL11.glVertex2d(paramDouble3, paramDouble4);
    GL11.glVertex2d(paramDouble3, paramDouble2);
    GL11.glVertex2d(paramDouble1, paramDouble2);
    GL11.glEnd();
  }
  
  public static void drawBorderedRectReliant(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, int paramInt1, int paramInt2) {
    enableGL2D();
    drawGuiRect(paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramInt1);
    glColor(paramInt2);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glLineWidth(paramFloat5);
    GL11.glBegin(3);
    GL11.glVertex2f(paramFloat1, paramFloat2);
    GL11.glVertex2f(paramFloat1, paramFloat4);
    GL11.glVertex2f(paramFloat3, paramFloat4);
    GL11.glVertex2f(paramFloat3, paramFloat2);
    GL11.glVertex2f(paramFloat1, paramFloat2);
    GL11.glEnd();
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    disableGL2D();
  }
  
  public static void drawRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt) {
    enableGL2D();
    glColor(paramInt);
    drawRect(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    disableGL2D();
  }
  
  public static void drawRectDouble(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    GL11.glBegin(7);
    GL11.glVertex2d(paramDouble1, paramDouble4);
    GL11.glVertex2d(paramDouble3, paramDouble4);
    GL11.glVertex2d(paramDouble3, paramDouble2);
    GL11.glVertex2d(paramDouble1, paramDouble2);
    GL11.glEnd();
  }
  
  public static void drawBorderedRectGui(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat, int paramInt5, int paramInt6) {
    enableGL2D();
    Gui.drawRect(paramInt1 + (int)paramFloat, paramInt2 + (int)paramFloat, paramInt3 - (int)paramFloat, paramInt4 - (int)paramFloat, paramInt5);
    Gui.drawRect(paramInt1 + (int)paramFloat - 1, paramInt2, paramInt3 - (int)paramFloat + 1, paramInt2 + (int)paramFloat, paramInt6);
    Gui.drawRect(paramInt1, paramInt2, paramInt1 + (int)paramFloat, paramInt4, paramInt6);
    Gui.drawRect(paramInt3 - (int)paramFloat, paramInt2, paramInt3, paramInt4, paramInt6);
    Gui.drawRect(paramInt1 + (int)paramFloat - 1, paramInt4 - (int)paramFloat, paramInt3 - (int)paramFloat + 1, paramInt4, paramInt6);
    disableGL2D();
  }
  
  public static void drawVLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt3 < paramInt2) {
      int i = paramInt2;
      paramInt2 = paramInt3;
      paramInt3 = i;
    } 
    Gui.drawRect(paramInt1, paramInt2 + 1, paramInt1 + 1, paramInt3, paramInt4);
  }
  
  public static void drawRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
    GL11.glBegin(7);
    GL11.glVertex2f(paramFloat1, paramFloat4);
    GL11.glVertex2f(paramFloat3, paramFloat4);
    GL11.glVertex2f(paramFloat3, paramFloat2);
    GL11.glVertex2f(paramFloat1, paramFloat2);
    GL11.glEnd();
  }
  
  public static void drawRectDoubleJavaColor(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt1, int paramInt2, int paramInt3) {
    enableGL2D();
    GL11.glColor3f(paramInt1, paramInt2, paramInt3);
    drawRectDouble(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    disableGL2D();
  }
  
  public static void drawHLine(float paramFloat1, float paramFloat2, float paramFloat3, int paramInt1, int paramInt2) {
    if (paramFloat2 < paramFloat1) {
      float f = paramFloat1;
      paramFloat1 = paramFloat2;
      paramFloat2 = f;
    } 
    drawGradientRect(paramFloat1, paramFloat3, (paramFloat2 + 1.0F), (paramFloat3 + 1.0F), paramInt1, paramInt2);
  }
  
  public static void drawHLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt2 < paramInt1) {
      int i = paramInt1;
      paramInt1 = paramInt2;
      paramInt2 = i;
    } 
    Gui.drawRect(paramInt1, paramInt3, paramInt2 + 1, paramInt3 + 1, paramInt4);
  }
  
  public static void wolfRamFilledCircle(float paramFloat1, float paramFloat2, float paramFloat3, int paramInt) {
    GL11.glEnable(3042);
    GL11.glDisable(2884);
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glBlendFunc(770, 771);
    GL11.glLineWidth(1.0F);
    float f1 = (paramInt >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt & 0xFF) / 255.0F;
    GL11.glColor4f(f2, f3, f4, (f1 == 0.0F) ? 1.0F : f1);
    int i = (int)Math.min(Math.max(paramFloat3, 45.0F), 360.0F);
    GL11.glBegin(9);
    for (byte b = 0; b < i; b++) {
      double d = 6.283185307179586D * b / i;
      GL11.glVertex2d(paramFloat1 + Math.sin(d) * paramFloat3, paramFloat2 + Math.cos(d) * paramFloat3);
    } 
    GL11.glEnd();
    GL11.glDisable(3042);
    GL11.glEnable(2884);
    GL11.glEnable(3553);
    GL11.glDisable(2848);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.shadeModel(7424);
    GlStateManager.disableBlend();
    GlStateManager.enableTexture2D();
    wolfRamCircle(paramFloat1, paramFloat2, paramFloat3, 1.5F, 16777215);
  }
  
  public static float getFadingHue() {
    return FADING_HUE;
  }
  
  public static void glColor(int paramInt) {
    float f1 = (paramInt >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt & 0xFF) / 255.0F;
    GL11.glColor4f(f2, f3, f4, f1);
  }
  
  public static void drawGuiRect(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt) {
    float f1 = (paramInt >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt & 0xFF) / 255.0F;
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(2848);
    GL11.glPushMatrix();
    GL11.glColor4f(f2, f3, f4, f1);
    GL11.glBegin(7);
    GL11.glVertex2d(paramDouble3, paramDouble2);
    GL11.glVertex2d(paramDouble1, paramDouble2);
    GL11.glVertex2d(paramDouble1, paramDouble4);
    GL11.glVertex2d(paramDouble3, paramDouble4);
    GL11.glEnd();
    GL11.glPopMatrix();
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glDisable(2848);
  }
  
  public static void enableGL2D() {
    GL11.glDisable(2929);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glDepthMask(true);
    GL11.glEnable(2848);
    GL11.glHint(3154, 4354);
    GL11.glHint(3155, 4354);
  }
  
  public static void fakeGuiRect(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt) {
    if (paramDouble1 < paramDouble3) {
      double d = paramDouble1;
      paramDouble1 = paramDouble3;
      paramDouble3 = d;
    } 
    if (paramDouble2 < paramDouble4) {
      double d = paramDouble2;
      paramDouble2 = paramDouble4;
      paramDouble4 = d;
    } 
    float f1 = (paramInt >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt & 0xFF) / 255.0F;
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    GlStateManager.color(f2, f3, f4, f1);
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
    bufferBuilder.pos(paramDouble1, paramDouble4, 0.0D).endVertex();
    bufferBuilder.pos(paramDouble3, paramDouble4, 0.0D).endVertex();
    bufferBuilder.pos(paramDouble3, paramDouble2, 0.0D).endVertex();
    bufferBuilder.pos(paramDouble1, paramDouble2, 0.0D).endVertex();
    tessellator.draw();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
  
  public static void drawHoloRect(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, float paramFloat, int paramInt) {
    enableGL2D();
    drawGuiRect(paramDouble1 + (int)paramFloat - 1.0D, paramDouble2, paramDouble3 - (int)paramFloat + 1.0D, paramDouble2 + (int)paramFloat, paramInt);
    drawGuiRect(paramDouble1, paramDouble2, paramDouble1 + (int)paramFloat, paramDouble4, paramInt);
    drawGuiRect(paramDouble3 - (int)paramFloat, paramDouble2, paramDouble3, paramDouble4, paramInt);
    drawGuiRect(paramDouble1 + (int)paramFloat - 1.0D, paramDouble4 - (int)paramFloat, paramDouble3 - (int)paramFloat + 1.0D, paramDouble4, paramInt);
    disableGL2D();
  }
  
  public static void drawRectDouble(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt) {
    enableGL2D();
    glColor(paramInt);
    drawRectDouble(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    disableGL2D();
  }
  
  public static void drawBorderedRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    enableGL2D();
    paramInt1 = (int)(paramInt1 * 2.0F);
    paramInt3 = (int)(paramInt3 * 2.0F);
    paramInt2 = (int)(paramInt2 * 2.0F);
    paramInt4 = (int)(paramInt4 * 2.0F);
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    drawVLine(paramInt1, paramInt2, paramInt4 - 1, paramInt6);
    drawVLine(paramInt3 - 1, paramInt2, paramInt4, paramInt6);
    drawHLine(paramInt1, paramInt3 - 1, paramInt2, paramInt6);
    drawHLine(paramInt1, paramInt3 - 2, paramInt4 - 1, paramInt6);
    Gui.drawRect(paramInt1 + 1, paramInt2 + 1, paramInt3 - 1, paramInt4 - 1, paramInt5);
    GL11.glScalef(2.0F, 2.0F, 2.0F);
    disableGL2D();
  }
  
  public static void drawOutlinedBoundingBox(AxisAlignedBB paramAxisAlignedBB) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(3, DefaultVertexFormats.POSITION);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ);
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ);
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ);
    tessellator.draw();
    bufferBuilder.begin(3, DefaultVertexFormats.POSITION);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ);
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ);
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ);
    tessellator.draw();
    bufferBuilder.begin(1, DefaultVertexFormats.POSITION);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ);
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ);
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ);
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ);
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ);
    tessellator.draw();
  }
  
  public static void enableGL3D() {
    GL11.glDisable(3008);
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(3553);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    GL11.glEnable(2884);
    GL11.glEnable(2848);
    GL11.glHint(3154, 4353);
    GL11.glDisable(2896);
  }
  
  public static void drawFilledCircle(int paramInt1, int paramInt2, double paramDouble, int paramInt3) {
    float f1 = (paramInt3 >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt3 >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt3 >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt3 & 0xFF) / 255.0F;
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glBlendFunc(770, 771);
    GL11.glColor4f(f2, f3, f4, f1);
    GL11.glBegin(6);
    for (byte b = 0; b <= 'Ũ'; b++) {
      double d1 = Math.sin(b * Math.PI / 180.0D) * paramDouble;
      double d2 = Math.cos(b * Math.PI / 180.0D) * paramDouble;
      GL11.glVertex2d(paramInt1 + d1, paramInt2 + d2);
    } 
    GL11.glEnd();
    GL11.glDisable(2848);
    GL11.glEnable(3553);
    GL11.glDisable(3042);
  }
  
  public static void drawUnfilledCircle(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, int paramInt3) {
    float f1 = (paramInt3 >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt3 >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt3 >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt3 & 0xFF) / 255.0F;
    GL11.glColor4f(f2, f3, f4, f1);
    GL11.glLineWidth(paramFloat2);
    GL11.glEnable(2848);
    GL11.glBegin(2);
    for (byte b = 0; b <= 'Ũ'; b++)
      GL11.glVertex2d(paramInt1 + Math.sin(b * Math.PI / 180.0D) * paramFloat1, paramInt2 + Math.cos(b * Math.PI / 180.0D) * paramFloat1); 
    GL11.glEnd();
    GL11.glDisable(2848);
  }
  
  public static void drawBorderedRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat, int paramInt5, int paramInt6) {
    enableGL2D();
    Gui.drawRect(paramInt1 + (int)paramFloat, paramInt2 + (int)paramFloat, paramInt3 - (int)paramFloat, paramInt4 - (int)paramFloat, paramInt5);
    Gui.drawRect(paramInt1 + (int)paramFloat, paramInt2, paramInt3 - (int)paramFloat, paramInt2 + (int)paramFloat, paramInt6);
    Gui.drawRect(paramInt1, paramInt2, paramInt1 + (int)paramFloat, paramInt4, paramInt6);
    Gui.drawRect(paramInt3 - (int)paramFloat, paramInt2, paramInt3, paramInt4, paramInt6);
    Gui.drawRect(paramInt1 + (int)paramFloat, paramInt4 - (int)paramFloat, paramInt3 - (int)paramFloat, paramInt4, paramInt6);
    disableGL2D();
  }
  
  public static void drawCircle(int paramInt1, int paramInt2, double paramDouble, int paramInt3) {
    float f1 = (paramInt3 >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt3 >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt3 >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt3 & 0xFF) / 255.0F;
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glBlendFunc(770, 771);
    GL11.glColor4f(f2, f3, f4, f1);
    GL11.glBegin(2);
    for (byte b = 0; b <= 'Ũ'; b++) {
      double d1 = Math.sin(b * Math.PI / 180.0D) * paramDouble;
      double d2 = Math.cos(b * Math.PI / 180.0D) * paramDouble;
      GL11.glVertex2d(paramInt1 + d1, paramInt2 + d2);
    } 
    GL11.glEnd();
    GL11.glDisable(2848);
    GL11.glEnable(3553);
    GL11.glDisable(3042);
  }
  
  public static void wolfRamCircle(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt) {
    GL11.glEnable(3042);
    GL11.glDisable(2884);
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glBlendFunc(770, 771);
    GL11.glLineWidth(1.0F);
    float f1 = (paramInt >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt & 0xFF) / 255.0F;
    GL11.glColor4f(f2, f3, f4, (f1 == 0.0F) ? 1.0F : f1);
    GL11.glLineWidth(paramFloat4);
    int i = (int)Math.min(Math.max(paramFloat3, 45.0F), 360.0F);
    GL11.glBegin(2);
    for (byte b = 0; b < i; b++) {
      double d = 6.283185307179586D * b / i;
      GL11.glVertex2d(paramFloat1 + Math.sin(d) * paramFloat3, paramFloat2 + Math.cos(d) * paramFloat3);
    } 
    GL11.glEnd();
    GL11.glDisable(3042);
    GL11.glEnable(2884);
    GL11.glEnable(3553);
    GL11.glDisable(2848);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.shadeModel(7424);
    GlStateManager.disableBlend();
    GlStateManager.enableTexture2D();
  }
  
  public static void drawRect(Rectangle paramRectangle, int paramInt) {
    drawRect(paramRectangle.x, paramRectangle.y, (paramRectangle.x + paramRectangle.width), (paramRectangle.y + paramRectangle.height), paramInt);
  }
  
  public static void drawOutlinedEntityESP(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
    GL11.glPushMatrix();
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    GL11.glColor4f(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    drawOutlinedBoundingBox(new AxisAlignedBB(paramDouble1 - paramDouble4, paramDouble2, paramDouble3 - paramDouble4, paramDouble1 + paramDouble4, paramDouble2 + paramDouble5, paramDouble3 + paramDouble4));
    GL11.glDisable(2848);
    GL11.glEnable(3553);
    GL11.glEnable(2929);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
    GL11.glPopMatrix();
  }
  
  public static void drawCircle(int paramInt1, int paramInt2, float paramFloat, int paramInt3) {
    float f1 = (paramInt3 >> 24 & 0xFF) / 255.0F;
    float f2 = (paramInt3 >> 16 & 0xFF) / 255.0F;
    float f3 = (paramInt3 >> 8 & 0xFF) / 255.0F;
    float f4 = (paramInt3 & 0xFF) / 255.0F;
    GL11.glColor4f(f2, f3, f4, f1);
    GL11.glBegin(9);
    for (byte b = 0; b <= 'Ũ'; b++)
      GL11.glVertex2d(paramInt1 + Math.sin(b * Math.PI / 180.0D) * paramFloat, paramInt2 + Math.cos(b * Math.PI / 180.0D) * paramFloat); 
    GL11.glEnd();
  }
  
  public static void drawGradientRect(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt1, int paramInt2) {
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
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.shadeModel(7425);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.endVertex();
    bufferBuilder.color(f2, f3, f4, f1);
    bufferBuilder.pos(paramDouble3, paramDouble2, 0.0D);
    bufferBuilder.pos(paramDouble1, paramDouble2, 0.0D);
    bufferBuilder.color(f6, f7, f8, f5);
    bufferBuilder.pos(paramDouble1, paramDouble4, 0.0D);
    bufferBuilder.pos(paramDouble3, paramDouble4, 0.0D);
    tessellator.draw();
    GlStateManager.shadeModel(7424);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
  }
  
  public static void disableGL3D() {
    GL11.glEnable(2896);
    GL11.glDisable(2848);
    GL11.glEnable(3553);
    GL11.glEnable(2929);
    GL11.glDisable(3042);
    GL11.glEnable(3008);
    GL11.glDepthMask(true);
    GL11.glCullFace(1029);
  }
  
  public static void disableGL2D() {
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glEnable(2929);
    GL11.glDisable(2848);
    GL11.glHint(3154, 4352);
    GL11.glHint(3155, 4352);
  }
  
  public static void drawTracerLine(double[] paramArrayOfdouble, float[] paramArrayOffloat, float paramFloat) {
    GL11.glPushMatrix();
    GL11.glEnable(3042);
    GL11.glEnable(2848);
    GL11.glDisable(2929);
    GL11.glDisable(3553);
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(3042);
    GL11.glLineWidth(paramFloat);
    GL11.glColor4f(paramArrayOffloat[0], paramArrayOffloat[1], paramArrayOffloat[2], paramArrayOffloat[3]);
    GL11.glBegin(1);
    GL11.glVertex3d(0.0D, (Minecraft.getMinecraft()).player.getEyeHeight(), 0.0D);
    GL11.glVertex3d(paramArrayOfdouble[0], paramArrayOfdouble[1], paramArrayOfdouble[2]);
    GL11.glEnd();
    GL11.glDisable(3042);
    GL11.glEnable(3553);
    GL11.glEnable(2929);
    GL11.glDisable(2848);
    GL11.glDisable(3042);
    GL11.glPopMatrix();
  }
  
  public static void renderCircleWithHoleInCenter(Vector2f paramVector2f, float paramFloat1, float paramFloat2, float[] paramArrayOffloat, float paramFloat3, float paramFloat4) {
    float f1 = (float)(paramVector2f.x + Math.sin(paramFloat3) * paramFloat1);
    float f2 = (float)(paramVector2f.y + Math.cos(paramFloat3) * paramFloat1);
    float f3 = (float)(paramVector2f.x + Math.sin(paramFloat3) * paramFloat2);
    float f4 = (float)(paramVector2f.y + Math.cos(paramFloat3) * paramFloat2);
    float f5 = (float)(paramVector2f.x + Math.sin((paramFloat3 + paramFloat4)) * paramFloat2);
    float f6 = (float)(paramVector2f.y + Math.cos((paramFloat3 + paramFloat4)) * paramFloat2);
    float f7 = (float)(paramVector2f.x + Math.sin((paramFloat3 + paramFloat4)) * paramFloat1);
    float f8 = (float)(paramVector2f.y + Math.cos((paramFloat3 + paramFloat4)) * paramFloat1);
    float f9 = paramArrayOffloat[3];
    float f10 = paramArrayOffloat[0];
    float f11 = paramArrayOffloat[1];
    float f12 = paramArrayOffloat[2];
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.endVertex();
    bufferBuilder.pos(f1, f2, 0.0D);
    bufferBuilder.pos(f3, f4, 0.0D);
    bufferBuilder.pos(f5, f6, 0.0D);
    bufferBuilder.pos(f7, f8, 0.0D);
  }
  
  public static void drawRectFourColor(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
    enableGL2D();
    GL11.glColor4f(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    drawRectDouble(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    disableGL2D();
  }
}
