package me.fluffycq.icehack.module;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.config.Configuration;
import me.fluffycq.icehack.events.RenderEvent;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Module {
  public void setKey(int paramInt) {
    Module module = ICEHack.fevents.moduleManager.getModule(this.name);
    if (module != null)
      for (Setting setting : ICEHack.setmgr.getSettingsByMod(this)) {
        if (setting.isBind()) {
          this.key = paramInt;
          setting.setValKey(paramInt);
        } 
      }  
  }
  
  public void subscribeState(boolean paramBoolean) {
    if (paramBoolean) {
      onEnable();
      ICEHack.EVENT_BUS.subscribe(this);
    } else if (!paramBoolean) {
      onDisable();
      ICEHack.EVENT_BUS.unsubscribe(this);
    } 
  }
  
  public void onKey(int paramInt) {
    if (paramInt == getKey()) {
      setState(!getState());
      onToggle(getState());
    } 
  }
  
  public void setModInfo(String paramString) {
    this.modInfo = paramString;
  }
  
  public void onWorldTick(TickEvent.WorldTickEvent paramWorldTickEvent) {}
  
  public boolean getState() {
    return this.state;
  }
  
  public void onWorld(RenderEvent paramRenderEvent) {}
  
  public void load() {
    for (Setting setting : ICEHack.setmgr.getSettingsByMod(this)) {
      if (setting.isBind())
        this.key = setting.getKeyBind(); 
      if (setting.getName().equalsIgnoreCase("Visible"))
        this.visible = setting.getValBoolean(); 
    } 
  }
  
  public boolean isEnabled() {
    return this.state;
  }
  
  public void addSetting(Setting paramSetting) {
    ICEHack.setmgr.rSetting(paramSetting);
  }
  
  public void onToggle(boolean paramBoolean) {}
  
  public Category getCategory() {
    return this.category;
  }
  
  public void setState(boolean paramBoolean) {
    this.state = paramBoolean;
    subscribeState(paramBoolean);
  }
  
  public Setting getSetting(String paramString) {
    return ICEHack.setmgr.getSettingByMod(paramString, this);
  }
  
  public void enable() {
    this.state = true;
  }
  
  public void onEnable() {}
  
  public void onDisable() {}
  
  public String getName() {
    return this.name;
  }
  
  public boolean isVisible() {
    return getSetting("Visible").getValBoolean();
  }
  
  public String getModInfo() {
    return this.modInfo;
  }
  
  public void disable() {
    this.state = false;
  }
  
  public boolean isDisabled() {
    return !this.state;
  }
  
  public void onRender() {}
  
  public void onUpdate() {}
  
  public Module(String paramString, int paramInt, Category paramCategory) {
    this.key = paramInt;
    this.category = paramCategory;
    this.name = paramString;
    getName();
    Setting setting1 = new Setting("Bind", this, paramInt);
    Setting setting2 = new Setting("Visible", this, true);
    load();
  }
  
  public int getKey() {
    return this.key;
  }
}
