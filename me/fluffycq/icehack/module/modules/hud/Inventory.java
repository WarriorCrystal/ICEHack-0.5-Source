//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.hud;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.hudeditor.frame.Frame;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class Inventory extends Module {
  public void drawRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt) {
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
  
  public Inventory() {
    super("Inventory", 0, Category.HUD);
  }
  
  public void onRender() {
    this.frame = ICEHack.hudeditor.frames.get(1);
    if (this.frame.extended) {
      GlStateManager.enableAlpha();
      GlStateManager.disableBlend();
      GlStateManager.disableDepth();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GuiIngame.drawRect(this.frame.x, this.frame.y, this.frame.x + 162, this.frame.y + 54, 1963986960);
      GlStateManager.enableDepth();
      NonNullList nonNullList = mc.player.inventory.mainInventory;
      GlStateManager.clear(256);
      int i = nonNullList.size();
      for (byte b = 9; b < i; b++) {
        int j = this.frame.x + 1 + b % 9 * 18;
        int k = this.frame.y + 1 + (b / 9 - 1) * 18;
        GlStateManager.pushMatrix();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI((ItemStack)nonNullList.get(b), j, k);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, (ItemStack)nonNullList.get(b), j, k);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
      } 
    } 
  }
}
