//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import java.awt.Color;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

public class ICERenderer extends Tessellator {
  public static void drawBoundingBoxBottomBlockPos(BlockPos paramBlockPos, float paramFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    GlStateManager.disableDepth();
    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
    GlStateManager.disableTexture2D();
    GlStateManager.depthMask(false);
    GL11.glEnable(2848);
    GL11.glHint(3154, 4354);
    GL11.glLineWidth(paramFloat);
    Minecraft minecraft = Minecraft.getMinecraft();
    double d1 = paramBlockPos.x - (minecraft.getRenderManager()).viewerPosX;
    double d2 = paramBlockPos.y - (minecraft.getRenderManager()).viewerPosY;
    double d3 = paramBlockPos.z - (minecraft.getRenderManager()).viewerPosZ;
    AxisAlignedBB axisAlignedBB = new AxisAlignedBB(d1, d2, d3, d1 + 1.0D, d2 + 1.0D, d3 + 1.0D);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    tessellator.draw();
    GL11.glDisable(2848);
    GlStateManager.depthMask(true);
    GlStateManager.enableDepth();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
  }
  
  public ICERenderer() {
    super(2097152);
  }
  
  public static void prepareGL() {
    GL11.glBlendFunc(770, 771);
    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    GlStateManager.glLineWidth(1.5F);
    GlStateManager.disableTexture2D();
    GlStateManager.depthMask(false);
    GlStateManager.enableBlend();
    GlStateManager.disableDepth();
    GlStateManager.disableLighting();
    GlStateManager.disableCull();
    GlStateManager.enableAlpha();
    GlStateManager.color(1.0F, 1.0F, 1.0F);
  }
  
  public static void begin(int paramInt) {
    INSTANCE.getBuffer().begin(paramInt, DefaultVertexFormats.POSITION_COLOR);
  }
  
  public static void releaseGL() {
    GlStateManager.enableCull();
    GlStateManager.depthMask(true);
    GlStateManager.enableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.enableDepth();
  }
  
