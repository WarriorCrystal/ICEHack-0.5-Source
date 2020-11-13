//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.misc;

import java.util.HashMap;
import java.util.Map;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class AutoReplenish extends Module {
  private static Map<Integer, ItemStack> getHotbar() {
    return getInventorySlots(36, 44);
  }
  
  private int findCompatibleInventorySlot(ItemStack paramItemStack) {
    int i = -1;
    int j = 999;
    for (Map.Entry<Integer, ItemStack> entry : getInventory().entrySet()) {
      ItemStack itemStack = (ItemStack)entry.getValue();
      if (itemStack.isEmpty || itemStack.getItem() == Items.AIR || !isCompatibleStacks(paramItemStack, itemStack))
        continue; 
      int k = ((ItemStack)mc.player.inventoryContainer.getInventory().get(((Integer)entry.getKey()).intValue())).stackSize;
      if (j > k) {
        j = k;
        i = ((Integer)entry.getKey()).intValue();
      } 
    } 
    return i;
  }
  
  private static Map<Integer, ItemStack> getInventory() {
    return getInventorySlots(9, 35);
  }
  
  public AutoReplenish() {
    super("AutoReplenish", 0, Category.COMBAT);
  }
  
  private Pair<Integer, Integer> findReplenishableHotbarSlot() {
    Pair<Integer, Integer> pair = null;
    for (Map.Entry<Integer, ItemStack> entry : getHotbar().entrySet()) {
      ItemStack itemStack = (ItemStack)entry.getValue();
      if (itemStack.isEmpty || itemStack.getItem() == Items.AIR || !itemStack.isStackable() || itemStack.stackSize >= itemStack.getMaxStackSize() || itemStack.stackSize > this.threshold.getValDouble())
        continue; 
      int i = findCompatibleInventorySlot(itemStack);
      if (i == -1)
        continue; 
      pair = new Pair(Integer.valueOf(i), entry.getKey());
    } 
    return pair;
  }
  
  private boolean isCompatibleStacks(ItemStack paramItemStack1, ItemStack paramItemStack2) {
    if (!paramItemStack1.getItem().equals(paramItemStack2.getItem()))
      return false; 
    if (paramItemStack1.getItem() instanceof ItemBlock && paramItemStack2.getItem() instanceof ItemBlock) {
      Block block1 = ((ItemBlock)paramItemStack1.getItem()).getBlock();
      Block block2 = ((ItemBlock)paramItemStack2.getItem()).getBlock();
      if (!block1.material.equals(block2.material))
        return false; 
    } 
    return !paramItemStack1.getDisplayName().equals(paramItemStack2.getDisplayName()) ? false : (!(paramItemStack1.getItemDamage() != paramItemStack2.getItemDamage()));
  }
  
  public static Map<Integer, ItemStack> getInventorySlots(int paramInt1, int paramInt2) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    while (paramInt1 <= paramInt2) {
      hashMap.put(Integer.valueOf(paramInt1), mc.player.inventoryContainer.getInventory().get(paramInt1));
      paramInt1++;
    } 
    return (Map)hashMap;
  }
  
  public void onUpdate() {
    if (mc.player == null)
      return; 
    if (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer)
      return; 
    if (this.delayStep < this.delay.getValDouble()) {
      this.delayStep++;
      return;
    } 
    this.delayStep = 0;
    Pair<Integer, Integer> pair = findReplenishableHotbarSlot();
    if (pair == null)
      return; 
    int i = ((Integer)pair.getKey()).intValue();
    int j = ((Integer)pair.getValue()).intValue();
    mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
    mc.playerController.windowClick(0, j, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
    mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
  }
}
