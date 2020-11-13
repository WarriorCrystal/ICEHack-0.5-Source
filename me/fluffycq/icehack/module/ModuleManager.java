//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import me.fluffycq.icehack.events.RenderEvent;
import me.fluffycq.icehack.util.ICERenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.reflections.Reflections;

public class ModuleManager {
  public Module getModule(String paramString) {
    Module module = null;
    for (Module module1 : this.moduleList) {
      if (module1.name.equalsIgnoreCase(paramString)) {
        module = module1;
        break;
      } 
    } 
    return module;
  }
  
  public void onWorldRender(RenderWorldLastEvent paramRenderWorldLastEvent) {
    (Minecraft.getMinecraft()).profiler.startSection("icehack");
    (Minecraft.getMinecraft()).profiler.startSection("setup");
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.shadeModel(7425);
    GlStateManager.disableDepth();
    GlStateManager.glLineWidth(1.0F);
    Vec3d vec3d = getInterpolatedPos((Entity)(Minecraft.getMinecraft()).player, paramRenderWorldLastEvent.getPartialTicks());
    RenderEvent renderEvent = new RenderEvent((Tessellator)ICERenderer.INSTANCE, vec3d);
    renderEvent.resetTranslation();
    (Minecraft.getMinecraft()).profiler.endSection();
    getEnabledModules().stream().filter(paramModule -> paramModule.isEnabled()).forEach(paramModule -> {
          (Minecraft.getMinecraft()).profiler.startSection(paramModule.getName());
          paramModule.onWorld(paramRenderEvent);
          (Minecraft.getMinecraft()).profiler.endSection();
        });
    (Minecraft.getMinecraft()).profiler.startSection("release");
    GlStateManager.glLineWidth(1.0F);
    GlStateManager.shadeModel(7424);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
    GlStateManager.enableDepth();
    GlStateManager.enableCull();
    ICERenderer.releaseGL();
    (Minecraft.getMinecraft()).profiler.endSection();
    (Minecraft.getMinecraft()).profiler.endSection();
  }
  
  public ModuleManager() {
    Reflections reflections = new Reflections("me.fluffycq.icehack.module.modules", new org.reflections.scanners.Scanner[0]);
    Set set = reflections.getSubTypesOf(Module.class);
    for (Class<Module> clazz : (Iterable<Class<Module>>)set) {
      try {
        this.moduleList.add(clazz.newInstance());
      } catch (InstantiationException instantiationException) {
        System.out.println(String.valueOf((new StringBuilder()).append("[ICEHack] Cannot create class instance of ").append(clazz.getName())));
      } catch (IllegalAccessException illegalAccessException) {
        System.out.println(String.valueOf((new StringBuilder()).append("[ICEHack] Cannot access Module class ").append(clazz.getName())));
      } 
    } 
    Comparator<Module> comparator = new Comparator<Module>() {
        public int compare(Module param1Module1, Module param1Module2) {
          return param1Module1.getName().compareToIgnoreCase(param1Module2.getName());
        }
      };
    Collections.sort(this.moduleList, comparator);
  }
  
  public ArrayList<Module> getEnabledModules() {
    ArrayList<Module> arrayList = new ArrayList();
    for (Module module : this.moduleList) {
      if (module.getState())
        arrayList.add(module); 
    } 
    return arrayList;
  }
  
  public static Vec3d getInterpolatedPos(Entity paramEntity, float paramFloat) {
    return (new Vec3d(paramEntity.lastTickPosX, paramEntity.lastTickPosY, paramEntity.lastTickPosZ)).add(getInterpolatedAmount(paramEntity, paramFloat));
  }
  
  public static Vec3d getInterpolatedAmount(Entity paramEntity, double paramDouble) {
    return getInterpolatedAmount(paramEntity, paramDouble, paramDouble, paramDouble);
  }
  
  public static Vec3d getInterpolatedAmount(Entity paramEntity, Vec3d paramVec3d) {
    return getInterpolatedAmount(paramEntity, paramVec3d.x, paramVec3d.y, paramVec3d.z);
  }
  
  public boolean hasModules(Category paramCategory) {
    ArrayList<Module> arrayList = new ArrayList();
    for (Module module : this.moduleList) {
      if (module.getCategory().equals(paramCategory))
        arrayList.add(module); 
    } 
    return !(arrayList.size() == 0);
  }
  
  public static Vec3d getInterpolatedAmount(Entity paramEntity, double paramDouble1, double paramDouble2, double paramDouble3) {
    return new Vec3d((paramEntity.posX - paramEntity.lastTickPosX) * paramDouble1, (paramEntity.posY - paramEntity.lastTickPosY) * paramDouble2, (paramEntity.posZ - paramEntity.lastTickPosZ) * paramDouble3);
  }
  
  public ArrayList<Module> getModulesByCategory(Category paramCategory) {
    ArrayList<Module> arrayList = new ArrayList();
    for (Module module : this.moduleList) {
      if (module.getCategory().equals(paramCategory))
        arrayList.add(module); 
    } 
    return arrayList;
  }
}
