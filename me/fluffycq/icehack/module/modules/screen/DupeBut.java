//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class DupeBut extends GuiButton {
  public void drawButton(Minecraft paramMinecraft, int paramInt1, int paramInt2, float paramFloat) {
    if (this.visible) {
      FontRenderer fontRenderer = paramMinecraft.fontRenderer;
      paramMinecraft.getTextureManager().bindTexture(BUTTON_TEXTURES);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.hovered = (paramInt1 >= this.x && paramInt2 >= this.y && paramInt1 < this.x + this.width && paramInt2 < this.y + this.height);
      int i = getHoverState(this.hovered);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
      drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
      mouseDragged(paramMinecraft, paramInt1, paramInt2);
      int j = 14737632;
      if (this.packedFGColour != 0) {
        j = this.packedFGColour;
      } else if (!this.enabled) {
        j = 10526880;
      } else if (this.hovered) {
        j = 16777120;
      } 
      drawCenteredString(fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
    } 
  }
  
  static {
  
  }
  
  public DupeBut(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString) {
    super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramString);
  }
  
  protected int getHoverState(boolean paramBoolean) {
    byte b = 1;
    if (!this.enabled) {
      b = 0;
    } else if (paramBoolean) {
      b = 2;
    } 
    return b;
  }
}