  public static void drawBox(BlockPos paramBlockPos, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    drawBox(INSTANCE.getBuffer(), paramBlockPos.x, paramBlockPos.y, paramBlockPos.z, 1.0F, 1.0F, 1.0F, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public static void drawBoundingBox(AxisAlignedBB paramAxisAlignedBB, float paramFloat, int paramInt) {
    int i = paramInt >>> 24 & 0xFF;
    int j = paramInt >>> 16 & 0xFF;
    int k = paramInt >>> 8 & 0xFF;
    int m = paramInt & 0xFF;
    drawBoundingBox(paramAxisAlignedBB, paramFloat, j, k, m, i);
  }
  
  public static void drawBoundingBoxBlockPos(BlockPos paramBlockPos, float paramFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    GlStateManager.disableDepth();
    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
    GlStateManager.disableTexture2D();
    GlStateManager.depthMask(false);
    GL11.glEnable(2848);
    GL11.glHint(3154, 4354);
    GL11.glLineWidth(paramFloat);
    Minecraft minecraft = Minecraft.getMinecraft();
    double d1 = paramBlockPos.x - (minecraft.getRenderManager()).viewerPosX;
    double d2 = paramBlockPos.y - (minecraft.getRenderManager()).viewerPosY;
    double d3 = paramBlockPos.z - (minecraft.getRenderManager()).viewerPosZ;
    AxisAlignedBB axisAlignedBB = new AxisAlignedBB(d1, d2, d3, d1 + 1.0D, d2 + 1.0D, d3 + 1.0D);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    tessellator.draw();
    bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    tessellator.draw();
    bufferBuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    tessellator.draw();
    GL11.glDisable(2848);
    GlStateManager.depthMask(true);
    GlStateManager.enableDepth();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
  }
  
  static {
    FACEMAP = new HashMap<>();
    FACEMAP.put(EnumFacing.DOWN, Integer.valueOf(1));
    FACEMAP.put(EnumFacing.WEST, Integer.valueOf(16));
    FACEMAP.put(EnumFacing.NORTH, Integer.valueOf(4));
    FACEMAP.put(EnumFacing.SOUTH, Integer.valueOf(8));
    FACEMAP.put(EnumFacing.EAST, Integer.valueOf(32));
    FACEMAP.put(EnumFacing.UP, Integer.valueOf(2));
  }
  
  public static void drawBoxOpacity(BlockPos paramBlockPos, int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt2;
    int j = paramInt1 >>> 16 & 0xFF;
    int k = paramInt1 >>> 8 & 0xFF;
    int m = paramInt1 & 0xFF;
    drawBox(paramBlockPos, j, k, m, i, paramInt3);
  }
  
  public static void drawBox(BlockPos paramBlockPos, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat, int paramInt5) {
    drawBox(INSTANCE.getBuffer(), paramBlockPos.x, paramBlockPos.y, paramBlockPos.z, paramFloat, 1.0F, 1.0F, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public static void drawBoundingBox(AxisAlignedBB paramAxisAlignedBB, float paramFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    GlStateManager.disableDepth();
    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
    GlStateManager.disableTexture2D();
    GlStateManager.depthMask(false);
    GlStateManager.glLineWidth(paramFloat);
    BufferBuilder bufferBuilder = INSTANCE.getBuffer();
    bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    render();
    GlStateManager.depthMask(true);
    GlStateManager.enableDepth();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
  }
  
  public static BufferBuilder getBufferBuilder() {
    return INSTANCE.getBuffer();
  }
  
  public static void logoutBox(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, Color paramColor) {
    Minecraft minecraft = Minecraft.getMinecraft();
    paramDouble1 -= (minecraft.getRenderManager()).viewerPosX;
    paramDouble2 -= (minecraft.getRenderManager()).viewerPosY;
    paramDouble3 -= (minecraft.getRenderManager()).viewerPosZ;
    paramDouble4 -= (minecraft.getRenderManager()).viewerPosX;
    paramDouble5 -= (minecraft.getRenderManager()).viewerPosY;
    paramDouble6 -= (minecraft.getRenderManager()).viewerPosZ;
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(3042);
    GL11.glLineWidth(2.0F);
    GL11.glDisable(3553);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    GL11.glDepthMask(false);
    setColor(paramColor);
    drawSelectionBoundingBox(new AxisAlignedBB(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6));
    GL11.glEnable(3553);
    GL11.glEnable(2929);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
  }
  
  public static void setColor(Color paramColor) {
    GL11.glColor4f(paramColor.getRed() / 255.0F, paramColor.getGreen() / 255.0F, paramColor.getBlue() / 255.0F, paramColor.getAlpha() / 255.0F);
  }
  
  public static void render() {
    INSTANCE.draw();
  }
  
  public static void drawSelectionBoundingBox(AxisAlignedBB paramAxisAlignedBB) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(3, DefaultVertexFormats.POSITION);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).endVertex();
    tessellator.draw();
    bufferBuilder.begin(3, DefaultVertexFormats.POSITION);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).endVertex();
    tessellator.draw();
    bufferBuilder.begin(1, DefaultVertexFormats.POSITION);
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ).endVertex();
    bufferBuilder.pos(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ).endVertex();
    tessellator.draw();
  }
  
  public static void release() {
    render();
    releaseGL();
  }
  
  public static void drawBox(BlockPos paramBlockPos, int paramInt1, int paramInt2) {
    int i = paramInt1 >>> 24 & 0xFF;
    int j = paramInt1 >>> 16 & 0xFF;
    int k = paramInt1 >>> 8 & 0xFF;
    int m = paramInt1 & 0xFF;
    drawBox(paramBlockPos, j, k, m, i, paramInt2);
  }
  
  public static void drawBox(float paramFloat1, float paramFloat2, float paramFloat3, int paramInt1, int paramInt2) {
    int i = paramInt1 >>> 24 & 0xFF;
    int j = paramInt1 >>> 16 & 0xFF;
    int k = paramInt1 >>> 8 & 0xFF;
    int m = paramInt1 & 0xFF;
    drawBox(INSTANCE.getBuffer(), paramFloat1, paramFloat2, paramFloat3, 1.0F, 1.0F, 1.0F, j, k, m, i, paramInt2);
  }
  
