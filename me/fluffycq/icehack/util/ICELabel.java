//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class ICELabel extends GuiButton {
  public void drawButton(Minecraft paramMinecraft, int paramInt1, int paramInt2, float paramFloat) {
    if (this.visible) {
      FontRenderer fontRenderer = paramMinecraft.fontRenderer;
      paramMinecraft.getTextureManager().bindTexture(BUTTON_TEXTURES);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.hovered = (paramInt1 >= this.x && paramInt2 >= this.y && paramInt1 < this.x + this.width && paramInt2 < this.y + this.height);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      mouseDragged(paramMinecraft, paramInt1, paramInt2);
      drawCenteredString(fontRenderer, this.displayString, this.x + this.width / 2, this.y, -16518916);
    } 
  }
  
  static {
  
  }
  
  public ICELabel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString) {
    super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramString);
  }
}
