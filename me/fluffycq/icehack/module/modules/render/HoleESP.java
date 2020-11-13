//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import me.fluffycq.icehack.events.RenderEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.module.modules.combat.AutoCrystal;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.ICERenderer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleESP extends Module {
  public void onUpdate() {
    if (this.safeHoles == null) {
      this.safeHoles = new ConcurrentHashMap<>();
    } else {
      this.safeHoles.clear();
    } 
    int i = (int)Math.ceil(this.renderDist.getValDouble());
    List list = AutoCrystal.getSphere(getPlayerPos(), i, i, false, true, 0);
    for (BlockPos blockPos : list) {
      if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
        continue; 
      boolean bool1 = true;
      boolean bool2 = true;
      for (BlockPos blockPos1 : this.surroundOffset) {
        Block block = mc.world.getBlockState(blockPos.add((Vec3i)blockPos1)).getBlock();
        if (block != Blocks.BEDROCK)
          bool2 = false; 
        if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
          bool1 = false;
          break;
        } 
      } 
      if (bool1)
        this.safeHoles.put(blockPos, Boolean.valueOf(bool2)); 
    } 
  }
  
  private void drawBox(BlockPos paramBlockPos, int paramInt1, int paramInt2, int paramInt3) {
    Color color = new Color(paramInt1, paramInt2, paramInt3, (int)this.a0.getValDouble());
    if (this.mode.getValString().equals("Down")) {
      ICERenderer.drawBox(paramBlockPos, color.getRGB(), 1);
    } else if (this.mode.getValString().equals("Full")) {
      ICERenderer.drawBox(paramBlockPos, color.getRGB(), 63);
    } else if (this.mode.getValString().equals("Outline")) {
      ICERenderer.drawBoundingBoxBottomBlockPos(paramBlockPos, (float)this.width.getValDouble(), paramInt1, paramInt2, paramInt3, (int)this.a0.getValDouble());
    } 
  }
  
  public void onWorld(RenderEvent paramRenderEvent) {
    if (mc.player == null || this.safeHoles == null)
      return; 
    if (this.safeHoles.isEmpty())
      return; 
    ICERenderer.prepare(7);
    this.safeHoles.forEach((paramBlockPos, paramBoolean) -> {
          if (paramBoolean.booleanValue()) {
            drawBox(paramBlockPos, (int)this.r2.getValDouble(), (int)this.g2.getValDouble(), (int)this.b2.getValDouble());
          } else {
            drawBox(paramBlockPos, (int)this.r1.getValDouble(), (int)this.g1.getValDouble(), (int)this.b1.getValDouble());
          } 
        });
    ICERenderer.release();
  }
  
  public HoleESP() {
    super("HoleESP", 0, Category.RENDER);
    this.options.add("Full");
    this.options.add("Down");
    this.options.add("Outline");
    this.mode = new Setting("Mode", this, "Full", this.options);
  }
  
  public static BlockPos getPlayerPos() {
    return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
  }
  
  private enum RenderMode {
    DOWN, BLOCK;
    
    static {
    
    }
  }
  
  private enum RenderBlocks {
    OBBY, BOTH, BEDROCK;
    
    static {
    
    }
  }
}
