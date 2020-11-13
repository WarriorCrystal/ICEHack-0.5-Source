//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.render;

import java.util.ArrayList;
import java.util.Collection;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.events.Render3DEvent;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.MathUtil;
import me.fluffycq.icehack.util.RenderMethods;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class Nametags extends Module {
  private void renderEnchantmentText(ItemStack paramItemStack, int paramInt1, int paramInt2) {
    int i = paramInt2;
    if ((paramItemStack.getItem() instanceof net.minecraft.item.ItemArmor || paramItemStack.getItem() instanceof net.minecraft.item.ItemSword || paramItemStack.getItem() instanceof net.minecraft.item.ItemTool || paramItemStack.getItem() instanceof net.minecraft.item.ItemElytra) && this.durability.getValBoolean()) {
      float f = (paramItemStack.getMaxDamage() - paramItemStack.getItemDamage()) / paramItemStack.getMaxDamage() * 100.0F;
      int j = (int)Math.min(f, 100.0F);
      mc.fontRenderer.drawStringWithShadow(String.valueOf((new StringBuilder()).append(String.valueOf(j)).append("%")), (paramInt1 * 2), (paramInt2 - 10), paramItemStack.getItem().getRGBDurabilityForDisplay(paramItemStack));
    } 
    if (paramItemStack.getItem() != null && !(paramItemStack.getItem() instanceof net.minecraft.item.ItemAir)) {
      NBTTagList nBTTagList = paramItemStack.getEnchantmentTagList();
      if (nBTTagList != null)
        for (byte b = 0; b < nBTTagList.tagCount(); b++) {
          short s1 = nBTTagList.getCompoundTagAt(b).getShort("id");
          short s2 = nBTTagList.getCompoundTagAt(b).getShort("lvl");
          Enchantment enchantment = Enchantment.getEnchantmentByID(s1);
          if (enchantment != null) {
            String str = enchantment.isCurse() ? enchantment.getTranslatedName(s2).substring(11).substring(0, 1).toLowerCase() : enchantment.getTranslatedName(s2).substring(0, 3).toLowerCase();
            if (!String.valueOf(s2).equalsIgnoreCase("1") && !enchantment.isCurse()) {
              str = String.valueOf((new StringBuilder()).append(enchantment.getTranslatedName(s2).substring(0, 2).toLowerCase()).append(String.valueOf(s2)));
            } else if (String.valueOf(s2).equalsIgnoreCase("1") && !enchantment.isCurse()) {
              str = enchantment.getTranslatedName(s2).substring(0, 3).toLowerCase();
            } 
            if (enchantment.isCurse())
              str = "Van"; 
            str = String.valueOf((new StringBuilder()).append(str.substring(0, 1).toUpperCase()).append(str.substring(1)));
            mc.fontRenderer.drawString(str, paramInt1 * 2, i, -1);
            i += 8;
          } 
        }  
    } 
    if (paramItemStack.getItem() == Items.GOLDEN_APPLE && paramItemStack.hasEffect())
      GuiUtil.drawString("God", paramInt1 * 2, i, -6416384); 
  }
  
  public int getPing(EntityPlayer paramEntityPlayer) {
    int i = 0;
    try {
      i = (int)MathUtil.clamp(mc.getConnection().getPlayerInfo(paramEntityPlayer.getUniqueID()).getResponseTime(), 0.0F, 300.0F);
    } catch (NullPointerException nullPointerException) {}
    return i;
  }
  
  public String getGMText(EntityPlayer paramEntityPlayer) {
    return paramEntityPlayer.isCreative() ? "C" : (paramEntityPlayer.isSpectator() ? "I" : ((!paramEntityPlayer.isAllowEdit() && !paramEntityPlayer.isSpectator()) ? "A" : ((!paramEntityPlayer.isCreative() && !paramEntityPlayer.isSpectator() && paramEntityPlayer.isAllowEdit()) ? "S" : "")));
  }
  
  private void renderItemStack(ArrayList<ItemStack> paramArrayList, ItemStack paramItemStack, int paramInt1, int paramInt2) {
    int i = 2 - getEnchantSpace(paramArrayList) - 24;
    int j = 2 - getEnchantSpace(paramArrayList) / 2 - 14;
    if (j >= -26)
      j = -26; 
    if (i >= -50)
      i = -50; 
    GlStateManager.pushMatrix();
    GlStateManager.depthMask(true);
    GlStateManager.clear(256);
    RenderHelper.enableStandardItemLighting();
    (mc.getRenderItem()).zLevel = -150.0F;
    GlStateManager.disableLighting();
    GlStateManager.disableDepth();
    GlStateManager.disableBlend();
    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
    GlStateManager.disableLighting();
    GlStateManager.disableDepth();
    GlStateManager.disableAlpha();
    GlStateManager.disableAlpha();
    GlStateManager.disableBlend();
    GlStateManager.enableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableAlpha();
    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
    mc.getRenderItem().renderItemAndEffectIntoGUI(paramItemStack, paramInt1, j);
    mc.getRenderItem().renderItemOverlays(mc.fontRenderer, paramItemStack, paramInt1, j);
    (mc.getRenderItem()).zLevel = 0.0F;
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableCull();
    GlStateManager.enableAlpha();
    GlStateManager.disableBlend();
    GlStateManager.disableLighting();
    GlStateManager.scale(0.5F, 0.5F, 0.5F);
    GlStateManager.disableDepth();
    renderEnchantmentText(paramItemStack, paramInt1, i);
    GlStateManager.enableDepth();
    GlStateManager.scale(2.0F, 2.0F, 2.0F);
    GlStateManager.popMatrix();
  }
  
  private void renderNameTag(EntityPlayer paramEntityPlayer, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat) {
    double d1 = paramDouble2;
    d1 += paramEntityPlayer.isSneaking() ? 0.5D : 0.7D;
    Entity entity = mc.getRenderViewEntity();
    double d2 = entity.posX;
    double d3 = entity.posY;
    double d4 = entity.posZ;
    entity.posX = interpolate(entity.prevPosX, entity.posX, paramFloat);
    entity.posY = interpolate(entity.prevPosY, entity.posY, paramFloat);
    entity.posZ = interpolate(entity.prevPosZ, entity.posZ, paramFloat);
    double d5 = entity.getDistance(paramDouble1 + (mc.getRenderManager()).viewerPosX, paramDouble2 + (mc.getRenderManager()).viewerPosY, paramDouble3 + (mc.getRenderManager()).viewerPosZ);
    int i = mc.fontRenderer.getStringWidth(getDisplayName(paramEntityPlayer)) / 2;
    double d6 = 0.0018D + this.scaling.getValDouble() * 0.001D * d5;
    if (d5 <= 8.0D)
      d6 = 0.0245D; 
    GlStateManager.pushMatrix();
    RenderHelper.enableStandardItemLighting();
    GlStateManager.enablePolygonOffset();
    GlStateManager.doPolygonOffset(1.0F, -1500000.0F);
    GlStateManager.disableLighting();
    GlStateManager.translate((float)paramDouble1, (float)d1 + 1.4F, (float)paramDouble3);
    GlStateManager.rotate(-(mc.getRenderManager()).playerViewY, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate((mc.getRenderManager()).playerViewX, (mc.gameSettings.thirdPersonView == 2) ? -1.0F : 1.0F, 0.0F, 0.0F);
    GlStateManager.scale(-d6, -d6, d6);
    GlStateManager.disableDepth();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    RenderMethods.drawBorderedRectReliant((-i - 2), -(mc.fontRenderer.FONT_HEIGHT + 1), i + 2.0F, 1.5F, 1.6F, 1996488704, 1426063360);
    GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
    GL11.glColor4f(1.0F, 10.0F, 1.0F, 1.0F);
    mc.fontRenderer.drawStringWithShadow(getDisplayName(paramEntityPlayer), -i, -(mc.fontRenderer.FONT_HEIGHT - 1), getDisplayColour(paramEntityPlayer));
    GlStateManager.glNormal3f(0.0F, 0.0F, 0.0F);
    if (this.armor.getValBoolean()) {
      GlStateManager.pushMatrix();
      byte b = 0;
      if ((paramEntityPlayer.getHeldItemMainhand().getItem() != null && paramEntityPlayer.getHeldItemOffhand().getItem() == null) || (paramEntityPlayer.getHeldItemMainhand().getItem() == null && paramEntityPlayer.getHeldItemOffhand().getItem() != null)) {
        b = -4;
      } else if (paramEntityPlayer.getHeldItemMainhand().getItem() != null && paramEntityPlayer.getHeldItemOffhand().getItem() != null) {
        b = -8;
      } 
      int j;
      for (j = 3; j >= 0; j--) {
        ItemStack itemStack = (ItemStack)paramEntityPlayer.inventory.armorInventory.get(j);
        if (itemStack != null && itemStack.getItem() != Items.AIR)
          b -= 8; 
      } 
      ArrayList<ItemStack> arrayList1 = new ArrayList();
      if (paramEntityPlayer.inventory.armorInventory != null)
        for (ItemStack itemStack : paramEntityPlayer.inventory.armorInventory) {
          if (itemStack != null && !itemStack.getItem().equals(Items.AIR))
            arrayList1.add(itemStack); 
        }  
      ArrayList<ItemStack> arrayList2 = new ArrayList();
      arrayList2.addAll((Collection)paramEntityPlayer.inventory.armorInventory);
      if (paramEntityPlayer.getHeldItemMainhand() != null)
        arrayList2.add(paramEntityPlayer.getHeldItemMainhand().copy()); 
      if (paramEntityPlayer.getHeldItemOffhand() != null)
        arrayList2.add(paramEntityPlayer.getHeldItemOffhand().copy()); 
      if (paramEntityPlayer.getHeldItemMainhand() != null) {
        b -= 8;
        ItemStack itemStack = paramEntityPlayer.getHeldItemMainhand().copy();
        if (!itemStack.getItem().equals(Items.AIR)) {
          renderItemStack(arrayList2, itemStack, b, -(getEnchantSpace(arrayList2) + 26) + 26 + 10);
          if (arrayList1.isEmpty()) {
            b += 22;
          } else {
            b += 16;
          } 
        } 
      } 
      for (j = arrayList1.size() - 1; j >= 0; j--) {
        ItemStack itemStack = arrayList1.get(j);
        if (itemStack != null) {
          ItemStack itemStack1 = itemStack.copy();
          if (!itemStack1.getItem().equals(Items.AIR)) {
            if (itemStack1.getItem() instanceof net.minecraft.item.ItemTool || itemStack1.getItem() instanceof net.minecraft.item.ItemArmor || itemStack1.getItem().equals(Items.ELYTRA))
              renderItemStack(arrayList2, itemStack1, b, -(getEnchantSpace(arrayList2) + 26) + 26 + 10); 
            if (arrayList1.get(0) == itemStack) {
              b += 24;
            } else {
              b += 16;
            } 
          } 
        } 
      } 
      if (paramEntityPlayer.getHeldItemOffhand() != null) {
        b -= 8;
        ItemStack itemStack = paramEntityPlayer.getHeldItemOffhand().copy();
        if (!itemStack.getItem().equals(Items.AIR)) {
          renderItemStack(arrayList2, itemStack, b, -(getEnchantSpace(arrayList2) + 26) + 26 + 10);
          b += 16;
        } 
      } 
      GlStateManager.popMatrix();
    } 
    entity.posX = d2;
    entity.posY = d3;
    entity.posZ = d4;
    GlStateManager.enableDepth();
    GlStateManager.enableLighting();
    GlStateManager.disableBlend();
    GlStateManager.enableLighting();
    GlStateManager.disablePolygonOffset();
    GlStateManager.doPolygonOffset(1.0F, 1500000.0F);
    GlStateManager.popMatrix();
  }
  
  private double interpolate(double paramDouble1, double paramDouble2, float paramFloat) {
    return paramDouble1 + (paramDouble2 - paramDouble1) * paramFloat;
  }
  
  public int getHighestEncY(ArrayList<ItemStack> paramArrayList) {
    return getEnchantSpace(paramArrayList);
  }
  
  private int getDisplayColour(EntityPlayer paramEntityPlayer) {
    int i = -1;
    if (Friends.isFriend(paramEntityPlayer.getName()))
      return -11141121; 
    if (paramEntityPlayer.isInvisible()) {
      i = -3593216;
    } else if (paramEntityPlayer.isSneaking()) {
      i = -3593216;
    } 
    return i;
  }
  
  public Nametags() {
    super("NameTags", 0, Category.RENDER);
    this.renderEvent = new Listener(paramRender3DEvent -> {
          for (Entity entity1 : mc.world.playerEntities) {
            Entity entity2 = entity1;
            if (entity2 instanceof EntityPlayer && entity2.isEntityAlive()) {
              double d1 = interpolate(entity2.lastTickPosX, entity2.posX, paramRender3DEvent.getPartialTicks()) - (mc.getRenderManager()).renderPosX;
              double d2 = interpolate(entity2.lastTickPosY, entity2.posY, paramRender3DEvent.getPartialTicks()) - (mc.getRenderManager()).renderPosY;
              double d3 = interpolate(entity2.lastTickPosZ, entity2.posZ, paramRender3DEvent.getPartialTicks()) - (mc.getRenderManager()).renderPosZ;
              if (!entity2.getName().equalsIgnoreCase(mc.player.getName()) || ICEHack.fevents.moduleManager.getModule("Freecam").isEnabled())
                renderNameTag((EntityPlayer)entity2, d1, d2, d3, paramRender3DEvent.getPartialTicks()); 
            } 
          } 
        }new java.util.function.Predicate[0]);
    this.armor = new Setting("Armor", this, true);
    this.health = new Setting("Health", this, true);
    this.ping = new Setting("Ping", this, true);
    this.gamemode = new Setting("Gamemode", this, true);
    this.durability = new Setting("Durability", this, true);
    this.itemname = new Setting("ItemName", this, true);
    this.scaling = new Setting("Scaling", this, 3.0D, 1.0D, 5.0D, true);
  }
  
  public int getEnchantSpace(ArrayList<ItemStack> paramArrayList) {
    int i = 0;
    for (ItemStack itemStack : paramArrayList) {
      NBTTagList nBTTagList = itemStack.getEnchantmentTagList();
      if (nBTTagList != null && nBTTagList.tagCount() > i)
        i = nBTTagList.tagCount(); 
    } 
    return i * 8;
  }
  
  private String getDisplayName(EntityPlayer paramEntityPlayer) {
    TextFormatting textFormatting;
    null = paramEntityPlayer.getDisplayName().getFormattedText();
    if (!this.health.getValBoolean())
      return null; 
    float f1 = paramEntityPlayer.getHealth() + paramEntityPlayer.getAbsorptionAmount();
    if (f1 <= 0.0F)
      f1 = 1.0F; 
    if (f1 > 18.0F) {
      textFormatting = TextFormatting.GREEN;
    } else if (f1 > 16.0F) {
      textFormatting = TextFormatting.DARK_GREEN;
    } else if (f1 > 12.0F) {
      textFormatting = TextFormatting.YELLOW;
    } else if (f1 > 8.0F) {
      textFormatting = TextFormatting.GOLD;
    } else if (f1 > 5.0F) {
      textFormatting = TextFormatting.RED;
    } else {
      textFormatting = TextFormatting.DARK_RED;
    } 
    String str1 = "";
    String str2 = "";
    if (this.gamemode.getValBoolean())
      str1 = String.valueOf((new StringBuilder()).append(str1).append(" [").append(getGMText(paramEntityPlayer)).append("]")); 
    if (this.ping.getValBoolean())
      str2 = String.valueOf((new StringBuilder()).append(str2).append(" ").append(String.valueOf(getPing(paramEntityPlayer))).append("ms")); 
    float f2 = paramEntityPlayer.getHealth() + paramEntityPlayer.getAbsorptionAmount();
    int i = (int)Math.ceil(f2);
    if (i <= 0)
      i = 1; 
    return String.valueOf((new StringBuilder()).append(null).append(str1).append(str2).append(textFormatting).append(" ").append(String.valueOf(i)));
  }
}
