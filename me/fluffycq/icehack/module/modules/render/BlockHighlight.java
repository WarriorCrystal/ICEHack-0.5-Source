//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.render;

import java.awt.Color;
import me.fluffycq.icehack.events.RenderEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.ICERenderer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class BlockHighlight extends Module {
  public BlockHighlight() {
    super("BlockHighlight", 0, Category.RENDER);
  }
  
  public void onWorld(RenderEvent paramRenderEvent) {
    float[] arrayOfFloat = { (float)(System.currentTimeMillis() % 11520L) / 11520.0F };
    int i = Color.HSBtoRGB(arrayOfFloat[0], 1.0F, 1.0F);
    int j = i >> 16 & 0xFF;
    int k = i >> 8 & 0xFF;
    int m = i & 0xFF;
    Minecraft minecraft = Minecraft.getMinecraft();
    if (minecraft.objectMouseOver != null) {
      RayTraceResult rayTraceResult = minecraft.objectMouseOver;
      if (rayTraceResult.typeOfHit != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
        BlockPos blockPos = rayTraceResult.getBlockPos();
        IBlockState iBlockState = minecraft.world.getBlockState(blockPos);
        if (iBlockState.getMaterial() != Material.AIR && minecraft.world.getWorldBorder().contains(blockPos)) {
          ICERenderer.prepare(7);
          if (this.rainbow.getValBoolean()) {
            ICERenderer.drawBoundingBoxBlockPos(blockPos, (int)this.width.getValDouble(), j, k, m, (int)this.opacity.getValDouble());
          } else {
            ICERenderer.drawBoundingBoxBlockPos(blockPos, (int)this.width.getValDouble(), (int)this.r.getValDouble(), (int)this.g.getValDouble(), (int)this.b.getValDouble(), (int)this.opacity.getValDouble());
          } 
          ICERenderer.release();
        } 
      } 
    } 
  }
}
