//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.mixin.client;

import java.awt.Color;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {GuiScreen.class}, priority = 2147483647)
public class MixinGuiScreen {
  RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
  
  FontRenderer fontRenderer = (Minecraft.getMinecraft()).fontRenderer;
  
  @Inject(method = {"renderToolTip"}, at = {@At("HEAD")}, cancellable = true)
  public void renderToolTip(ItemStack paramItemStack, int paramInt1, int paramInt2, CallbackInfo paramCallbackInfo) {
    if (ICEHack.fevents.moduleManager.getModule("ShulkerViewer").isEnabled() && paramItemStack.getItem() instanceof net.minecraft.item.ItemShulkerBox) {
      NBTTagCompound nBTTagCompound = paramItemStack.getTagCompound();
      if (nBTTagCompound != null && nBTTagCompound.hasKey("BlockEntityTag", 10)) {
        NBTTagCompound nBTTagCompound1 = nBTTagCompound.getCompoundTag("BlockEntityTag");
        if (nBTTagCompound1.hasKey("Items", 9)) {
          int n;
          paramCallbackInfo.cancel();
          NonNullList nonNullList = NonNullList.withSize(27, ItemStack.EMPTY);
          ItemStackHelper.loadAllItems(nBTTagCompound1, nonNullList);
          GlStateManager.enableBlend();
          GlStateManager.disableRescaleNormal();
          RenderHelper.disableStandardItemLighting();
          GlStateManager.disableLighting();
          GlStateManager.disableDepth();
          int i = paramInt1 + 12;
          int j = paramInt2 - 12;
          char c = '';
          byte b1 = 60;
          this.itemRender.zLevel = 300.0F;
          Module module1 = ICEHack.fevents.moduleManager.getModule("ShulkerViewer");
          Module module2 = ICEHack.fevents.moduleManager.getModule("ClickGUI");
          int k = (new Color(16, 16, 16, (int)module1.getSetting("Opacity").getValDouble())).getRGB();
          drawRect(i, j, (i + c), (j + b1), k);
          int m = (new Color((int)module1.getSetting("Red").getValDouble(), (int)module1.getSetting("Green").getValDouble(), (int)module1.getSetting("Blue").getValDouble())).getRGB();
          if (module2.getSetting("Rainbow").getValBoolean()) {
            n = Color.getHSBColor((float)(System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F).getRGB();
          } else {
            n = (new Color((int)module2.getSetting("Red").getValDouble(), (int)module2.getSetting("Green").getValDouble(), (int)module2.getSetting("Blue").getValDouble())).getRGB();
          } 
          GuiUtil.drawHorizontalLine(i - 1, i + c, j - 1, n);
          GuiUtil.drawHorizontalLine(i - 1, i + c, j + b1 - 1, n);
          GuiUtil.drawVerticalLine(i - 1, j - 1, j + b1 - 1, n);
          GuiUtil.drawVerticalLine(i + c, j - 1, j + b1 - 1, n);
          drawCenteredString(paramItemStack.getDisplayName(), i + c / 2, j + 2, m);
          GlStateManager.enableBlend();
          GlStateManager.enableAlpha();
          GlStateManager.enableTexture2D();
          GlStateManager.enableLighting();
          GlStateManager.enableDepth();
          RenderHelper.enableGUIStandardItemLighting();
          for (byte b2 = 0; b2 < nonNullList.size(); b2++) {
            int i1 = paramInt1 + b2 % 9 * 16 + 11;
            int i2 = paramInt2 + b2 / 9 * 16 - 11 + 8;
            ItemStack itemStack = (ItemStack)nonNullList.get(b2);
            this.itemRender.renderItemAndEffectIntoGUI(itemStack, i1 + 3, i2);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemStack, i1 + 3, i2, null);
          } 
          RenderHelper.disableStandardItemLighting();
          this.itemRender.zLevel = 0.0F;
          GlStateManager.enableLighting();
          GlStateManager.enableDepth();
          RenderHelper.enableStandardItemLighting();
          GlStateManager.enableRescaleNormal();
        } 
      } 
    } 
  }
  
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
  
  public void drawCenteredString(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    this.fontRenderer.drawStringWithShadow(paramString, (paramInt1 - this.fontRenderer.getStringWidth(paramString) / 2), paramInt2, paramInt3);
  }
}
