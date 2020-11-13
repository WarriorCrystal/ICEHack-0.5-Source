//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.events;

import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.module.ModuleManager;
import me.fluffycq.icehack.module.modules.render.BlurGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

public class ForgeEvents {
  @SubscribeEvent
  public void onWorldTick(TickEvent.WorldTickEvent paramWorldTickEvent) {
    if (this.mc.player == null || this.mc.world == null)
      return; 
    for (Module module : this.moduleManager.getEnabledModules())
      module.onWorldTick(paramWorldTickEvent); 
  }
  
  @SubscribeEvent
  public void guiOpen(GuiScreenEvent.InitGuiEvent paramInitGuiEvent) {
    if (this.moduleManager.getModule("BlurGui") != null && this.moduleManager.getModule("BlurGui").isEnabled()) {
      BlurGui blurGui = (BlurGui)this.moduleManager.getModule("BlurGui");
      BlurGui.enableBlur();
    } 
  }
  
  @SubscribeEvent
  public void onUpdate(TickEvent.ClientTickEvent paramClientTickEvent) {
    if (this.mc.player == null)
      return; 
    for (Module module : this.moduleManager.getEnabledModules())
      module.onUpdate(); 
  }
  
  @SubscribeEvent
  public void onRender(RenderGameOverlayEvent.Post paramPost) {
    RenderGameOverlayEvent.ElementType elementType = RenderGameOverlayEvent.ElementType.EXPERIENCE;
    if (!this.mc.player.isCreative() && this.mc.player.getRidingEntity() instanceof net.minecraft.entity.passive.AbstractHorse)
      elementType = RenderGameOverlayEvent.ElementType.HEALTHMOUNT; 
    if (paramPost.getType() == elementType) {
      for (Module module : this.moduleManager.getEnabledModules())
        module.onRender(); 
      GL11.glPushMatrix();
      GL11.glPopMatrix();
      GlStateManager.enableCull();
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.enableDepth();
    } 
  }
  
  @SubscribeEvent
  public void onWorldRender(RenderWorldLastEvent paramRenderWorldLastEvent) {
    if (paramRenderWorldLastEvent.isCanceled())
      return; 
    this.moduleManager.onWorldRender(paramRenderWorldLastEvent);
  }
}