  public static void drawLines(BufferBuilder paramBufferBuilder, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    if ((paramInt5 & 0x11) != 0) {
      paramBufferBuilder.pos(paramFloat1, paramFloat2, paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, paramFloat2, (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x12) != 0) {
      paramBufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat5), paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat5), (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x21) != 0) {
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), paramFloat2, paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), paramFloat2, (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x22) != 0) {
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), (paramFloat2 + paramFloat5), paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), (paramFloat2 + paramFloat5), (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x5) != 0) {
      paramBufferBuilder.pos(paramFloat1, paramFloat2, paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), paramFloat2, paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x6) != 0) {
      paramBufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat5), paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), (paramFloat2 + paramFloat5), paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x9) != 0) {
      paramBufferBuilder.pos(paramFloat1, paramFloat2, (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), paramFloat2, (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0xA) != 0) {
      paramBufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat5), (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), (paramFloat2 + paramFloat5), (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x14) != 0) {
      paramBufferBuilder.pos(paramFloat1, paramFloat2, paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat5), paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x24) != 0) {
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), paramFloat2, paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), (paramFloat2 + paramFloat5), paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x18) != 0) {
      paramBufferBuilder.pos(paramFloat1, paramFloat2, (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat5), (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x28) != 0) {
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), paramFloat2, (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), (paramFloat2 + paramFloat5), (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
  }
  
  public static void drawOutlinedBox(AxisAlignedBB paramAxisAlignedBB) {
    GL11.glBegin(1);
    GL11.glVertex3d(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ);
    GL11.glVertex3d(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ);
    GL11.glVertex3d(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ);
    GL11.glVertex3d(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ);
    GL11.glVertex3d(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ);
    GL11.glVertex3d(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ);
    GL11.glVertex3d(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ);
    GL11.glVertex3d(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ);
    GL11.glVertex3d(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ);
    GL11.glVertex3d(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ);
    GL11.glVertex3d(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.minZ);
    GL11.glVertex3d(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ);
    GL11.glVertex3d(paramAxisAlignedBB.maxX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ);
    GL11.glVertex3d(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ);
    GL11.glVertex3d(paramAxisAlignedBB.minX, paramAxisAlignedBB.minY, paramAxisAlignedBB.maxZ);
    GL11.glVertex3d(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ);
    GL11.glVertex3d(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ);
    GL11.glVertex3d(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ);
    GL11.glVertex3d(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ);
    GL11.glVertex3d(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ);
    GL11.glVertex3d(paramAxisAlignedBB.maxX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ);
    GL11.glVertex3d(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ);
    GL11.glVertex3d(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.maxZ);
    GL11.glVertex3d(paramAxisAlignedBB.minX, paramAxisAlignedBB.maxY, paramAxisAlignedBB.minZ);
    GL11.glEnd();
  }
  
  public static void prepare(int paramInt) {
    prepareGL();
    begin(paramInt);
  }
  
  public static void drawBox(BufferBuilder paramBufferBuilder, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    if ((paramInt5 & 0x1) != 0) {
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), paramFloat2, paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), paramFloat2, (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, paramFloat2, (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, paramFloat2, paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x2) != 0) {
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), (paramFloat2 + paramFloat5), paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat5), paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat5), (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), (paramFloat2 + paramFloat5), (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x4) != 0) {
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), paramFloat2, paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, paramFloat2, paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat5), paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), (paramFloat2 + paramFloat5), paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x8) != 0) {
      paramBufferBuilder.pos(paramFloat1, paramFloat2, (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), paramFloat2, (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), (paramFloat2 + paramFloat5), (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat5), (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x10) != 0) {
      paramBufferBuilder.pos(paramFloat1, paramFloat2, paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, paramFloat2, (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat5), (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos(paramFloat1, (paramFloat2 + paramFloat5), paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
    if ((paramInt5 & 0x20) != 0) {
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), paramFloat2, (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), paramFloat2, paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), (paramFloat2 + paramFloat5), paramFloat3).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
      paramBufferBuilder.pos((paramFloat1 + paramFloat4), (paramFloat2 + paramFloat5), (paramFloat3 + paramFloat6)).color(paramInt1, paramInt2, paramInt3, paramInt4).endVertex();
    } 
  }
  
  public static final class Line {
    static {
      DOWN_WEST = 17;
      NORTH_EAST = 36;
      UP_SOUTH = 10;
      UP_WEST = 18;
      SOUTH_EAST = 40;
      DOWN_EAST = 33;
      UP_EAST = 34;
      DOWN_NORTH = 5;
      ALL = 63;
      NORTH_WEST = 20;
    }
  }
  
  public static final class Quad {
    static {
      SOUTH = 8;
      ALL = 63;
      EAST = 32;
    }
  }
}
