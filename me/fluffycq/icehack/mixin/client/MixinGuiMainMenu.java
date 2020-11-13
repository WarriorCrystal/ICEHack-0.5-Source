//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.util.ICELabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {GuiMainMenu.class}, priority = 2147483647)
public class MixinGuiMainMenu extends GuiScreen {
  @Inject(method = {"initGui"}, at = {@At("HEAD")}, cancellable = true)
  public void initGui(CallbackInfo paramCallbackInfo) {
    this.buttonList.add(new ICELabel(1337, 1, 5, this.mc.fontRenderer.getStringWidth("ICEHack b1.5") + 3, 20, "ICEHack b1.5"));
  }
  
  @Inject(method = {"drawPanorama"}, at = {@At("HEAD")}, cancellable = true)
  public void drawPanorama(int paramInt1, int paramInt2, float paramFloat, CallbackInfo paramCallbackInfo) {
    paramCallbackInfo.cancel();
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    GlStateManager.matrixMode(5889);
    GlStateManager.pushMatrix();
    GlStateManager.loadIdentity();
    Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
    GlStateManager.matrixMode(5888);
    GlStateManager.pushMatrix();
    GlStateManager.loadIdentity();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
    GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.disableCull();
    GlStateManager.depthMask(false);
    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    GlStateManager.pushMatrix();
    GlStateManager.pushMatrix();
    Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("icehack/icehack.png"));
    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
    char c = 'ÿ';
    bufferBuilder.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, c).endVertex();
    bufferBuilder.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, c).endVertex();
    bufferBuilder.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, c).endVertex();
    bufferBuilder.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, c).endVertex();
    tessellator.draw();
    GlStateManager.popMatrix();
    GlStateManager.popMatrix();
    GlStateManager.colorMask(true, true, true, false);
    bufferBuilder.setTranslation(0.0D, 0.0D, 0.0D);
    GlStateManager.colorMask(true, true, true, true);
    GlStateManager.matrixMode(5889);
    GlStateManager.popMatrix();
    GlStateManager.matrixMode(5888);
    GlStateManager.popMatrix();
    GlStateManager.depthMask(true);
    GlStateManager.enableCull();
    GlStateManager.enableDepth();
  }
  
  @Inject(method = {"rotateAndBlurSkybox"}, at = {@At("HEAD")}, cancellable = true)
  public void rotateAndBlurSkybox(CallbackInfo paramCallbackInfo) {
    paramCallbackInfo.cancel();
  }
}
