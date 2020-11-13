//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module.modules.render;

import java.awt.Color;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.EntityUtil;
import me.fluffycq.icehack.util.RenderingMethods;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ESP extends Module {
  public void onDisable() {
    if (mc == null || mc.player == null || mc.world == null)
      return; 
    for (Entity entity : mc.world.loadedEntityList)
      entity.setGlowing(false); 
    mc.player.setGlowing(false);
    mc.renderGlobal.entityOutlineShader.listShaders.forEach(paramShader -> {
          ShaderUniform shaderUniform = paramShader.getShaderManager().getShaderUniform("Radius");
          if (shaderUniform != null)
            shaderUniform.set(2.0F); 
        });
  }
  
  public static int getEntityColor(Entity paramEntity) {
    int i = -43691;
    if (paramEntity instanceof EntityPlayer)
      return Friends.isFriend(((EntityPlayer)paramEntity).getName()) ? -11141121 : -43691; 
    if (EntityUtil.isHostileMob(paramEntity))
      return -43691; 
    if (EntityUtil.isFriendlyMob(paramEntity))
      return -11141291; 
    if (paramEntity instanceof net.minecraft.entity.item.EntityEnderCrystal)
      return -43691; 
    if (isVehicle(paramEntity))
      return -43691; 
    if (paramEntity instanceof net.minecraft.entity.item.EntityItem) {
      int j;
      if (ICEHack.setmgr.getSettingByMod("Rainbow", ICEHack.fevents.moduleManager.getModule("ClickGUI")).getValBoolean()) {
        j = Color.getHSBColor((float)(System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F).getRGB();
      } else {
        j = (new Color((int)ICEHack.setmgr.getSettingByName("Red").getValDouble(), (int)ICEHack.setmgr.getSettingByName("Green").getValDouble(), (int)ICEHack.setmgr.getSettingByName("Blue").getValDouble())).getRGB();
      } 
      return j;
    } 
    return i;
  }
  
  public static boolean isVehicle(Entity paramEntity) {
    return (paramEntity instanceof net.minecraft.entity.item.EntityBoat || paramEntity instanceof net.minecraft.entity.item.EntityMinecart);
  }
  
  public void drawEntity(EntityLivingBase paramEntityLivingBase) {
    float f1 = 255.0F;
    float f2 = 255.0F;
    float f3 = 255.0F;
    double d1 = paramEntityLivingBase.lastTickPosX + (paramEntityLivingBase.posX - paramEntityLivingBase.lastTickPosX) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosX;
    double d2 = paramEntityLivingBase.lastTickPosY + (paramEntityLivingBase.posY - paramEntityLivingBase.lastTickPosY) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosY;
    double d3 = paramEntityLivingBase.lastTickPosZ + (paramEntityLivingBase.posZ - paramEntityLivingBase.lastTickPosZ) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosZ;
    render(f1, f2, f3, d1, d2, d3, paramEntityLivingBase.width - 0.23D, paramEntityLivingBase.height + 0.2D);
  }
  
  public void render(float paramFloat1, float paramFloat2, float paramFloat3, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5) {
    RenderingMethods.drawOutlinedEntityESP(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramFloat1, paramFloat2, paramFloat3, 1.0F);
  }
  
  public void onUpdate() {
    if (mc == null || mc.player == null || mc.world == null)
      return; 
    if (isDisabled())
      return; 
    this.removeGlow = true;
    mc.renderGlobal.entityOutlineShader.listShaders.forEach(paramShader -> {
          ShaderUniform shaderUniform = paramShader.getShaderManager().getShaderUniform("Radius");
          if (shaderUniform != null)
            shaderUniform.set((float)this.width.getValDouble()); 
        });
    for (Entity entity : mc.world.loadedEntityList) {
      if (entity == null || entity.isDead)
        return; 
      if (canRenderEntity(entity))
        entity.setGlowing(true); 
      if (!canRenderEntity(entity) && entity.isGlowing())
        entity.setGlowing(false); 
      entity.setGlowing(true);
    } 
  }
  
  public boolean canRenderEntity(Entity paramEntity) {
    boolean bool = false;
    if (paramEntity instanceof EntityPlayer && this.players.getValBoolean())
      bool = true; 
    if (EntityUtil.isFriendlyMob(paramEntity) && this.animals.getValBoolean())
      bool = true; 
    if (EntityUtil.isHostileMob(paramEntity) && this.monsters.getValBoolean())
      bool = true; 
    if (isVehicle(paramEntity) && this.vehicles.getValBoolean())
      bool = true; 
    if (paramEntity instanceof net.minecraft.entity.item.EntityItem && this.items.getValBoolean())
      bool = true; 
    if (paramEntity instanceof net.minecraft.entity.item.EntityEnderCrystal && this.others.getValBoolean())
      bool = true; 
    return bool;
  }
  
  private boolean isValidType(EntityLivingBase paramEntityLivingBase) {
    return ((this.players.getValBoolean() && paramEntityLivingBase instanceof EntityPlayer) || (this.monsters.getValBoolean() && (paramEntityLivingBase instanceof net.minecraft.entity.monster.EntityMob || paramEntityLivingBase instanceof net.minecraft.entity.monster.EntitySlime)) || (this.animals.getValBoolean() && (paramEntityLivingBase instanceof net.minecraft.entity.passive.IAnimals || paramEntityLivingBase instanceof net.minecraft.entity.passive.EntityVillager || paramEntityLivingBase instanceof net.minecraft.entity.monster.EntityGolem)));
  }
  
  private boolean isValid(EntityLivingBase paramEntityLivingBase) {
    return (mc.player != paramEntityLivingBase && paramEntityLivingBase.getEntityId() != -1488 && isValidType(paramEntityLivingBase) && paramEntityLivingBase.isEntityAlive());
  }
  
  public ESP() {
    super("ESP", 0, Category.RENDER);
  }
}
