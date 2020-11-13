//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.combat;

import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;

public class TickAura extends Module {
  public void onEnable() {
    this.Dticks = 0;
  }
  
  public static boolean isPassive(Entity paramEntity) {
    return (paramEntity instanceof EntityWolf && ((EntityWolf)paramEntity).isAngry()) ? false : ((paramEntity instanceof net.minecraft.entity.passive.EntityAnimal || paramEntity instanceof net.minecraft.entity.EntityAgeable || paramEntity instanceof net.minecraft.entity.passive.EntityTameable || paramEntity instanceof net.minecraft.entity.passive.EntityAmbientCreature || paramEntity instanceof net.minecraft.entity.passive.EntitySquid) ? true : ((paramEntity instanceof EntityIronGolem && ((EntityIronGolem)paramEntity).getRevengeTarget() == null)));
  }
  
  public static boolean isHostileMob(Entity paramEntity) {
    return (paramEntity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(paramEntity));
  }
  
  public void onUpdate() {
    if (mc.player.isDead || mc.world == null)
      return; 
    this.Dticks++;
    if (this.ttkswitch.getValBoolean()) {
      boolean bool = false;
      byte b = -1;
      byte b1;
      for (b1 = 0; b1 < 9; b1++) {
        ItemStack itemStack = (ItemStack)mc.player.inventory.mainInventory.get(b1);
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= 32767) {
          b = b1;
          this.foundsword = true;
        } 
        if (!this.foundsword) {
          b = -1;
          this.foundsword = false;
        } 
      } 
      if (b != -1 && mc.player.inventory.currentItem != b) {
        mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(b));
        mc.player.inventory.currentItem = b;
        mc.playerController.updateController();
      } 
      if (b == -1 && mc.player.openContainer != null && mc.player.openContainer instanceof net.minecraft.inventory.ContainerHopper && mc.player.openContainer.inventorySlots != null && !mc.player.openContainer.inventorySlots.isEmpty()) {
        for (b1 = 0; b1 < 5; b1++) {
          if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, ((Slot)mc.player.openContainer.inventorySlots.get(0)).inventory.getStackInSlot(b1)) >= 32767) {
            b = b1;
            break;
          } 
        } 
        if (b == -1)
          return; 
        if (b != -1)
          for (b1 = 0; b1 < 9; b1++) {
            ItemStack itemStack = (ItemStack)mc.player.inventory.mainInventory.get(b1);
            if (itemStack.getItem() instanceof net.minecraft.item.ItemAir) {
              if (mc.player.inventory.currentItem != b1) {
                mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(b1));
                mc.player.inventory.currentItem = b1;
                mc.playerController.updateController();
              } 
              bool = true;
              break;
            } 
          }  
        if (bool || checkEnchants())
          mc.playerController.windowClick(mc.player.openContainer.windowId, b, mc.player.inventory.currentItem, ClickType.SWAP, (EntityPlayer)mc.player); 
      } 
    } 
    for (Entity entity : mc.world.loadedEntityList) {
      boolean bool = false;
      if (!(entity instanceof EntityLivingBase) || entity == mc.player || mc.player.getDistance(entity) > this.range.getValDouble() || ((EntityLivingBase)entity).getHealth() <= 0.0F || (!isTTK(mc.player.inventory.getCurrentItem()) && this.onlyttk.getValBoolean()) || (entity instanceof EntityPlayer && Friends.isFriend(entity.getName())))
        continue; 
      isSpoofingAngles = false;
      if (this.Dticks >= (int)this.delay.getValDouble()) {
        if (entity instanceof EntityPlayer && this.players.getValBoolean()) {
          if (this.rotate.getValBoolean()) {
            isSpoofingAngles = true;
            lookAtPacket(entity.posX, entity.posY, entity.posZ, (EntityPlayer)mc.player);
          } 
          mc.playerController.attackEntity((EntityPlayer)mc.player, entity);
          mc.player.swingArm(EnumHand.MAIN_HAND);
          this.Dticks = 0;
          bool = true;
          return;
        } 
        if (isPassive(entity) ? this.animals.getValBoolean() : (isMobAggressive(entity) && this.mobs.getValBoolean())) {
          if (this.rotate.getValBoolean()) {
            isSpoofingAngles = true;
            lookAtPacket(entity.posX, entity.posY, entity.posZ, (EntityPlayer)mc.player);
          } 
          mc.playerController.attackEntity((EntityPlayer)mc.player, entity);
          mc.player.swingArm(EnumHand.MAIN_HAND);
          this.Dticks = 0;
          bool = false;
          return;
        } 
      } 
    } 
  }
  
  public static boolean isMobAggressive(Entity paramEntity) {
    if (paramEntity instanceof EntityPigZombie) {
      if (((EntityPigZombie)paramEntity).isArmsRaised() || ((EntityPigZombie)paramEntity).isAngry())
        return true; 
    } else {
      if (paramEntity instanceof EntityWolf)
        return (((EntityWolf)paramEntity).isAngry() && !mc.player.equals(((EntityWolf)paramEntity).getOwner())); 
      if (paramEntity instanceof EntityEnderman)
        return ((EntityEnderman)paramEntity).isScreaming(); 
    } 
    return isHostileMob(paramEntity);
  }
  
  public void onDisable() {
    this.Dticks = 0;
    resetRotation();
  }
  
  private void lookAtPacket(double paramDouble1, double paramDouble2, double paramDouble3, EntityPlayer paramEntityPlayer) {
    double[] arrayOfDouble = calculateLookAt(paramDouble1, paramDouble2, paramDouble3, paramEntityPlayer);
    setYawAndPitch((float)arrayOfDouble[0], (float)arrayOfDouble[1]);
  }
  
  private static void resetRotation() {
    if (isSpoofingAngles) {
      yaw = mc.player.rotationYaw;
      pitch = mc.player.rotationPitch;
      isSpoofingAngles = false;
    } 
  }
  
  public boolean checkEnchants() {
    return (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, mc.player.inventory.getCurrentItem()) == Short.valueOf((short)5).shortValue() || mc.player.inventory.getCurrentItem().getItem() instanceof net.minecraft.item.ItemPickaxe);
  }
  
  public TickAura() {
    super("TickAura", 0, Category.COMBAT);
    this.packetEvent = new Listener(paramSend -> {
          if (paramSend.getPacket() instanceof CPacketPlayer && isSpoofingAngles) {
            ((CPacketPlayer)paramSend.getPacket()).yaw = yaw;
            ((CPacketPlayer)paramSend.getPacket()).pitch = pitch;
            mc.player.setRotationYawHead(yaw);
          } 
        }new java.util.function.Predicate[0]);
    this.players = new Setting("Players", this, true);
    this.mobs = new Setting("Mob", this, true);
    this.animals = new Setting("Animals", this, false);
    this.ttkswitch = new Setting("32kSwitch", this, true);
    this.range = new Setting("Range", this, 4.5D, 1.0D, 7.0D, false);
    this.delay = new Setting("TickDelay", this, 3.0D, 1.0D, 60.0D, true);
    this.onlyttk = new Setting("Only32k", this, true);
    this.rotate = new Setting("Rotate", this, true);
  }
  
  private static void setYawAndPitch(float paramFloat1, float paramFloat2) {
    yaw = paramFloat1;
    pitch = paramFloat2;
  }
  
  public static boolean isNeutralMob(Entity paramEntity) {
    return (paramEntity instanceof EntityPigZombie || paramEntity instanceof EntityWolf || paramEntity instanceof EntityEnderman);
  }
  
  public static boolean isFriendlyMob(Entity paramEntity) {
    return ((paramEntity.isCreatureType(EnumCreatureType.CREATURE, false) && !isNeutralMob(paramEntity)) || paramEntity.isCreatureType(EnumCreatureType.AMBIENT, false) || paramEntity instanceof net.minecraft.entity.passive.EntityVillager || paramEntity instanceof EntityIronGolem || (isNeutralMob(paramEntity) && !isMobAggressive(paramEntity)));
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
  
  public static boolean isTTK(ItemStack paramItemStack) {
    if (paramItemStack == null)
      return false; 
    if (paramItemStack.getTagCompound() == null)
      return false; 
    if (paramItemStack.getEnchantmentTagList().getTagType() == 0)
      return false; 
    NBTTagList nBTTagList = (NBTTagList)paramItemStack.getTagCompound().getTag("ench");
    for (byte b = 0; b < nBTTagList.tagCount(); b++) {
      NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(b);
      if (nBTTagCompound.getInteger("id") == 16) {
        if (nBTTagCompound.getInteger("lvl") >= 16)
          return true; 
        break;
      } 
    } 
    return false;
  }
}
