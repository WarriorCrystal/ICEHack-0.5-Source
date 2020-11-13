//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtil {
  public static int getItem(Item paramItem) {
    if (mc.player == null)
      return -1; 
    for (byte b = 0; b < mc.player.inventoryContainer.getInventory().size(); b++) {
      if (b != 0 && b != 5 && b != 6 && b != 7 && b != 8) {
        ItemStack itemStack = (ItemStack)mc.player.inventoryContainer.getInventory().get(b);
        if (!itemStack.isEmpty() && itemStack.getItem().equals(paramItem))
          return b; 
      } 
    } 
    return -1;
  }
}
