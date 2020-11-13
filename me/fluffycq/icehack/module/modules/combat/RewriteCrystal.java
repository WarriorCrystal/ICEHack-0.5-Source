//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.events.RenderEvent;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.message.Messages;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.ICERenderer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class RewriteCrystal extends Module {
  private static void resetRotation() {
    if (spoofing) {
      yaw = mc.player.rotationYaw;
      pitch = mc.player.rotationPitch;
      spoofing = false;
    } 
  }
  
  public void onUpdate() {
    if (this.destroy.getValBoolean()) {
      EntityEnderCrystal entityEnderCrystal = mc.world.loadedEntityList.stream().filter(paramEntity -> paramEntity instanceof EntityEnderCrystal).map(paramEntity -> paramEntity).min(Comparator.comparing(paramEntity -> Float.valueOf(mc.player.getDistance(paramEntity)))).orElse(null);
      if (entityEnderCrystal != null && mc.player.getDistance((Entity)entityEnderCrystal) <= this.breakrange.getValDouble()) {
        if (this.antistuck.getValBoolean() && (System.nanoTime() / 1000000L - this.antiStuckSystemTime) <= 400.0D + 400.0D - this.breakspeed.getValDouble() * 20.0D)
          return; 
        if ((System.nanoTime() / 1000000L - this.breakSystemTime) >= 420.0D - this.breakspeed.getValDouble() * 20.0D) {
          lookAtPacket(entityEnderCrystal.posX, entityEnderCrystal.posY, entityEnderCrystal.posZ, (EntityPlayer)mc.player);
          mc.playerController.attackEntity((EntityPlayer)mc.player, (Entity)entityEnderCrystal);
          mc.player.swingArm(EnumHand.MAIN_HAND);
          this.breakSystemTime = System.nanoTime() / 1000000L;
        } 
      } else {
        resetRotation();
      } 
    } 
    byte b = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) ? mc.player.inventory.currentItem : -1;
    if (b == -1)
      for (byte b1 = 0; b1 < 9; b1++) {
        if (mc.player.inventory.getStackInSlot(b1).getItem() == Items.END_CRYSTAL) {
          b = b1;
          break;
        } 
      }  
    boolean bool = false;
    if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
      bool = true;
    } else if (b == -1) {
      return;
    } 
    Entity entity1 = null;
    Entity entity2 = null;
    BlockPos blockPos = null;
    List<BlockPos> list = findCrystalBlocks();
    ArrayList arrayList = new ArrayList();
    arrayList.addAll((Collection)mc.world.playerEntities.stream().filter(paramEntityPlayer -> !Friends.isFriend(paramEntityPlayer.getName())).collect(Collectors.toList()));
    this.damage = 0.5D;
    for (Entity entity : arrayList) {
      if (entity == mc.player || ((EntityLivingBase)entity).getHealth() <= 0.0F || mc.player.getDistanceSq(entity) > this.range.getValDouble() * this.range.getValDouble())
        continue; 
      for (BlockPos blockPos1 : list) {
        double d1 = entity.getDistanceSq(blockPos1);
        if (d1 > 56.2D)
          continue; 
        double d2 = calculateDamage(blockPos1.x + 0.5D, (blockPos1.y + 1), blockPos1.z + 0.5D, entity);
        if ((d2 < this.mindmg.getValDouble() && (((EntityLivingBase)entity).getHealth() + ((EntityLivingBase)entity).getAbsorptionAmount()) > 8.0D) || d2 <= this.damage)
          continue; 
        double d3 = calculateDamage(blockPos1.x + 0.5D, (blockPos1.y + 1), blockPos1.z + 0.5D, (Entity)mc.player);
        if (this.antisui.getValBoolean() && ((mc.player.getHealth() + mc.player.getAbsorptionAmount()) - d3 <= 7.0D || d3 > d2))
          continue; 
        this.target = entity;
        this.damage = d2;
        blockPos = blockPos1;
        entity1 = entity;
        entity2 = entity;
      } 
    } 
    if (this.damage == 0.5D) {
      this.render = null;
      resetRotation();
      return;
    } 
    this.render = blockPos;
    if (this.place.getValBoolean()) {
      EnumFacing enumFacing;
      this.placeTick++;
      if (!bool && mc.player.inventory.currentItem != b) {
        if (this.autoswitch.getValBoolean()) {
          mc.player.inventory.currentItem = b;
          resetRotation();
          this.switchCooldown = true;
        } 
        return;
      } 
      lookAtPacket(blockPos.x + 0.5D, blockPos.y - 0.5D, blockPos.z + 0.5D, (EntityPlayer)mc.player);
      RayTraceResult rayTraceResult = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.x + 0.5D, blockPos.y - 0.5D, blockPos.z + 0.5D));
      if (rayTraceResult == null || rayTraceResult.sideHit == null) {
        enumFacing = EnumFacing.UP;
      } else {
        enumFacing = rayTraceResult.sideHit;
      } 
      if (this.switchCooldown) {
        this.switchCooldown = false;
        return;
      } 
      if (this.placeTick >= this.placedelay.getValDouble()) {
        mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(blockPos, enumFacing, bool ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
        this.placeTick = 0;
        this.antiStuckSystemTime = System.nanoTime() / 1000000L;
      } 
      if (spoofing)
        if (togglePitch) {
          EntityPlayerSP entityPlayerSP = mc.player;
          entityPlayerSP.rotationPitch += 4.0E-4F;
          togglePitch = false;
        } else {
          EntityPlayerSP entityPlayerSP = mc.player;
          entityPlayerSP.rotationPitch -= 4.0E-4F;
          togglePitch = true;
        }  
    } 
  }
  
  public void onEnable() {
    if (this.alerts.getValBoolean())
      Messages.sendChatMessage("&9RewriteCrystal &aenabled."); 
    this.placeTick = 0;
  }
  
  private void lookAtPacket(double paramDouble1, double paramDouble2, double paramDouble3, EntityPlayer paramEntityPlayer) {
    double[] arrayOfDouble = calculateLookAt(paramDouble1, paramDouble2, paramDouble3, paramEntityPlayer);
    setYawAndPitch((float)arrayOfDouble[0], (float)arrayOfDouble[1]);
  }
  
  public RewriteCrystal() {
    super("RewriteCrystal", 0, Category.COMBAT);
    this.sendPacket = new Listener(paramSend -> {
          if (paramSend.getPacket() instanceof CPacketPlayer && spoofing) {
            ((CPacketPlayer)paramSend.getPacket()).yaw = yaw;
            ((CPacketPlayer)paramSend.getPacket()).pitch = pitch;
          } 
          if (paramSend.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect sPacketSoundEffect = (SPacketSoundEffect)paramSend.getPacket();
            if (sPacketSoundEffect.getCategory() == SoundCategory.BLOCKS && sPacketSoundEffect.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE)
              for (Entity entity : new ArrayList(mc.world.loadedEntityList)) {
                if (entity instanceof EntityEnderCrystal && entity.getDistance(sPacketSoundEffect.getX(), sPacketSoundEffect.getY(), sPacketSoundEffect.getZ()) <= 6.0D)
                  entity.setDead(); 
              }  
          } 
        }new java.util.function.Predicate[0]);
    this.rec = new Listener(paramReceive -> {
          if (paramReceive.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect sPacketSoundEffect = (SPacketSoundEffect)paramReceive.getPacket();
            if (sPacketSoundEffect.getCategory() == SoundCategory.BLOCKS && sPacketSoundEffect.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE)
              for (Entity entity : new ArrayList(mc.world.loadedEntityList)) {
                if (entity instanceof EntityEnderCrystal && entity.getDistance(sPacketSoundEffect.getX(), sPacketSoundEffect.getY(), sPacketSoundEffect.getZ()) <= 6.0D)
                  entity.setDead(); 
              }  
          } 
        }new java.util.function.Predicate[0]);
    this.place = new Setting("Place", this, true);
    this.placedelay = new Setting("PlaceDelay", this, 2.0D, 1.0D, 40.0D, true);
    this.placerange = new Setting("PlaceRange", this, 6.0D, 1.0D, 6.0D, true);
    this.destroy = new Setting("Destroy", this, true);
    this.breakspeed = new Setting("BreakSpeed", this, 16.0D, 1.0D, 40.0D, true);
    this.breakrange = new Setting("BreakRange", this, 6.0D, 1.0D, 6.0D, true);
    this.range = new Setting("Range", this, 6.0D, 1.0D, 15.0D, true);
    this.mindmg = new Setting("MinDMG", this, 6.0D, 1.0D, 20.0D, true);
    this.autoswitch = new Setting("AutoSwitch", this, true);
    this.antistuck = new Setting("AntiStuck", this, true);
    this.antisui = new Setting("AntiSui", this, true);
    this.alerts = new Setting("Alerts", this, true);
    this.opacity = new Setting("Opacity", this, 35.0D, 35.0D, 255.0D, true);
    this.rendermodes.add("Full");
    this.rendermodes.add("Up");
    this.rendermodes.add("Outline");
    this.rendermode = new Setting("Mode", this, "Full", this.rendermodes);
    this.width = new Setting("Width", this, 1.0D, 1.0D, 15.0D, false);
  }
  
  public static float getBlastReduction(EntityLivingBase paramEntityLivingBase, float paramFloat, Explosion paramExplosion) {
    if (paramEntityLivingBase instanceof EntityPlayer) {
      EntityPlayer entityPlayer = (EntityPlayer)paramEntityLivingBase;
      DamageSource damageSource = DamageSource.causeExplosionDamage(paramExplosion);
      paramFloat = CombatRules.getDamageAfterAbsorb(paramFloat, entityPlayer.getTotalArmorValue(), (float)entityPlayer.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
      int i = EnchantmentHelper.getEnchantmentModifierDamage(entityPlayer.getArmorInventoryList(), damageSource);
      float f = MathHelper.clamp(i, 0.0F, 20.0F);
      paramFloat *= 1.0F - f / 25.0F;
      if (paramEntityLivingBase.isPotionActive(Potion.getPotionById(11)))
        paramFloat -= paramFloat / 4.0F; 
      return paramFloat;
    } 
    return CombatRules.getDamageAfterAbsorb(paramFloat, paramEntityLivingBase.getTotalArmorValue(), (float)paramEntityLivingBase.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
  }
  
  public static float calculateDamage(double paramDouble1, double paramDouble2, double paramDouble3, Entity paramEntity) {
    float f1 = 12.0F;
    double d1 = paramEntity.getDistance(paramDouble1, paramDouble2, paramDouble3) / 12.0D;
    Vec3d vec3d = new Vec3d(paramDouble1, paramDouble2, paramDouble3);
    double d2 = paramEntity.world.getBlockDensity(vec3d, paramEntity.getEntityBoundingBox());
    double d3 = (1.0D - d1) * d2;
    float f2 = (int)((d3 * d3 + d3) / 2.0D * 7.0D * 12.0D + 1.0D);
    double d4 = 1.0D;
    if (paramEntity instanceof EntityLivingBase)
      d4 = getBlastReduction((EntityLivingBase)paramEntity, getDamageMultiplied(f2), new Explosion((World)mc.world, (Entity)null, paramDouble1, paramDouble2, paramDouble3, 6.0F, false, true)); 
    return (float)d4;
  }
  
  private boolean canPlaceCrystal(BlockPos paramBlockPos) {
    BlockPos blockPos1 = paramBlockPos.add(0, 1, 0);
    BlockPos blockPos2 = paramBlockPos.add(0, 2, 0);
    return ((mc.world.getBlockState(paramBlockPos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(paramBlockPos).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(blockPos1).getBlock() == Blocks.AIR && mc.world.getBlockState(blockPos2).getBlock() == Blocks.AIR && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos1)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos2)).isEmpty());
  }
  
  private List<BlockPos> findCrystalBlocks() {
    NonNullList nonNullList = NonNullList.create();
    nonNullList.addAll((Collection)getSphere(getPlayerPos(), (float)this.placerange.getValDouble(), (int)this.placerange.getValDouble(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
    return (List<BlockPos>)nonNullList;
  }
  
  private int findAntiWeak() {
    byte b = -1;
    for (byte b1 = 0; b1 < 9; b1++) {
      ItemStack itemStack = mc.player.inventory.getStackInSlot(b1);
      if (itemStack != ItemStack.EMPTY) {
        if (itemStack.getItem().equals(Items.DIAMOND_SWORD)) {
          b = b1;
          break;
        } 
        if (itemStack.getItem().equals(Items.DIAMOND_PICKAXE)) {
          b = b1;
          break;
        } 
      } 
    } 
    return b;
  }
  
  public void onDisable() {
    this.render = null;
    if (this.alerts.getValBoolean())
      Messages.sendChatMessage("&9RewriteCrystal &cdisabled."); 
    resetRotation();
  }
  
  public static List<BlockPos> getSphere(BlockPos paramBlockPos, float paramFloat, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2) {
    ArrayList<BlockPos> arrayList = new ArrayList();
    int i = paramBlockPos.getX();
    int j = paramBlockPos.getY();
    int k = paramBlockPos.getZ();
    for (int m = i - (int)paramFloat; m <= i + paramFloat; m++) {
      int n = k - (int)paramFloat;
      while (n <= k + paramFloat) {
        int i1 = paramBoolean2 ? (j - (int)paramFloat) : j;
        while (true) {
          if (i1 < (paramBoolean2 ? (j + paramFloat) : (j + paramInt1))) {
            double d = ((i - m) * (i - m) + (k - n) * (k - n) + (paramBoolean2 ? ((j - i1) * (j - i1)) : 0));
            if (d < (paramFloat * paramFloat) && (!paramBoolean1 || d >= ((paramFloat - 1.0F) * (paramFloat - 1.0F)))) {
              BlockPos blockPos = new BlockPos(m, i1 + paramInt2, n);
              arrayList.add(blockPos);
            } 
            i1++;
            continue;
          } 
          n++;
        } 
      } 
    } 
    return arrayList;
  }
  
  public void onWorld(RenderEvent paramRenderEvent) {
    if (this.render != null) {
      float[] arrayOfFloat = { (float)(System.currentTimeMillis() % 11520L) / 11520.0F };
      int i = Color.HSBtoRGB(arrayOfFloat[0], 1.0F, 1.0F);
      int j = i >> 16 & 0xFF;
      int k = i >> 8 & 0xFF;
      int m = i & 0xFF;
      if (this.rendermode.getValString().equalsIgnoreCase("Up")) {
        ICERenderer.prepare(7);
        ICERenderer.drawBoxOpacity(this.render, i, (int)this.opacity.getValDouble(), 2);
        ICERenderer.release();
      } else if (this.rendermode.getValString().equalsIgnoreCase("Full")) {
        ICERenderer.prepare(7);
        ICERenderer.drawBox(this.render, j, k, m, 77, 63);
        ICERenderer.release();
        ICERenderer.prepare(7);
        ICERenderer.drawBoundingBoxBlockPos(this.render, 1.0F, j, k, m, (int)this.opacity.getValDouble());
        ICERenderer.release();
      } else if (this.rendermode.getValString().equalsIgnoreCase("Outline")) {
        ICERenderer.prepare(7);
        ICERenderer.drawBoundingBoxBlockPos(this.render, (float)this.width.getValDouble(), j, k, m, (int)this.opacity.getValDouble());
        ICERenderer.release();
      } 
    } 
  }
  
  private static void setYawAndPitch(float paramFloat1, float paramFloat2) {
    yaw = paramFloat1;
    pitch = paramFloat2;
    spoofing = true;
  }
  
  public static double[] calculateLookAt(double paramDouble1, double paramDouble2, double paramDouble3, EntityPlayer paramEntityPlayer) {
    double d1 = paramEntityPlayer.posX - paramDouble1;
    double d2 = paramEntityPlayer.posY - paramDouble2;
    double d3 = paramEntityPlayer.posZ - paramDouble3;
    double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
    d1 /= d4;
    d2 /= d4;
    d3 /= d4;
    double d5 = Math.asin(d2);
    double d6 = Math.atan2(d3, d1);
    d5 = d5 * 180.0D / Math.PI;
    d6 = d6 * 180.0D / Math.PI;
    d6 += 90.0D;
    return new double[] { d6, d5 };
  }
  
  public static float calculateDamage(EntityEnderCrystal paramEntityEnderCrystal, Entity paramEntity) {
    return calculateDamage(paramEntityEnderCrystal.posX, paramEntityEnderCrystal.posY, paramEntityEnderCrystal.posZ, paramEntity);
  }
  
  private static float getDamageMultiplied(float paramFloat) {
    int i = mc.world.getDifficulty().getId();
    return paramFloat * ((i == 0) ? 0.0F : ((i == 2) ? 1.0F : ((i == 1) ? 0.5F : 1.5F)));
  }
  
  public static BlockPos getPlayerPos() {
    return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
  }
  
  public static boolean canBlockBeSeen(BlockPos paramBlockPos) {
    return (mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(paramBlockPos.getX(), paramBlockPos.getY(), paramBlockPos.getZ()), false, true, false) == null);
  }
  
  private static final class PlaceLocation extends Vec3i {
    private PlaceLocation(int param1Int1, int param1Int2, int param1Int3) {
      super(param1Int1, param1Int2, param1Int3);
    }
  }
}
