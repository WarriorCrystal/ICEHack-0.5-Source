//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.combat;

import java.util.ArrayList;
import me.fluffycq.icehack.message.Messages;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AutoTotem extends Module {
  public String getModInfo() {
    return (String.valueOf(this.totems) == null) ? "0" : (String.valueOf(this.totems).equalsIgnoreCase("") ? "0" : String.valueOf(this.totems));
  }
  
  public void onDisable() {
    this.smartTick = 0;
    this.isSmart = false;
  }
  
  public void switchOffhand(String paramString) {
    Item item = null;
    item = getItemVal(paramString);
    int i = InventoryUtil.getItem(item);
    if (i != -1) {
      if (this.messages.getValBoolean())
        Messages.sendChatMessage(String.valueOf((new StringBuilder()).append("AutoTotem equipped you with a ").append(paramString))); 
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.updateController();
    } else {
      int j = InventoryUtil.getItem(Items.TOTEM_OF_UNDYING);
      if (j != -1) {
        if (this.messages.getValBoolean())
          Messages.sendChatMessage("Fallback Emergency Totem equipped!"); 
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, j, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, j, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
        mc.playerController.updateController();
      } 
    } 
  }
  
  public void onEnable() {
    this.smartTick = 0;
    this.isSmart = false;
  }
  
  public double getCrystalDMG() {
    boolean bool = false;
    double d = 0.0D;
    for (Entity entity : mc.world.loadedEntityList) {
      if (!(entity instanceof EntityEnderCrystal))
        continue; 
      EntityEnderCrystal entityEnderCrystal = (EntityEnderCrystal)entity;
      if (entityEnderCrystal != null) {
        d = AutoCrystal.calculateDamage(entityEnderCrystal.posX + 0.5D, entityEnderCrystal.posY + 1.0D, entityEnderCrystal.posZ + 0.5D, (Entity)mc.player);
        if (AutoCrystal.calculateDamage(entityEnderCrystal.posX + 0.5D, entityEnderCrystal.posY + 1.0D, entityEnderCrystal.posZ + 0.5D, (Entity)mc.player) >= mc.player.getHealth() + mc.player.getAbsorptionAmount() && !bool)
          bool = true; 
      } 
    } 
    return d;
  }
  
  public Item getItemVal(String paramString) {
    Item item = null;
    switch (paramString) {
      case "Totem":
        item = Items.TOTEM_OF_UNDYING;
        break;
      case "Crystal":
        item = Items.END_CRYSTAL;
        break;
      case "Gapple":
        item = Items.GOLDEN_APPLE;
        break;
      case "Sword":
        item = Items.DIAMOND_SWORD;
        break;
      case "Pickaxe":
        item = Items.DIAMOND_PICKAXE;
        break;
    } 
    return item;
  }
  
  public void onUpdate() {
    if (mc.currentScreen != null && !(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory))
      return; 
    this.totems = mc.player.inventory.mainInventory.stream().filter(paramItemStack -> (paramItemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum();
    if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
      this.totems++; 
    this.modInfo = (String.valueOf(this.totems) != null) ? String.valueOf(this.totems) : "0";
    if (InventoryUtil.getItem(getItemVal(this.priority.getValString())) == -1) {
      if (InventoryUtil.getItem(Items.TOTEM_OF_UNDYING) != -1 && !mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING))
        switchOffhand("Totem"); 
    } else if ((mc.player.getHealth() + mc.player.getAbsorptionAmount()) >= this.health.getValDouble() && !mc.player.getHeldItemOffhand().getItem().equals(getItemVal(this.priority.getValString()))) {
      if (this.allowGap.getValBoolean()) {
        if (!mc.player.getHeldItemOffhand().getItem().equals(Items.GOLDEN_APPLE))
          switchOffhand(this.priority.getValString()); 
      } else {
        switchOffhand(this.priority.getValString());
      } 
    } 
    if ((mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= this.health.getValDouble() && !mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING))
      switchOffhand("Totem"); 
  }
  
  public boolean crystalsWillKill() {
    boolean bool = false;
    for (Entity entity : mc.world.loadedEntityList) {
      if (!(entity instanceof EntityEnderCrystal))
        continue; 
      EntityEnderCrystal entityEnderCrystal = (EntityEnderCrystal)entity;
      if (entityEnderCrystal != null && AutoCrystal.calculateDamage(entityEnderCrystal.posX + 0.5D, entityEnderCrystal.posY + 1.0D, entityEnderCrystal.posZ + 0.5D, (Entity)mc.player) >= mc.player.getHealth() + mc.player.getAbsorptionAmount() && !bool)
        bool = true; 
    } 
    return bool;
  }
  
  public AutoTotem() {
    super("AutoTotem", 0, Category.COMBAT);
    this.priorities.add("Totem");
    this.priorities.add("Crystal");
    this.priorities.add("Gapple");
    this.priorities.add("Sword");
    this.priorities.add("Pickaxe");
    this.priority = new Setting("Priority", this, "Totem", this.priorities);
    this.allowGap = new Setting("AllowGap", this, true);
    this.messages = new Setting("Alerts", this, false);
    this.debug = new Setting("Debug", this, false);
  }
}
